package com.tcs.games.score4.ui.uploadimages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import model.ImagesData
import javax.inject.Inject

@HiltViewModel
class SharedImageViewModel @Inject constructor(): ViewModel() {

    private val _imageData = MutableStateFlow<Map<Int,Int>>(ImagesData.data)
    val imageData: StateFlow<Map<Int,Int>> = _imageData

    // Function to update the state with new data
    fun updateImageData(newData: Map<Int,Int>) {
        _imageData.value = newData
    }
}