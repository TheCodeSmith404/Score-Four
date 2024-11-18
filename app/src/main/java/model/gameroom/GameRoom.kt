package model.gameroom

data class GameRoom(
    val gameId:String="",
    val gamePassword:String="",
    var hostId:String="",
    var numberOfPlayers:Int=0,
    var numberOfBots:Int=0,
    var isRunning:Boolean=false,
    var numberOfPlayersReady:Int=0,
    var createdAt:Long=0L,
    var startedAt:Long=0L,
    val players:MutableList<PlayersStatus> =mutableListOf(),
    val cards:MutableList<CardInfo> = mutableListOf(),
    val timePerTurns:Int=10,
)
