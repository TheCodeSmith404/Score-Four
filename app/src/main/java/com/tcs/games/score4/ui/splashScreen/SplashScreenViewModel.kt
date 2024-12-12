package com.tcs.games.score4.ui.splashScreen

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tcs.games.score4.data.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tcs.games.score4.data.repository.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.tcs.games.score4.model.UserData
import javax.inject.Inject

@HiltViewModel
class SplashScreenViewModel @Inject constructor(private val userRepository: UserRepository, private val preferenceManager: PreferenceManager, private val application: Application):ViewModel() {
    private val auth= FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _userData = MutableLiveData<UserData?>()
    val userData: LiveData<UserData?> = _userData

    init {
        var isReady: Task<UserData?>
        if(auth.currentUser!=null) {
            @Suppress("OPT_IN_USAGE")
            GlobalScope.launch {
                isReady = userRepository.getUser(auth.currentUser!!.uid,application.applicationContext)
                isReady.await()
                _userData.postValue(isReady.result)
            }
        }
        _user.value = auth.currentUser
    }
}