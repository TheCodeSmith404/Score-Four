package com.tcs.games.score4.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.utils.convertors.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tcs.games.score4.data.repository.UserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.tcs.games.score4.model.UserData
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository, private val preferenceManager: PreferenceManager,private val application: Application) : ViewModel() {
    private val auth= FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    private val _loadingMessage=MutableLiveData<String>()
    val loadingMessage:LiveData<String> =_loadingMessage


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
        try {
            _loadingMessage.value="Retrieving Credentials"


            val firebaseCredential = GoogleAuthProvider.getCredential(token, null)

            // Sign in with Firebase
            val authResult = auth.signInWithCredential(firebaseCredential).await()

            val user = authResult.user
            if (user != null) {
                viewModelScope.launch {
                    try {
                        val id = user.uid

                        // Check if the user exists in the repository
                        val exists = userRepository.checkUserExists(id)
                        if (exists) {
                            _loadingMessage.value="Getting Account Details"


                            Log.d("LoginViewModel", "User Exists")
                            userRepository.getUser(id, application.applicationContext)
                        } else {
                            _loadingMessage.value="Creating Account"


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

                            Log.d("LoginViewModel", "New user successfully registered and signed in.")
                        }
                        // Successfully signed in, update UI
                        _user.value = auth.currentUser
                        Log.d("LoginViewModel", "Sign-in successful!")

                    } catch (e: Exception) {
                        _loadingMessage.value="Error"


                        Log.e("LoginViewModel", "Error processing user data: ${e.message}")
                        _user.value = null
                    }
                }
            } else {
                _loadingMessage.value="Error"


                Log.e("LoginViewModel", "Sign-in failed: No user returned.")
                _user.value = null
            }
        } catch (e: Exception) {
            _loadingMessage.value="Error"


            Log.e("LoginViewModel", "Firebase sign-in failed: ${e.message}")
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
                    _loadingMessage.value="Creating Account"


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

                _loadingMessage.value="Error"


                Log.e("SignIn","Anonymous Log in failed ${task.exception?.message}")
                _user.value=null
            }
        }
    }
}