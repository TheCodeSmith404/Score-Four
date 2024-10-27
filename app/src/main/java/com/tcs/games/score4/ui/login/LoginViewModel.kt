package com.tcs.games.score4.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {
    private val auth= FirebaseAuth.getInstance()
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> = _user

    init {
        // Check if the user is already signed in
        _user.value = auth.currentUser
    }
    fun signInWithGoogle(token:String){
        val firebaseCredential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(firebaseCredential).addOnCompleteListener(){task->
            if(task.isSuccessful){
                _user.value=auth.currentUser
            }else{
                _user.value=null
            }
        }
    }
    fun signInAnonymousLy(){
        auth.signInAnonymously().addOnCompleteListener{task->
            if(task.isSuccessful){
                _user.value=auth.currentUser
            }else{
                Log.e("SignIn","Anonymous Log in failed ${task.exception?.message}")
                _user.value=null
            }
        }
    }
}