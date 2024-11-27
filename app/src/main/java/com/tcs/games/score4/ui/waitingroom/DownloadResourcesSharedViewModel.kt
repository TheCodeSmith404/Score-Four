package com.tcs.games.score4.ui.waitingroom

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import data.repository.DownloadGameResourcesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import model.gameroom.CardInfo
import javax.inject.Inject
import kotlin.coroutines.resume

@HiltViewModel
class DownloadResourcesSharedViewModel @Inject constructor(
    private val repository: DownloadGameResourcesRepository
):ViewModel() {

    private val _isDownloaded = MutableLiveData(false)
    val isDownloaded: LiveData<Boolean> get() = _isDownloaded

    fun setResourcesDownloaded(){
        _isDownloaded.value=true
    }

    private val _progress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _progress

    suspend fun downloadCardResources(
        context: Context,
        hostId: String,
        gameId: String,
        data: List<CardInfo>
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            // Launch the download task inside a coroutine
            viewModelScope.launch {
                try {
                    repository.downloadCardResources(
                        context,
                        hostId,
                        gameId,
                        data,
                        progress = { progress -> _progress.postValue(progress) },
                        listener = { success ->
                            continuation.resume(success) // Resume the coroutine with the success value
                        }
                    )
                } catch (e: Exception) {
                    Log.e("DownloadError", "Error during resource download: ${e.message}")
                    continuation.resume(false) // If there's an error, resume with false
                }
            }
        }
    }

    companion object{
        private const val TAG="DownloadResourcesSharedViewModel.kt"
    }
}