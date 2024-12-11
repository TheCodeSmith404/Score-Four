package com.tcs.games.score4.bot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.random.Random

class ProbabilityBot {
    fun start(cardsString: List<String>): Deferred<Int> =
        CoroutineScope(Dispatchers.Default).async {
            val cards=cardsString.map{it[0]}
            // Step 1: Count occurrences of each card
            val cardCounts = cards.groupingBy { it }.eachCount()
            val uniqueCardCount = cardCounts.size
            val baseProbability = if (uniqueCardCount > 0) 100.0 / uniqueCardCount else 0.0
            // Step 2: Initialize probabilities
            val probabilityMap: MutableMap<Char, Double> = mutableMapOf()
            var remainingProbability = 100.0
            val highFrequencyCards = mutableListOf<Char>()
            for ((card, count) in cardCounts) {
                if (count > 1) {
                    val divider= 2.0.pow(count.toDouble())
                    val prob = baseProbability / divider
                    probabilityMap[card] = prob
                    remainingProbability -= prob
                    highFrequencyCards.add(card)
                } else {
                    probabilityMap[card] = baseProbability
                }
            }
            // Step 3: Distribute remaining probability for unique cards
            if (highFrequencyCards.size < uniqueCardCount) {
                val adjustment = remainingProbability / (uniqueCardCount - highFrequencyCards.size)
                for ((card, prob) in probabilityMap) {
                    if (card !in highFrequencyCards) {
                        probabilityMap[card] = prob + adjustment
                    }
                }
            }
            // Step 4: Build the `getCards` list
            val getCards = mutableListOf<Char>()
            probabilityMap.forEach { (card, prob) ->
                repeat(prob.roundToInt()) {
                    getCards.add(card)
                }
            }
            // Return a random card ID continuously
            val init=getCards[Random.nextInt(getCards.size)]
            cards.indexOf(init)
        }
}