package model.gameroom

data class PlayersStatus(
    var isBot:Boolean,
    val firebaseId:String,
    val playerId:String,
    val playerName:String,
    val isOG:Boolean,
    val numberOfGamesPlayed:Int,
    val numberOfGamesWon:Int,
    val dateJoined:Long,
    var isReady:Boolean,
    var isActive:Boolean,
)
