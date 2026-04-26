package com.tcs.games.score4.ui.splashScreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager,
    private val application: Application
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    
    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user.asStateFlow()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData.asStateFlow()

    init {
        auth.currentUser?.let { firebaseUser ->
            viewModelScope.launch {
                val data = userRepository.getUser(firebaseUser.uid, application.applicationContext)
                _userData.value = data
            }
        }
    }
}