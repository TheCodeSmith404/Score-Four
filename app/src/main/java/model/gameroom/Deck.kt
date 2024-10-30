package model.gameroom

data class Deck(
    var currentlyPlaying:Char,
    var playerA:MutableList<Int>,
    var playerB:MutableList<Int>,
    var playerC:MutableList<Int>,
    var playerD:MutableList<Int>,
)