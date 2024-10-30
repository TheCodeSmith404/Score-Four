package model.gameroom

data class GameRoom(
    val gameId:String,
    val gamePassword:String,
    var hostId:String,
    var numberOfPlayers:Int,
    var numberOfBots:Int,
    var isRunning:Boolean,
    var numberOfPlayersReady:Int,
    val players:MutableList<PlayersStatus>,
    val cards:MutableList<CardInfo>,
    val deck:Deck
)
