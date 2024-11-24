package model

data class UserData(
    val authId:String="",
    val generatedId:String="",
    var playerName:String="",
    val gmail:String="",
    var profileUrl:String="",
    var lastNameChangedTime:Long=0L,
    val timeCreated:Long=0L,
    var lastLoginTime:Long=0L,
    var userNumber:Int=0,
    val isOG:Boolean=false,
    var isImageUploaded:Boolean=false,
    var numberImagesUploaded:Int=0,
    val numberImagesAllowed:Int=6,
    var numberGamesPlayed:Int=0,
    var numberGamesWon:Int=0,
    var numberGamesLost:Int=0,
    var numberGamesAbandoned:Int=0,
    var lastGamePlayed:Long=0,
    var lastGameAbandonedTime:Long=0,
    var lastGameAbandonedTimeAsHost:Long=0,
    var status:Int=0,
    var isSignedIn:Boolean=false,
    var imageData:MutableList<Int> = mutableListOf()
    )