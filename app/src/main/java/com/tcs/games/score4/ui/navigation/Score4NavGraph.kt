package com.tcs.games.score4.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tcs.games.score4.ui.home.HomeScreen
import com.tcs.games.score4.ui.home.HomeViewModel
import com.tcs.games.score4.ui.login.LoginScreen
import com.tcs.games.score4.ui.login.LoginViewModel
import com.tcs.games.score4.ui.splashScreen.SplashScreen
import com.tcs.games.score4.ui.splashScreen.SplashScreenViewModel
import com.tcs.games.score4.ui.gameroom.GameRoomScreen
import com.tcs.games.score4.ui.gameroom.GameRoomViewModel
import com.tcs.games.score4.ui.gamesettingfragment.GameSettingsScreen
import com.tcs.games.score4.ui.gamesettingfragment.GameSettingViewModel
import com.tcs.games.score4.ui.waitingroom.WaitingRoomScreen
import com.tcs.games.score4.ui.waitingroom.WaitingRoomViewModel

@Composable
fun Score4NavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Splash.route) {
        composable(Screen.Splash.route) {
            val viewModel: SplashScreenViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = viewModel,
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = hiltViewModel()
            val userData by viewModel.userFlow.collectAsState()
            
            HomeScreen(
                userData = userData,
                onCreateGame = { navController.navigate(Screen.GameSettings.route) },
                onJoinGame = { /* Navigate to Join Dialog */ },
                onProfileClick = { /* Navigate to Profile Dialog */ }
            )
        }

        composable(Screen.GameSettings.route) {
            val viewModel: GameSettingViewModel = hiltViewModel()
            GameSettingsScreen(
                viewModel = viewModel,
                onNavigateToWaitingRoom = { 
                    navController.navigate(Screen.WaitingRoom.createRoute("ROOM_ID")) // TODO: Pass actual ID
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.WaitingRoom.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) {
            val viewModel: WaitingRoomViewModel = hiltViewModel()
            WaitingRoomScreen(
                viewModel = viewModel,
                onNavigateToGameRoom = { roomId ->
                    navController.navigate(Screen.GameRoom.createRoute(roomId))
                },
                onNavigateToGameDetails = { /* Show Dialog */ },
                onBack = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.GameRoom.route,
            arguments = listOf(navArgument("roomId") { type = NavType.StringType })
        ) {
            val viewModel: GameRoomViewModel = hiltViewModel()
            GameRoomScreen(
                // ... Existing GameRoomScreen parameters
                onBack = { navController.navigateUp() }
            )
        }
    }
}
