## 15 June  2024
* Laid Down Project structure rough roadmap with basic navigation
---
## 13 December 2024
* It's been 6 months this is a very long time for a single project make it to 1-2 months for each project prepare a roadmap in advance
* Did major working game is running and need to do stress testing and make it look pretty and add sounds and make it to remind players if any player leaves or take counter measures
* Planning to launch by 25th December as internal beta and 1st january as open beta
---
Analysis of Score Four (Score4)
Insight: The project is a real-time multiplayer card-matching game (akin to "Donkey" or "Pig") built on a robust MVVM + Hilt + Firebase stack. While the architectural foundation is professional, the implementation is currently "feature-complete but production-brittle," particularly regarding player lifecycle and hardcoded assumptions.

The Good, The Bad, and The Awful
✅ The Good
Architecture: Excellent use of Hilt for DI and a clean Repository pattern. The separation between GameDetailsRepository (room state) and GameDeckRepository (real-time moves) is logically sound.
Bot Logic: The ProbabilityBot isn't just a random number generator; it uses a probability distribution based on hand frequency to make strategic discards.
Tech Stack: Modern integration of Firebase (Firestore + RTDB) for low-latency updates and Google Identity/Credentials API for auth.
UX Thinking: Implementation of infinite scrolling in ViewPager2 for cards is a clever way to handle hand navigation.
⚠️ The Bad
View-Based UI: Using XML and ItemTouchHelper for card animations is functional but harder to polish than Jetpack Compose. The "premium" feel mentioned in your goals will be difficult to achieve with standard View transitions.
Manual Threading: Fragment-level CoroutineScope and Handler calls for bot delays (Handler(Looper.getMainLooper()).postDelayed) introduce potential memory leaks and make testing difficult.
Logic Leakage: High-level game rules (like winning conditions) are inside the ViewModel/Fragment. Ideally, this should be in a pure-Kotlin GameEngine or domain layer for easier unit testing.
❌ The Awful
Hardcoded Player Count: Logic like currentlyPlaying % 4 and when(index) { 0..3 } is pervasive. If a player leaves or a 2-player match is started, the game will likely crash or hang.
Force Unwrapping (!!): Frequent use of !! on LiveData values (e.g., gameDetailsRepository.gameRoom.value!!) is a "crash-waiting-to-happen" in a real-world network environment where sync delays are common.
Brittle State Management: There is no "Reconnection" or "Player Abandoned" logic. If a user’s socket drops, the game stalls for everyone.
Project Goals
The project aims to be a competitive, fast-paced multiplayer mobile game where the objective is to collect 4 cards of the same type. It strives for a premium feel with custom avatars, ad-supported monetization, and a balance between human players and AI bots.

What it Needs More
Resilience Layer: A state machine to handle PLAYER_DISCONNECTED, PLAYER_TIMED_OUT, and ROOM_CANCELLED.
Dynamic Scaling: Support for 2-4 players without hardcoded modulo logic.
Visual "Juice": Lottie animations for winning, sound effects (ExoPlayer/SoundPool), and haptic feedback.
Unit Testing: The ProbabilityBot and checkIfWon logic are critical and should be verified with JUnit tests.
State Recovery: Ability for a player to rejoin a room and fetch the current Deck state if the app crashes.
The Roadmap to Launch
Phase 1: Hardening (Immediate)
Refactor Modulo Logic: Replace % 4 with % players.size.
Null Safety: Replace !! with ?.let {} or provide fallback UI states.
Cleanup: Move Handler logic into the ViewModel using viewModelScope.launch { delay(...) }.
Phase 2: Player Lifecycle (Stress Testing)
Implement a Heartbeat/Presence system using Firestore onDisconnect.
Add logic to auto-replace a dropped player with a ProbabilityBot so the game continues.
Phase 3: Premium Polish
Add Sound Effects for card swipes and win/loss states.
Implement a Design System: Use consistent HSL-based colors and custom typography.
Jetpack Compose Migration (Optional but Recommended): Migrate the GameRoom UI to Compose for better animation control.
Phase 4: Monetization & Beta
Refine AdMob triggers (between games only).
Launch the Internal Beta (per your README) with a focus on network latency testing.
Implementation Choice: The transition to a dynamic player system is the most critical technical debt.

kotlin
// Example: Dynamic Turn Logic
fun moveToNextPlayer() {
    val deck = gameDeckRepository.gameDeck.value ?: return
    val playersCount = gameDetailsRepository.gameRoom.value?.players?.size ?: 4
    
    deck.currentlyPlaying = (deck.currentlyPlaying + 1) % playersCount
    uploadDeck(deck)
}
Trade-off: Using viewModelScope with delay instead of Handler decouples logic from the Android lifecycle, allowing for easier unit testing but requiring careful management of LiveData observers.