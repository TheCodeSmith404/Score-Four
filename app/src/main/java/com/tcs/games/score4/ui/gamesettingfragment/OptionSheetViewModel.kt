package com.tcs.games.score4.ui.gamesettingfragment

import androidx.lifecycle.ViewModel
import com.tcs.games.score4.model.gamesettings.OptionsSheetUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class OptionSheetViewModel: ViewModel() {
    private val _state= MutableStateFlow<OptionsSheetUiState>(OptionsSheetUiState.Colors(0,1))
    val state:StateFlow<OptionsSheetUiState> =_state
    fun updateState(isColor:Boolean=true,position:Int,id:Int,isNumber:Boolean=false){
        if(isColor){
            _state.value=OptionsSheetUiState.Colors(position,id)
        }else{
            if(isNumber){
                _state.value=OptionsSheetUiState.Time(id)
            }else {
                _state.value = OptionsSheetUiState.Icons(position, id)
            }
        }
    }
    var byUser=false
    var position=0
    val setColors= mutableSetOf(1,2,3,4)
}