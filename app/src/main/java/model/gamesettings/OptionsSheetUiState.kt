package model.gamesettings

sealed class OptionsSheetUiState {
    data class Colors(val colorId:Int):OptionsSheetUiState()
    data class Icons(val iconId: Int):OptionsSheetUiState()
}