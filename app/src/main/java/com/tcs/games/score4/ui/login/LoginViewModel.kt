package com.tcs.games.score4.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.model.UserData
import com.tcs.games.score4.utils.convertors.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager,
    private val application: Application
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _loadingMessage = MutableStateFlow<String?>(null)
    val loadingMessage: StateFlow<String?> = _loadingMessage.asStateFlow()

    init {
        auth.currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                userRepository.getUser(firebaseUser.uid, application.applicationContext)
                _user.value = auth.currentUser
            }
        }
    }

    fun signInWithGoogle(token: String) {
        viewModelScope.launch {
        try {
            _loadingMessage.value = "Retrieving Credentials"
            val firebaseCredential = GoogleAuthProvider.getCredential(token, null)
            val authResult = auth.signInWithCredential(firebaseCredential).await()
            val user = authResult.user

            if (user != null) {
                val id = user.uid
                val exists = userRepository.checkUserExists(id)
                if (exists) {
                    _loadingMessage.value = "Getting Account Details"
                    userRepository.getUser(id, application.applicationContext)
                } else {
                    _loadingMessage.value = "Creating Account"
                    val currentTime = TimeUtils.getCurrentTimeInMillis()
                    val pair = userRepository.getStats()
                    val data = UserData(
                        id, generateShownId(id, currentTime), user.displayName ?: "User",
                        user.email ?: "", "", currentTime, currentTime, currentTime,
                        pair.first, pair.second, false, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, true
                    )
                    userRepository.addUser(data)
                    preferenceManager.isSignedIn = true
                    preferenceManager.profileUrl = null
                    preferenceManager.userName = user.displayName ?: "User"
                }
                _user.value = auth.currentUser
                _loadingMessage.value = null
            } else {
                _loadingMessage.value = "Error"
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Sign-in failed: ${e.message}")
            _loadingMessage.value = "Error"
        }
    }
}

    fun signInAnonymousLy() {
        viewModelScope.launch {
            try {
                val authResult = auth.signInAnonymously().await()
                val firebaseUser = authResult.user!!
                val id = firebaseUser.uid
                _loadingMessage.value = "Creating Account"
                
                val currentTime = TimeUtils.getCurrentTimeInMillis()
                val pair = userRepository.getStats()
                val data = UserData(
                    id, generateShownId(id, currentTime), "User_${pair.first}",
                    "Please Sign Up", "", currentTime, currentTime, currentTime,
                    pair.first, pair.second, false, 0, 6, 0, 0, 0, 0, 0, 0, 0, 0, false
                )
                userRepository.addUser(data)
                preferenceManager.isSignedIn = false
                preferenceManager.profileUrl = null
                preferenceManager.userName = data.playerName
                _user.value = auth.currentUser
                _loadingMessage.value = null
            } catch (e: Exception) {
                Log.e("SignIn", "Anonymous login failed: ${e.message}")
                _loadingMessage.value = "Error"
            }
        }
    }

    private fun generateShownId(id: String, time: Long): String {
        val timeStr = time.toString().substring(minOf(4, time.toString().length))
        val subId = id.substring(0, minOf(10, id.length))
        return "${subId}_$timeStr"
    }
}