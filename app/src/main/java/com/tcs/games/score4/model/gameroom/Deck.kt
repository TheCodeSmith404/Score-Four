package com.tcs.games.score4.model.gameroom

data class Deck(
    var currentlyPlaying:Int=0,
    var playerA:MutableList<String> = mutableListOf(),
    var playerB:MutableList<String> = mutableListOf(),
    var playerC:MutableList<String> = mutableListOf(),
    var playerD:MutableList<String> = mutableListOf(),
)