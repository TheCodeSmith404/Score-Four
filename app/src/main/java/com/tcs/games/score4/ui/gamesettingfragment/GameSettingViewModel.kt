package com.tcs.games.score4.ui.gamesettingfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import data.defaults.DefaultCardOptions
import model.gamesettings.CardInfoAdapter
import javax.inject.Inject

@HiltViewModel
class GameSettingViewModel @Inject constructor():ViewModel() {
    private val _cards=MutableLiveData(DefaultCardOptions.defaultCards)
    val cards: MutableList<CardInfoAdapter> =DefaultCardOptions.defaultCards

}