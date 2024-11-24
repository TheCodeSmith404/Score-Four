package model.gameroom

data class PlayersStatus(
    var isBot:Boolean=false,
    val firebaseId:String="",
    val playerId:String="",
    val playerName:String="",
    val playerProfile:String="",
    val isOG:Boolean=false,
    val numberOfGamesPlayed:Int=0,
    val numberOfGamesWon:Int=0,
    val dateJoined:Long=0,
    var isReady:Boolean=false,
    var isActive:Boolean=false,
)
