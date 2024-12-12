package com.tcs.games.score4.ui.waitingroom

import androidx.lifecycle.ViewModel
import com.tcs.games.score4.model.gameroom.CardInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import data.repository.GameDetailsRepository
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
