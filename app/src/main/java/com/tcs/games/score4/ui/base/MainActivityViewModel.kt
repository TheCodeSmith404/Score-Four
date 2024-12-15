package com.tcs.games.score4.ui.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    firebaseAuth: FirebaseAuth,
    firebaseDatabase: FirebaseDatabase,
):ViewModel() {
    private val connectedRef: DatabaseReference = firebaseDatabase.getReference(".info/connected")
    private val userStatusRef: DatabaseReference
    private val _userStatus = MutableLiveData<String>()
    val userStatus: LiveData<String> get()=_userStatus
    init {
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            userStatusRef = firebaseDatabase.getReference("users/$uid/status")
            monitorConnectionStatus()
        } else {
            userStatusRef=firebaseDatabase.getReference("users")
            _userStatus.postValue("not_registered")
        }
    }
    private fun monitorConnectionStatus() {
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isConnected = snapshot.getValue(Boolean::class.java) ?: false
                val onlineStatus = mapOf(
                    "status" to "true",
                    "lastChanged" to ServerValue.TIMESTAMP
                )
                val offlineStatus = mapOf(
                    "status" to "false",
                    "lastChanged" to ServerValue.TIMESTAMP
                )
                if (isConnected) {
                    Log.d("Reconnection", "Connected to Firebase")
                    userStatusRef.setValue(onlineStatus)
                    _userStatus.postValue("online")
                } else {
                    userStatusRef.setValue(offlineStatus)
                    _userStatus.postValue("offline")
                }
                userStatusRef.onDisconnect().setValue(offlineStatus)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Reconnection", "Listener cancelled: ${error.message}")
            }
        })
    }
}