package com.tcs.games.score4.ui.uploadedimages

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import com.tcs.games.score4.utils.ImageUtils
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
    fun getMaxAllowedImages():Int{
        return userRepository.user?.numberImagesAllowed?:6
    }
    fun getImageList():MutableList<Int> {
        return userRepository.user!!.imageData
    }
    fun updateImagesList(id:Int){
        val list=userRepository.user!!.imageData
        list.add(id)
        Log.d("list",list.toString())
        viewModelScope.launch {
            val done=userRepository.updateImageList(list)
            Log.d("Card Image","$done")
        }
    }
    var currentImageId=1
    fun getImageIdToUpload():Int{
        return userRepository.user!!.numberImagesUploaded+1
    }

    fun isImagesUploaded():Boolean{
        return preferenceManager.imagesDownloaded
    }
    fun imageUploaded(){
        preferenceManager.imagesDownloaded=true
    }


    fun downloadMultipleImages(context: Context,progressListener:(Int)->Unit,doneListener:(Boolean)->Unit){
        val user=userRepository.user!!
        viewModelScope.launch {
            ImageUtils.downloadMultipleCardImagesFromFirebase(
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