package model.gamesettings

sealed class OptionsSheetUiState {
    data class Colors(val position:Int,val colorId:Int):OptionsSheetUiState()
    data class Icons(val position:Int,val iconId: Int):OptionsSheetUiState()
    data class Time(val numId:Int):OptionsSheetUiState()
}