package model.gameroom

data class PlayersStatus(
    var bot:Boolean=false,
    val firebaseId:String="",
    val playerId:String="",
    val playerName:String="",
    val playerProfile:String="",
    val og:Boolean=false,
    val numberOfGamesPlayed:Int=0,
    val numberOfGamesWon:Int=0,
    val dateJoined:Long=0,
    var ready:Boolean=false,
    var active:Boolean=false,
)
