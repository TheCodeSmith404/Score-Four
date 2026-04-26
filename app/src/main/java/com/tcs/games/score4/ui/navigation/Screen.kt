package com.tcs.games.score4.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Home : Screen("home")
    object GameSettings : Screen("game_settings")
    object WaitingRoom : Screen("waiting_room/{roomId}") {
        fun createRoute(roomId: String) = "waiting_room/$roomId"
    }
    object GameRoom : Screen("game_room/{roomId}") {
        fun createRoute(roomId: String) = "game_room/$roomId"
    }
    object SelectImage : Screen("select_image/{sourceId}/{imageName}") {
        fun createRoute(sourceId: Int, imageName: String) = "select_image/$sourceId/$imageName"
    }
    object UploadedImages : Screen("uploaded_images")
    object GameFinished : Screen("game_finished/{winner}") {
        fun createRoute(winner: Int) = "game_finished/$winner"
    }
}
