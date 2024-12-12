package com.tcs.games.score4.bot

class ProbabilityBotTest {
//    @Test
//    fun `test valid card is returned`() = runBlocking {
//        // Input card list
//        val cardList = listOf("A","B","C","D")
//
//        // Call the function
//        val drawnCard = ProbabilityBot().start(cardList).await()
//
//        // Verify that the drawn card is in the input list
//        assertTrue("The drawn card should be in the original list", cardList.contains(drawnCard))
//    }
//
//    @Test
//    fun `test randomness over multiple draws with various card combinations`() = runBlocking {
//        // Define various card lists
//        val cardLists = generateCardLists(80809L)
//
//        // For each card list, simulate 1000 draws and print the results
//        cardLists.forEach { cardList ->
//            println("Testing card list: $cardList")
//
//            // Map to count occurrences of each card
//            val drawCounts = mutableMapOf<Char, Int>().withDefault { 0 }
//
//            // Simulate 1000 draws
//            repeat(1000) {
//                val drawnCard = ProbabilityBot().start(cardList).await()
//                drawCounts[drawnCard] = drawCounts.getValue(drawnCard) + 1
//            }
//
//            // Print the count of each card
//            println("Count of A: ${drawCounts['A'] ?: 0}")
//            println("Count of B: ${drawCounts['B'] ?: 0}")
//            println("Count of C: ${drawCounts['C'] ?: 0}")
//            println("Count of D: ${drawCounts['D'] ?: 0}")
//            println("-".repeat(40)) // Separator between tests
//        }
//    }
//
//    private fun generateCardLists(seed: Long? = null): List<List<String>> {
//        val cards = listOf("A","B","C","D")
//        val cardLists = mutableListOf<List<String>>()
//
//        // If a seed is provided, use it; otherwise, use the default random generator
//        val random = seed?.let { Random(it) } ?: Random
//
//        repeat(100) {
//            // Use random to determine the length of the list (3, 4, or 5)
//            val listLength = random.nextInt(3, 6)
//
//            // Use random to select the cards for the list
//            val list = List(listLength) { cards[random.nextInt(cards.size)] }
//
//            cardLists.add(list)
//        }
//
//        return cardLists
//    }
//
//
//
//    @Test
//    fun `test 1000 results from a given card set`() = runBlocking {
//        // Input card list
//        val cardList = listOf('A', 'B', 'C', 'A', 'A')
//
//        // Collect 1000 results
//        val results = mutableListOf<Char>()
//        repeat(1000) {
//            results.add(ProbabilityBot().start(cardList).await())
//        }
//
//        // Verify all results are from the original card list
//        results.forEach { result ->
//            assertTrue("The result card '$result' should be in the original card list", cardList.contains(result))
//        }
//
//        // Verify that each card is drawn at least once (unlikely but possible to fail if randomness avoids a card in all 1000 draws)
//        cardList.forEach { card ->
//            assertTrue("Card '$card' should be drawn at least once", results.contains(card))
//        }
//    }

}