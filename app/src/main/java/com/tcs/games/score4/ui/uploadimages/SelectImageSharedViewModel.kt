package com.tcs.games.score4.ui.uploadimages

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SelectImageSharedViewModel @Inject constructor(): ViewModel() {
    val imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
}