package com.tcs.games.score4.data.repository

import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.utils.constants.ImageNames
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadGameResourcesRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
) {

    suspend fun downloadCardResources(
        context: Context,
        hostId: String,
        gameId: String,
        data: List<CardInfo>,
        progress: (Int) -> Unit,
        listener: (Boolean) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            var downloaded = 0
            try {
                val reference = firebaseStorage.reference
                val targetDir = File(context.cacheDir, gameId)
                if(!targetDir.exists()){
                    targetDir.mkdirs()
                }
                // Ensure the subfolder exists
//                if (!targetDir.exists() || targetDir.mkdirs()) {
//                    Log.e("SubFolder", "Failed to create subfolder at: ${targetDir.absolutePath}")
//                    withContext(Dispatchers.Main) { listener(false) }
//                    return@withContext
//                }
                // Process all cards in parallel
                try {
                    data.map { card ->
                        async {
                            try {
                                if (card.imageRes > 0) {
                                    val imageRef = reference.child("card_images/$hostId/${ImageNames.CARD.txt}${card.imageRes}.jpg")
                                    val file = File(targetDir, "${ImageNames.CARD.txt}${card.imageRes}.jpg")

                                    // Download the image
                                    imageRef.getFile(file).await()
                                    Log.d(TAG,"image downloaded with uri ${file.toUri()}")
                                    // Update progress safely
                                    synchronized(this@withContext) {
                                        downloaded++
                                    }
                                    withContext(Dispatchers.Main) { progress(downloaded) }
                                } else {
                                    // Handle cards with no image resource
                                    synchronized(this@withContext) {
                                        downloaded++
                                    }
                                    withContext(Dispatchers.Main) { progress(downloaded) }
                                }
                            } catch (e: Exception) {
                                Log.e("DownloadError", "Error downloading card resource: ${e.message}")
                                throw e // Propagate the exception to handle failure
                            }
                        }
                    }.awaitAll() // Wait for all downloads to complete
                    // Notify success
                    withContext(Dispatchers.Main) { listener(true) }
                } catch (e: Exception) {
                    Log.e("DownloadError", "Error during batch download: ${e.message}")
                    withContext(Dispatchers.Main) { listener(false) }
                }
            } catch (e: Exception) {
                Log.e("DownloadError", "Unexpected error: ${e.message}")
                withContext(Dispatchers.Main) { listener(false) }
            }
        }
    }

    companion object{
        private const val TAG="DownloadGameResourcesRepository.kt"
    }
}