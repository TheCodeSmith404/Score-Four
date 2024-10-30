package model

data class UserData(
    val authId:String,
    val generatedId:String,
    var playerName:String,
    var lastNameChangedTime:Long,
    val gMail:String,
    val timeCreated:Long,
    var lastLoginTime:Long,
    val isOG:Boolean=false,
    var isImageUploaded:Boolean=false,
    var numberGamesPlayed:Int=0,
    var numberGamesWon:Int=0,
    var numberGamesLost:Int=0,
    var numberGamesAbandoned:Int=0,
    var lastGamePlayed:Long=0,
    var lastGameAbandonedTime:Long=0,
    var lastGameAbandonedTimeAsHost:Long=0,
    var status:Int=0,
    )