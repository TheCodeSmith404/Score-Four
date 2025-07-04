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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.tcs.games.score4.data.PreferenceManager
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.constants.ImageNames
import dagger.hilt.android.lifecycle.HiltViewModel
import data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.views.CropOverlayView
import javax.inject.Inject

@HiltViewModel
class SelectImageViewModel @Inject constructor(
    val preferenceManager: PreferenceManager,
    private val firebaseStorage: FirebaseStorage,
    private val userRepository: UserRepository,
):ViewModel() {

    val savedImageUri: MutableLiveData<Uri?> = MutableLiveData()
    var imageName:String=ImageNames.PROFILE.txt
    var sourceId:Int=0
    fun getUserId():String{
        return userRepository.user!!.authId
    }

    // Function to handle cropping, compression, and saving image
    fun cropCompressAndSaveImage(imageName:String,originalBitmap: Bitmap, cropOverlay: CropOverlayView,context:Context,onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("Crop","Starting to crop")
                // Perform cropping and compression in the background thread
                val croppedBitmap = withContext(Dispatchers.Default) {
                    cropBitmap(originalBitmap, cropOverlay)
                }
                val compressedBitmap = ImageUtils.compressBitmap(croppedBitmap)
                // Save the image to storage
                val savedUri = ImageUtils.
                saveImageToInternalStorage(compressedBitmap,context,"$imageName.jpg")
                Log.d("Crop","saved Uri: ${savedUri.toString()}")
                if (savedUri != null) {
                    // Save URI in SharedPreferences
                    savedImageUri.value=savedUri
                    Log.d("Crop","All Done")
                    onComplete(true)
                } else {
                    onComplete(false)
                    Log.d("Crop","saved Uri=null")
                }
            } catch (e: Exception) {
                Log.d("Crop",e.message.toString())
                onComplete(false)
            }
        }
    }

    // Crop Bitmap based on the crop mode
    private fun cropBitmap(originalBitmap: Bitmap, cropOverlay: CropOverlayView): Bitmap {
        Log.d("Crop","cropping bitmap")
        return when (cropOverlay.cropMode) {
            CropOverlayView.CropMode.CIRCLE -> {
                cropCircle(originalBitmap, cropOverlay.cropCircleCenter, cropOverlay.cropCircleRadius)
            }
            CropOverlayView.CropMode.RECTANGLE -> {
                cropRectangle(originalBitmap, cropOverlay.cropRect)
            }
        }
    }

    private fun cropCircle(bitmap: Bitmap, center: PointF, radius: Float): Bitmap {
        // Create a new bitmap with the same dimensions as the circle area
        val diameter = radius * 2
        val output = Bitmap.createBitmap(diameter.toInt(), diameter.toInt(), Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the output Bitmap
        val canvas = Canvas(output)

        // Define the circular path
        val paint = Paint()
        paint.isAntiAlias = true
        val rectF = RectF(0f, 0f, diameter, diameter)
        val path = Path()
        path.addOval(rectF, Path.Direction.CW)

        // Clip the canvas to the circular path
        canvas.clipPath(path)

        // Calculate the source rectangle to draw the bitmap (centered around the given PointF)
        val left = (center.x - radius).toInt()
        val top = (center.y - radius).toInt()
        val right = (center.x + radius).toInt()
        val bottom = (center.y + radius).toInt()

        // Ensure the source rectangle stays within the bounds of the original Bitmap
        val sourceRect = Rect(
            left.coerceIn(0, bitmap.width),
            top.coerceIn(0, bitmap.height),
            right.coerceIn(0, bitmap.width),
            bottom.coerceIn(0, bitmap.height)
        )

        // Define the destination rectangle in the output Bitmap
        val destRect = Rect(0, 0, diameter.toInt(), diameter.toInt())

        // Draw the part of the Bitmap within the circular area onto the output Bitmap
        canvas.drawBitmap(bitmap, sourceRect, destRect, paint)

        return output
    }


    private fun cropRectangle(bitmap: Bitmap, rectF: RectF): Bitmap {
        // Create a new bitmap with the specified width and height of the rectangle
        val width = rectF.width().toInt()
        val height = rectF.height().toInt()

        // Create a new Bitmap with the dimensions of the rectangle
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the output Bitmap
        val canvas = Canvas(output)

        // Define a paint object (if needed)
        val paint = Paint()
        paint.isAntiAlias = true

        // Draw the portion of the original Bitmap inside the RectF to the new Bitmap
        val sourceRect = Rect(
            rectF.left.toInt(),
            rectF.top.toInt(),
            rectF.right.toInt(),
            rectF.bottom.toInt()
        )

        // Destination RectF (same as the output size)
        val destRect = Rect(0, 0, width, height)

        // Draw the Bitmap
        canvas.drawBitmap(bitmap, sourceRect, destRect, paint)

        return output
    }

    private suspend fun getBitmapFromUri(uri: Uri, context: Context): Bitmap? {
        // Use withContext to switch to background thread (IO)
        Log.d("Crop","Getting bitmap from uri")
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
                null // Return null if any error occurs
            }
        }
    }

    // Save the URI to SharedPreferences
    private fun saveUriToSharedPreferences(uri: Uri) {
        Log.d("Crop","Saving URI")
        preferenceManager.profileUrl=uri
    }
    suspend fun uploadToFirebase(context: Context,directory:String,imageName:String,done:(Boolean,String)->Unit){
       val url=ImageUtils.uploadImageToFirebase(firebaseStorage,directory,imageName,getBitmapFromUri(savedImageUri.value!!,context)!!)
        if(url!=null){
            done(true,url)
        }else
            done(false,"failure")
        Log.d("Upload profile",url.toString())
    }
    suspend fun updateProfileUrl(url:String):Boolean{
        val done=userRepository.updateProfileImage(userRepository.user!!.authId,url)
        if(done){
            return true
        }else{
            return false
        }
    }
}