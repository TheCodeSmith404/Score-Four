package com.tcs.games.score4.ui.uploadedimages

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import data.PreferenceManager
import data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import utils.ImageUtils
import javax.inject.Inject

@HiltViewModel
class UploadedImagesViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val userRepository: UserRepository,
    private val storage: FirebaseStorage
):ViewModel() {
    var currentCard:Int =0
    var showWarning=false


    fun getCardInitialFromId(id:Int):Char{
        return ('A' +id)
    }
    fun getImageList():MutableList<Int> {
        return userRepository.user!!.imageData
    }
    fun updateImagesList(){
        val list=userRepository.user!!.imageData
        Log.d("list",list.toString())
        viewModelScope.launch {
            val done=userRepository.updateImageList(list)
            Log.d("Card Image","$done")
        }
    }
    var currentImageId=1

    fun isImagesUploaded():Boolean{
        return preferenceManager.imagesDownloaded
    }
    fun imageUploaded(){
        preferenceManager.imagesDownloaded=true
    }


    fun downloadMultipleImages(context: Context,progressListener:(Int)->Unit,doneListener:(Boolean)->Unit){
        val user=userRepository.user!!
        viewModelScope.launch {
            ImageUtils.downloadMultipleImagesFromFirebase(
                storage,
                "card_images/${user.authId}",
                user.imageData,
                context,
                true,
                progressListener,
                doneListener
            )
        }
    }
}