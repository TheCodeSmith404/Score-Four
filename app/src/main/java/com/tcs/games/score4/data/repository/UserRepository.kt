package com.tcs.games.score4.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.constants.ImageNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.tcs.games.score4.model.UserData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val preferenceManager: PreferenceManager,
    private val firebaseStorage: FirebaseStorage,
) {

    private val usersCollection = firestore.collection("accounts")
    private val revenue=firestore.collection("revenue")
    private val config=firestore.collection("config")
    var user: UserData?=null

    // Suspend function to add user
    suspend fun addUser(userData: UserData): Task<Boolean> = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userData.authId).set(userData).await()
            user=userData
            Log.d("UserRepository", "User added successfully")
            Tasks.forResult(true)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user", e)
            Tasks.forResult(false)
        }
    }
    // Suspend function to get user data
    suspend fun getUser(authId: String,context: Context): Task<UserData?> = withContext(Dispatchers.IO) {
        try {
            val document = usersCollection.document(authId).get().await()
            user=document.toObject(UserData::class.java)
            Log.d("Download","user Data: ${user.toString()}")
            preferenceManager.isSignedIn=true
            preferenceManager.userName=user?.playerName.toString()
            if(user!!.profileUrl!="none"&&preferenceManager.profileUrl==null||preferenceManager.profileImageChanged){
                Log.d("Download","Starting Download")
                preferenceManager.profileImageChanged=false
                val uri= ImageUtils.downloadImageFromFirebase(firebaseStorage,"profile_images",user!!.authId, context,
                    ImageNames.PROFILE.txt,true)
                if(uri!=null){
                    preferenceManager.profileUrl=uri
                }
            }
            // TODO downloadImage from firebase if available
            Log.d("UserRepository", "Data Received")
            Tasks.forResult(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user data", e)
            Tasks.forResult(null)
        }
    }

    suspend fun updateProfileImage(authId: String, profile: String): Boolean = withContext(
        Dispatchers.IO) {
        try {
            // Update the profile URL in the FireStore document
            usersCollection.document(authId)
                .update("profileUrl", profile)
                .await()
            user?.profileUrl=profile
            // Wait for the operation to complete
            true // Return true if the update succeeds
        } catch (e: Exception) {
            false // Return false if the update fails
        }
    }
    suspend fun updateImageList(list:MutableList<Int>):Boolean = withContext(Dispatchers.IO){
        try{
            usersCollection.document(user!!.authId)
                .update(
                    "imageData",list,
                    "numberImagesUploaded",user!!.numberImagesUploaded+1)
                .await()
            user!!.numberImagesUploaded++
            user?.imageData=list
            true
        }catch (e:Exception){
            false
        }
    }


    // Suspend function to check if user exists
    suspend fun checkUserExists(authId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val document = usersCollection.document(authId).get().await()
            document.exists()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error checking if user exists", e)
            false
        }
    }

    suspend fun getStats(): Task<Pair<Int, Boolean>> = withContext(Dispatchers.IO) {
        try {
            val configDoc = config.document("config").get().await()
            val isOG = configDoc.data?.get("isOG") as? Boolean ?: false // Default to false if null

            // Use a transaction to safely increment the player count
            val updatedPair = firestore.runTransaction { transaction ->
                val playerDoc = transaction.get(revenue.document("player_count"))

                val playerCount = playerDoc.getLong("playerCount") ?: 0L
                val updatedPlayerCount = playerCount + 1

                // Update player count in the transaction
                transaction.update(revenue.document("player_count"), "playerCount", updatedPlayerCount)

                // Return updated player count and isOG as a Pair
                updatedPlayerCount.toInt() to isOG
            }.await()

            return@withContext Tasks.forResult(updatedPair)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user count", e)
            return@withContext Tasks.forResult(0 to false) // Return default values on error
        }
    }
    fun updateGameFinishedStats(userId:String,userWon:Boolean){
        val field=if(userWon) "numberGamesWon" else "NumberGamesLost"
        usersCollection.document(userId).update(field, FieldValue.increment(1))
            .addOnSuccessListener {
                Log.d("updateStats","userWon: $userWon and update is successful")
            }
            .addOnFailureListener{
                Log.d("updateStats","userWon: $userWon and update is unsuccessful")
            }

    }

}
