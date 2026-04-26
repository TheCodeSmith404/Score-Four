package com.tcs.games.score4.ui.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.tcs.games.score4.ui.navigation.Score4NavGraph
import com.tcs.games.score4.ui.theme.Score4Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Score4Theme {
                Score4NavGraph()
            }
        }
    }
}