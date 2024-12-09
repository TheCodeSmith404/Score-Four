package com.tcs.games.score4.utils.gamelogic

object GenerateGameIdPass {
    private fun generateLobbyId(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val usedChars = mutableSetOf<Char>()  // Set to track used characters
        return (1..6)
            .map {
                // Find a random character that has not been used
                val availableChars = chars.filterNot { usedChars.contains(it) }
                val randomChar = availableChars.random()
                usedChars.add(randomChar)  // Mark this character as used
                randomChar
            }
            .joinToString("")
    }
    private fun generateLobbyPassword(): String {
        val digits = "0123456789"
        return (1..4)
            .map { digits.random() }
            .joinToString("")
    }
    fun getIdPass():Pair<String,String>{
        return Pair("AAAAAA", "0000")
    }
}