package com.tcs.games.score4.ui.gamesettingfragment

import android.graphics.Path.Op
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.gamesettings.OptionsSheetUiState

class OptionSheetViewModel: ViewModel() {
    private val _state= MutableStateFlow<OptionsSheetUiState>(OptionsSheetUiState.Colors(1))
    val state:StateFlow<OptionsSheetUiState> =_state
    fun updateState(isColor:Boolean=true,id:Int){
        if(isColor){
            _state.value=OptionsSheetUiState.Colors(id)
        }else{
            _state.value=OptionsSheetUiState.Icons(id)
        }
    }
}