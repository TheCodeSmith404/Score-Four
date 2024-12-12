package com.tcs.games.score4.ui.uploadedimages

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import model.ImagesData
import javax.inject.Inject

@HiltViewModel
class UploadedImagesSharedViewModel @Inject constructor(): ViewModel(){
    private val _cardId=MutableStateFlow(ImagesData.data)
    val cardId: StateFlow<MutableMap<Int,Pair<Int,Int>>> get()= _cardId
    val imagesChanged=MutableStateFlow(false)

    fun updateImageData(data:MutableMap<Int,Pair<Int,Int>>){
        _cardId.value=data
    }

    fun updateCurrentCardId(images:Int){
        val temp=_cardId.value
        temp[-1]=Pair(images,-1)
        _cardId.value=temp
    }
}