package data.repository

import com.google.firebase.firestore.FirebaseFirestore
import data.PreferenceManager
import kotlinx.coroutines.tasks.await
import model.gamesettings.GameKeys
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JoinGameRepository @Inject constructor(
    val firestore: FirebaseFirestore,
    val userRepository: UserRepository,
    val preferenceManager: PreferenceManager,
    ) {

    suspend fun verifyUser(id: String, password: String): Result<String?> {
        val idPassRef = firestore.collection("game_room_ids")
        val documentId = "${id.uppercase(Locale.getDefault())}$password"

        return try {
            // Retrieve the document
            val documentSnapshot = idPassRef.document(documentId).get().await()
            if (documentSnapshot.exists()) {
                // Convert to Keys data class
                val keys = documentSnapshot.toObject(GameKeys::class.java)
                if (keys != null) {
                    if(keys.id == id&&keys.pass==password){
                        Result.success(keys.gameId)
                    }else{
                        Result.success(null)
                    }
                } else {
                    Result.failure(Exception("Document found but failed to parse"))
                }
            } else {
                Result.failure(Exception("Document does not exist"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun registerUser(){

    }
}