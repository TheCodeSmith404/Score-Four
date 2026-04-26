package com.tcs.games.score4.ui.selectimage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.data.repository.UserRepository
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.constants.ImageNames
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SelectImageViewModel @Inject constructor(
    val preferenceManager: PreferenceManager,
    private val firebaseStorage: FirebaseStorage,
    private val userRepository: UserRepository,
) : ViewModel() {

    private val _savedImageUri = MutableStateFlow<Uri?>(null)
    val savedImageUri: StateFlow<Uri?> = _savedImageUri.asStateFlow()

    var imageName: String = ImageNames.PROFILE.txt
    var sourceId: Int = 0

    fun getUserId(): String = userRepository.user?.authId ?: ""

    fun cropCompressAndSaveImage(
        imageName: String,
        originalBitmap: Bitmap,
        cropParams: CropParams,
        context: Context,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                Log.d("Crop", "Starting to crop")
                val croppedBitmap = withContext(Dispatchers.Default) {
                    cropBitmap(originalBitmap, cropParams)
                }
                val compressedBitmap = ImageUtils.compressBitmap(croppedBitmap)
                val savedUri = ImageUtils.saveImageToInternalStorage(compressedBitmap, context, "$imageName.jpg")
                
                if (savedUri != null) {
                    _savedImageUri.value = savedUri
                    onComplete(true)
                } else {
                    onComplete(false)
                }
            } catch (e: Exception) {
                Log.d("Crop", e.message.toString())
                onComplete(false)
            }
        }
    }

    private fun cropBitmap(originalBitmap: Bitmap, cropParams: CropParams): Bitmap {
        return when (cropParams.cropMode) {
            CropParams.CropMode.CIRCLE -> cropCircle(originalBitmap, cropParams.cropCircleCenter, cropParams.cropCircleRadius)
            CropParams.CropMode.RECTANGLE -> cropRectangle(originalBitmap, cropParams.cropRect)
        }
    }

    private fun cropCircle(bitmap: Bitmap, center: PointF, radius: Float): Bitmap {
        val diameter = radius * 2
        val output = Bitmap.createBitmap(diameter.toInt(), diameter.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply { isAntiAlias = true }
        val rectF = RectF(0f, 0f, diameter, diameter)
        val path = Path().apply { addOval(rectF, Path.Direction.CW) }
        
        canvas.clipPath(path)
        val left = (center.x - radius).toInt()
        val top = (center.y - radius).toInt()
        val right = (center.x + radius).toInt()
        val bottom = (center.y + radius).toInt()
        val sourceRect = Rect(
            left.coerceIn(0, bitmap.width),
            top.coerceIn(0, bitmap.height),
            right.coerceIn(0, bitmap.width),
            bottom.coerceIn(0, bitmap.height)
        )
        val destRect = Rect(0, 0, diameter.toInt(), diameter.toInt())
        canvas.drawBitmap(bitmap, sourceRect, destRect, paint)
        return output
    }

    private fun cropRectangle(bitmap: Bitmap, rectF: RectF): Bitmap {
        val width = rectF.width().toInt()
        val height = rectF.height().toInt()
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply { isAntiAlias = true }
        val sourceRect = Rect(rectF.left.toInt(), rectF.top.toInt(), rectF.right.toInt(), rectF.bottom.toInt())
        val destRect = Rect(0, 0, width, height)
        canvas.drawBitmap(bitmap, sourceRect, destRect, paint)
        return output
    }

    private suspend fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    fun saveUriToSharedPreferences(uri: Uri) {
        preferenceManager.profileUrl = uri
    }

    suspend fun uploadToFirebase(context: Context, directory: String, imageName: String, done: (Boolean, String) -> Unit) {
        val uri = _savedImageUri.value ?: return
        val bitmap = getBitmapFromUri(uri, context) ?: return
        val url = ImageUtils.uploadImageToFirebase(firebaseStorage, directory, imageName, bitmap)
        if (url != null) {
            done(true, url)
        } else {
            done(false, "failure")
        }
    }

    suspend fun updateProfileUrl(url: String): Boolean {
        return userRepository.updateProfileImage(getUserId(), url)
    }
}