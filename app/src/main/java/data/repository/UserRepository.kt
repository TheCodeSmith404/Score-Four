package data.repository

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import model.UserData
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection("accounts")
    private val revenue=firestore.collection("revenue")
    private val config=firestore.collection("config")
    var user:UserData?=null

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
    suspend fun getUser(authId: String): Task<UserData?> = withContext(Dispatchers.IO) {
        try {
            val document = usersCollection.document(authId).get().await()
            user=document.toObject(UserData::class.java)
            Log.d("UserRepository", "Data Received")
            Tasks.forResult(user)
        } catch (e: Exception) {
            Log.e("UserRepository", "Error getting user data", e)
            Tasks.forResult(null)
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

}
