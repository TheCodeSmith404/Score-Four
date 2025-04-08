package com.tcs.games.score4.model.messages

import com.google.firebase.Timestamp

data class Message(
    val id:String,
    val hasLinks:Boolean,
    val message:String,
    val timeSent: Timestamp?=null,
    val isRead:Boolean,
)
