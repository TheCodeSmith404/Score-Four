package com.tcs.games.score4.data.defaults

import androidx.compose.ui.graphics.Color
import com.tcs.games.score4.R
import com.tcs.games.score4.model.gamesettings.CardInfoAdapter

object DefaultCardOptions {
    private val cardColors: Map<Int, Color> = mapOf(
        1 to Color(0xFFFF6B6B), // Soft Red
        2 to Color(0xFFFFD93D), // Warm Yellow
        3 to Color(0xFF6BCB77), // Fresh Green
        4 to Color(0xFF4D96FF), // Sky Blue
        5 to Color(0xFFFF6F91), // Coral Pink
        6 to Color(0xFF845EC2), // Purple Haze
        7 to Color(0xFF00797F), // Medium Dark Teal
        8 to Color(0xFFFFC75F)  // Sunset Orange
    )

    fun getColor(index: Int): Color {
        return cardColors[index] ?: Color.Transparent
    }

    private fun getDefaultNames(key: Int): String {
        val defaultNames: Map<Int, String> = mapOf(
            1 to "Hammer",
            2 to "Queen",
            3 to "Rich",
            4 to "Coins",
            5 to "Snow",
            6 to "Big Snow",
            7 to "You",
            8 to "Slime"
        )
        return defaultNames[key] ?: "Hammer"
    }

    val defaultCards: MutableList<CardInfoAdapter> = mutableListOf(
        CardInfoAdapter("00", getDefaultNames(1), 1, 0, 1, false),
        CardInfoAdapter("01", getDefaultNames(2), 2, -1, 2, false),
        CardInfoAdapter("10", getDefaultNames(3), 3, -2, 3, false),
        CardInfoAdapter("11", getDefaultNames(4), 4, -3, 4, false)
    )

    fun getIconRes(key: Int): Int {
        val map = mapOf(
            1 to R.drawable.cards_club,
            2 to R.drawable.cards_heart,
            3 to R.drawable.cards_diamond,
            4 to R.drawable.cards_spade,
            5 to R.drawable.star_three_points,
            6 to R.drawable.star_four_points,
            7 to R.drawable.star,
            8 to R.drawable.hexagram,
            9 to R.drawable.chess_pawn,
            10 to R.drawable.chess_knight,
            11 to R.drawable.chess_bishop,
            12 to R.drawable.chess_rook,
            13 to R.drawable.chess_queen,
            14 to R.drawable.chess_king,
            15 to R.drawable.car,
            16 to R.drawable.ferry
        )
        return map[key] ?: R.drawable.star
    }
}