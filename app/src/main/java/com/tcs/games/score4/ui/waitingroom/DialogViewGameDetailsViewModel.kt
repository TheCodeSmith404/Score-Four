package com.tcs.games.score4.ui.waitingroom

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import data.repository.GameDetailsRepository
import model.gameroom.CardInfo
import javax.inject.Inject

@HiltViewModel
class DialogViewGameDetailsViewModel @Inject constructor(
    private val gameDetailsRepository: GameDetailsRepository,
):ViewModel() {
    fun getGameInformation():Triple<List<CardInfo>,String,String>{
        return gameDetailsRepository.getGameInformation()
    }
    fun getCards():List<CardInfo>{
        return gameDetailsRepository.getCardsDetails()
    }
    fun getTpt():String{
        return gameDetailsRepository.getTpt()
    }

}
