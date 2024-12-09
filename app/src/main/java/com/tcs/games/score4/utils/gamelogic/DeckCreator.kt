package com.tcs.games.score4.utils.gamelogic

import com.tcs.games.score4.model.gameroom.Deck

object DeckCreator {
    private val cardsId= mutableListOf(
        "a1","a2","a3","a4",
        "b1","b2","b3","b4",
        "c1","c2","c3","c4",
        "d1","d2","d3","d4")
    fun createDeck():Deck{
        val randomPlayer=(0..3).random()
        cardsId.shuffle()
        val deck= mutableListOf(
            cardsId.subList(0,4),
            cardsId.subList(4,8),
            cardsId.subList(8,12),
            cardsId.subList(12,16))
        deck.shuffle()
        val data=Deck(
            randomPlayer,
            deck[0],
            deck[1],
            deck[2],
            deck[3]
        )
        return data
    }
}