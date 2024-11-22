package com.tcs.games.score4.ui.selectimage

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import utils.constants.ImageSelectionType
import javax.inject.Inject
/*
    *Probably don't need it
 */

@HiltViewModel
class SelectImageSharedViewModel @Inject constructor(): ViewModel() {
    val imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    private val _isImageUploaded= MutableStateFlow(false)
    val isImageUploaded:StateFlow<Boolean> =_isImageUploaded

    fun updateImageUploaded(uploaded:Boolean){
        _isImageUploaded.value=uploaded
    }
}