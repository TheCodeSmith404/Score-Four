package com.tcs.games.score4.data.defaults

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.tcs.games.score4.R
import com.tcs.games.score4.model.gamesettings.CardInfoAdapter

object DefaultCardOptions {
    private val cardColors: Map<Int, Int> = mapOf(
        1 to Color.argb(255, 255, 107, 107), // Soft Red with full opacity
        2 to Color.argb(255, 255, 217, 61),  // Warm Yellow with full opacity
        3 to Color.argb(255, 107, 203, 119), // Fresh Green with full opacity
        4 to Color.argb(255, 77, 150, 255),  // Sky Blue with full opacity
        5 to Color.argb(255, 255, 111, 145), // Coral Pink with full opacity
        6 to Color.argb(255, 132, 94, 194),  // Purple Haze with full opacity
        7 to Color.argb(255, 0, 121, 127),  // Medium Dark Teal with full opacity
        8 to Color.argb(255, 255, 199, 95)    // Sunset Orange with full opacity
    )

    private val cardColorsLight: Map<Int, Int> = mapOf(
        1 to Color.argb(102, 255, 107, 107), // Soft Red with full opacity
        2 to Color.argb(102, 255, 217, 61),  // Warm Yellow with full opacity
        3 to Color.argb(102, 107, 203, 119), // Fresh Green with full opacity
        4 to Color.argb(102, 77, 150, 255),  // Sky Blue with full opacity
        5 to Color.argb(102, 255, 111, 145), // Coral Pink with full opacity
        6 to Color.argb(102, 132, 94, 194),  // Purple Haze with full opacity
        7 to Color.argb(102, 0, 121, 127),  // Purple 500 with full opacity
        8 to Color.argb(102, 255, 199, 95)   // Lighter Sunset Orange
    )
    fun getColor(index: Int, useLight: Boolean): Int {
        return if (useLight) {
            cardColorsLight[index] ?: Color.TRANSPARENT // Fallback to transparent if key not found
        } else {
            cardColors[index] ?: Color.TRANSPARENT // Fallback to transparent if key not found
        }
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
        return defaultNames[key] ?: "Hammer" // Default name if key is not found
    }

    val defaultCards: MutableList<CardInfoAdapter> = mutableListOf(
        CardInfoAdapter("00", getDefaultNames(1), 1, 0, 1,false),
        CardInfoAdapter("01", getDefaultNames(2), 2, -1, 2,false),
        CardInfoAdapter("10", getDefaultNames(3), 3, -2, 3,false),
        CardInfoAdapter("11", getDefaultNames(4), 4, -3, 4,false)
    )

    fun getDrawable(context: Context,key:Int): Drawable {
        val map=mapOf(
            1 to ContextCompat.getDrawable(context, R.drawable.cards_club), // v1 drawable
            2 to ContextCompat.getDrawable(context, R.drawable.cards_heart), // v2 drawable
            3 to ContextCompat.getDrawable(context, R.drawable.cards_diamond), // v3 drawable
            4 to ContextCompat.getDrawable(context, R.drawable.cards_spade), // v4 drawable
            5 to ContextCompat.getDrawable(context, R.drawable.star_three_points), // v5 drawable
            6 to ContextCompat.getDrawable(context, R.drawable.star_four_points), // v6 drawable
            7 to ContextCompat.getDrawable(context, R.drawable.star), // v7 drawable
            8 to ContextCompat.getDrawable(context, R.drawable.hexagram), // v8 drawable
            9 to ContextCompat.getDrawable(context, R.drawable.chess_pawn),
            10 to ContextCompat.getDrawable(context, R.drawable.chess_knight),
            11 to ContextCompat.getDrawable(context, R.drawable.chess_bishop),
            12 to ContextCompat.getDrawable(context, R.drawable.chess_rook),
            13 to ContextCompat.getDrawable(context, R.drawable.chess_queen),
            14 to ContextCompat.getDrawable(context, R.drawable.chess_king),
            15 to ContextCompat.getDrawable(context, R.drawable.car),
            16 to ContextCompat.getDrawable(context, R.drawable.ferry),
        )
        return map[key]?:ContextCompat.getDrawable(context,R.drawable.star)!!
    }

    fun getColoursRadioButtonId(id:Int):Int{
        val radioButtonMap = mapOf(
            1 to R.id.card_color_option_1,
            2 to R.id.card_color_option_2,
            3 to R.id.card_color_option_3,
            4 to R.id.card_color_option_4,
            5 to R.id.card_color_option_5,
            6 to R.id.card_color_option_6,
            7 to R.id.card_color_option_7,
            8 to R.id.card_color_option_8
        )
        return radioButtonMap[id]?:R.id.card_color_option_1
    }

    fun getTurnsRadioButtonId(id:Int):Int{
        val radioGroupMap= mapOf(
            1 to R.id.seconds_10,
            2 to R.id.seconds_20,
            3 to R.id.seconds_30,
            4 to R.id.seconds_50,
        )
        return radioGroupMap[id]?:R.id.seconds_10
    }

}