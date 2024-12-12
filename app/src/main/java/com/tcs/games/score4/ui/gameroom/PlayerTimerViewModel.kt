package com.tcs.games.score4.ui.gameroom

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.tcs.games.score4.data.repository.GameDeckRepository
import com.tcs.games.score4.data.repository.GameDetailsRepository
import com.tcs.games.score4.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerTimerViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val gameDetailsRepository: GameDetailsRepository,
    private val gameDeckRepository: GameDeckRepository
): ViewModel() {
    private val _state= MutableStateFlow<GameRoomCurrentlyPlayingState>(GameRoomCurrentlyPlayingState.Player1.Progress(0))
    val state: StateFlow<GameRoomCurrentlyPlayingState> get()=_state

    fun updateState(state:GameRoomCurrentlyPlayingState){
        _state.value=state
    }

    private var countDownTimer: CountDownTimer? = null
    private var _userIndex: Int? = null
    val userIndex: Int
        get() {
            if (_userIndex==null) {
                val currentUserId = userRepository.user!!.authId
                val players = gameDetailsRepository.gameRoom.value!!.players
                Log.d("Deck","$currentUserId is $players")
                _userIndex = players.indexOfFirst { user -> user.firebaseId == currentUserId }
                Log.d("Deck", _userIndex.toString())
            }
            return _userIndex!!
        }

    // Starts the countdown from the given initial time (in seconds)
    fun startCountdown(initialTime: Int) {
        // Cancel the existing timer if it is running

        countDownTimer = object : CountDownTimer(initialTime.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val value=state.value
                val time=(((millisUntilFinished/1000).toDouble()/initialTime)*100).toInt()
                when(value){
                    is GameRoomCurrentlyPlayingState.Player1 ->{
                        _state.value=GameRoomCurrentlyPlayingState.Player1.Progress(time)
                    }
                    is GameRoomCurrentlyPlayingState.Player2->{
                        _state.value=GameRoomCurrentlyPlayingState.Player2.Progress(time)
                    }
                    is GameRoomCurrentlyPlayingState.Player3 ->{
                        _state.value=GameRoomCurrentlyPlayingState.Player3.Progress(time)
                    }
                    is GameRoomCurrentlyPlayingState.Player4 ->{
                        _state.value=GameRoomCurrentlyPlayingState.Player4.Progress(time)
                    }
                }
            }

            override fun onFinish() {
                if(isUserCurrentlyPlaying()) {
                    val value = state.value
                    when (value) {
                        is GameRoomCurrentlyPlayingState.Player1 -> {
                            _state.value = GameRoomCurrentlyPlayingState.Player1.TimeOut
                        }

                        is GameRoomCurrentlyPlayingState.Player2 -> {
                            _state.value = GameRoomCurrentlyPlayingState.Player2.TimeOut
                        }

                        is GameRoomCurrentlyPlayingState.Player3 -> {
                            _state.value = GameRoomCurrentlyPlayingState.Player3.TimeOut
                        }

                        is GameRoomCurrentlyPlayingState.Player4 -> {
                            _state.value = GameRoomCurrentlyPlayingState.Player4.TimeOut
                        }
                    }
                }
            }
        }.start()
    }

    // Cancels the ongoing countdown timer if it's running
    fun cancelCountdown() {
        val value=state.value
        when(value){
            is GameRoomCurrentlyPlayingState.Player1 ->{
                _state.value=GameRoomCurrentlyPlayingState.Player1.Progress(0)
            }
            is GameRoomCurrentlyPlayingState.Player2->{
                _state.value=GameRoomCurrentlyPlayingState.Player2.Progress(0)
            }
            is GameRoomCurrentlyPlayingState.Player3 ->{
                _state.value=GameRoomCurrentlyPlayingState.Player3.Progress(0)
            }
            is GameRoomCurrentlyPlayingState.Player4 ->{
                _state.value=GameRoomCurrentlyPlayingState.Player4.Progress(0)
            }
        }
        countDownTimer?.cancel()
    }
    private fun isUserCurrentlyPlaying():Boolean{
        val currentlyPlaying=gameDeckRepository.gameDeck.value?.currentlyPlaying?:0
        return userIndex==currentlyPlaying
    }
}