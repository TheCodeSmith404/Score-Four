package data.repository

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    private val usersCollection = firestore.collection("accounts")

    fun getUserProfile(userId: String): DocumentReference {
        return usersCollection.document(userId)
    }

    fun updateUserProfile(userId: String, data: Map<String, Any>) {
        usersCollection.document(userId).update(data)
            .addOnSuccessListener {
                Log.d("ProfileRepository", "User profile updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ProfileRepository", "Error updating profile", e)
            }
    }
}