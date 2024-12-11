package com.tcs.games.score4.ui.login

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import data.PreferenceManager
import data.repository.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import model.UserData
import com.tcs.games.score4.utils.convertors.TimeUtils
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository, private val preferenceManager: PreferenceManager,private val application: Application) : ViewModel() {
    private val auth= FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user


    init {
        var isReady: Task<UserData?>
        if(auth.currentUser!=null) {
            @Suppress("OPT_IN_USAGE")
            GlobalScope.launch {
                isReady = userRepository.getUser(auth.currentUser!!.uid,application.applicationContext)
                isReady.await()
            }
        }
        _user.value = auth.currentUser
    }
    suspend fun signInWithGoogle(token: String) {
        val firebaseCredential = GoogleAuthProvider.getCredential(token, null)

        // Sign in with Firebase and check if the task is successful
        val task = auth.signInWithCredential(firebaseCredential).await() // await() to ensure completion before proceeding
        if (task.user != null) {
            viewModelScope.launch {
                val user = auth.currentUser!!
                val id = user.uid

                // Check if the user exists in the repository
                val exists = userRepository.checkUserExists(id)
                if (exists) {
                    Log.d("LoginViewModel", "User Exists")
                    userRepository.getUser(id,application.applicationContext)
                } else {
                    Log.d("LoginViewModel", "User Does Not Exist")

                    val currentTime = TimeUtils.getCurrentTimeInMillis()
                    val pair = userRepository.getStats().await()

                    // Create UserData object with details
                    val data=UserData(id,generateShownId(id,currentTime),user.displayName.toString(),user.email.toString(),"",currentTime,currentTime,currentTime,pair.first,pair.second,false,0,6,0,0,0,0,0,0,0,0,true)
                    val uploadData=userRepository.addUser(data)
                    preferenceManager.isSignedIn=true
                    preferenceManager.profileUrl=null
                    preferenceManager.userName=user.displayName.toString()
                    // Use await() to block further execution until user is added
                    uploadData.await()
                }

                // Set the user value after repository actions are completed
                _user.value = auth.currentUser
            }
        } else {
            _user.value = null
        }
    }
    private fun generateShownId(id:String,time:Long):String{
        val timeStr=time.toString().substring(4)
        val subId=id.substring(0,10)
        return "${subId}_$timeStr"
    }

    fun signInAnonymousLy(){
        auth.signInAnonymously().addOnCompleteListener{task->
            if(task.isSuccessful){
                val user = auth.currentUser!!
                val id = user.uid
                viewModelScope.launch {
                    val currentTime = TimeUtils.getCurrentTimeInMillis()
                    val pair = userRepository.getStats().await()
                    val data=UserData(id,generateShownId(id,currentTime),"User_${pair.first}","Please Sign Up","",currentTime,currentTime,currentTime,pair.first,pair.second,false,0,6,0,0,0,0,0,0,0,0,false)
                    val uploadData=userRepository.addUser(data)
                    preferenceManager.isSignedIn=false
                    preferenceManager.profileUrl=null
                    preferenceManager.userName=data.playerName
                    uploadData.await()
                    _user.value=auth.currentUser
                }
            }else{
                Log.e("SignIn","Anonymous Log in failed ${task.exception?.message}")
                _user.value=null
            }
        }
    }
}