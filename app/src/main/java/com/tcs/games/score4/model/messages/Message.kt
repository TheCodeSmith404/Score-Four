package com.tcs.games.score4.model.messages

data class Message(
    val id:String,
    val hasLinks:Boolean,
    val message:String,
    val timeSent:Long,
    val isRead:Boolean,
)
