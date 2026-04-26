package com.tcs.games.score4.data.repository

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.constants.ImageNames
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val revenue = firestore.collection("revenue")
    private val config = firestore.collection("config")

    private val _user = MutableStateFlow<UserData?>(null)
    val userFlow: StateFlow<UserData?> = _user.asStateFlow()
    
    val user: UserData? get() = _user.value

    suspend fun addUser(userData: UserData): Boolean = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(userData.authId).set(userData).await()
            _user.value = userData
            Log.d("UserRepository", "User added successfully")
            true
        } catch (e: Exception) {
            Log.e("UserRepository", "Error adding user", e)
            false
        }
    }

    suspend fun getUser(authId: String, context: Context): UserData? = withContext(Dispatchers.IO) {
        try {
            val document = usersCollection.document(authId).get().await()
            val userData = document.toObject(UserData::class.java)
            _user.value = userData
            
            preferenceManager.isSignedIn = true
            preferenceManager.userName = userData?.playerName ?: "Player"
            
            if (userData?.profileUrl != "none" && (preferenceManager.profileUrl == null || preferenceManager.profileImageChanged)) {
                preferenceManager.profileImageChanged = false
                val uri = ImageUtils.downloadImageFromFirebase(
                    firebaseStorage, "profile_images", userData!!.authId, context,
                    ImageNames.PROFILE.txt, true
                )
                if (uri != null) {
                    preferenceManager.profileUrl = uri
                }
            }
            userData
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user data", e)
            null
        }
    }

    suspend fun updateProfileImage(authId: String, profile: String): Boolean = withContext(Dispatchers.IO) {
        try {
            usersCollection.document(authId).update("profileUrl", profile).await()
            _user.value = _user.value?.copy(profileUrl = profile)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateImageList(list: MutableList<Int>): Boolean = withContext(Dispatchers.IO) {
        try {
            val currentUser = _user.value ?: return@withContext false
            usersCollection.document(currentUser.authId).update(
                "imageData", list,
                "numberImagesUploaded", currentUser.numberImagesUploaded + 1
            ).await()
            _user.value = currentUser.copy(
                imageData = list,
                numberImagesUploaded = currentUser.numberImagesUploaded + 1
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun checkUserExists(authId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val document = usersCollection.document(authId).get().await()
            document.exists()
        } catch (e: Exception) {
            Log.e("UserRepository", "Error checking if user exists", e)
            false
        }
    }

    suspend fun getStats(): Pair<Int, Boolean> = withContext(Dispatchers.IO) {
        try {
            val configDoc = config.document("config").get().await()
            val isOG = configDoc.data?.get("isOG") as? Boolean ?: false

            val updatedPair = firestore.runTransaction { transaction ->
                val playerDoc = transaction.get(revenue.document("player_count"))
                val playerCount = playerDoc.getLong("playerCount") ?: 0L
                val updatedPlayerCount = playerCount + 1
                transaction.update(revenue.document("player_count"), "playerCount", updatedPlayerCount)
                updatedPlayerCount.toInt() to isOG
            }.await()

            updatedPair
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user count", e)
            0 to false
        }
    }

    fun updateGameFinishedStats(userId: String, userWon: Boolean) {
        val field = if (userWon) "numberGamesWon" else "numberGamesLost"
        usersCollection.document(userId).update(field, FieldValue.increment(1))
    }
}
