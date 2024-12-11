package com.tcs.games.score4.ui.gameroom

sealed class GameRoomCurrentlyPlayingState {
    sealed class Player1:GameRoomCurrentlyPlayingState(){
        data class Progress(val progress:Int):Player1()
        data object TimeOut:Player1()
    }
    sealed class Player2:GameRoomCurrentlyPlayingState(){
        data class Progress(val progress: Int):Player2()
        data object TimeOut:Player2()
    }
    sealed class Player3:GameRoomCurrentlyPlayingState(){
        data class Progress(val progress: Int):Player3()
        data object TimeOut:Player3()
    }
    sealed class Player4:GameRoomCurrentlyPlayingState(){
        data class Progress(val progress: Int):Player4()
        data object TimeOut:Player4()
    }
}