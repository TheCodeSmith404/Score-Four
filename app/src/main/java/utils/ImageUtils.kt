package utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.transition.Transition
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.storage.FirebaseStorage
import com.tcs.games.score4.R
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import utils.constants.ImageNames
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object ImageUtils {
    fun loadImageAsBitmapIntoImageView(context: Context, imageView: ImageView, imageUri: Uri) {
        Glide.with(context)
            .asBitmap() // Load image as Bitmap
            .load(imageUri) // URI, URL, or File path
            .into(imageView)
    }
    fun getBitmapFromImageView(imageView: ImageView): Bitmap? {
        val drawable = imageView.drawable
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else {
            null
        }
    }
    fun loadBitmapIntoImageView(context: Context, bitmap: Bitmap, imageView: ImageView) {
        Glide.with(context)
            .load(bitmap) // Load the Bitmap
            .into(imageView) // Set it into the ImageView
    }
    fun loadImageUriIntoImageView(context: Context, uri:Uri, imageView: ImageView) {
        Glide.with(context)
            .load(uri) // Load the Bitmap
            .into(imageView) // Set it into the ImageView
    }
    suspend fun compressBitmap(bitmap: Bitmap): Bitmap {
        return withContext(Dispatchers.Default) {
            Log.d("Crop", "Compressing bitmap")
            var quality = 100  // Start with 100% quality
            val outputStream = ByteArrayOutputStream()
            // First, try compressing the bitmap to a JPEG format and see if it fits within the 500KB limit
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            // Check the size of the compressed bitmap and reduce quality iteratively if necessary
            while (outputStream.size() > 500 * 1024) { // Minimum quality is 10%
                outputStream.reset()  // Reset the output stream to reuse it
                quality -= 10  // Reduce the quality by 5% in each iteration
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }
            // Convert the compressed byte array back to a bitmap
            val compressedBitmap =
                BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
            // Clean up
            outputStream.close()
            compressedBitmap
        }
    }
    suspend fun saveImageToInternalStorage(bitmap: Bitmap, context: Context, name: String): Uri? {
        return withContext(Dispatchers.IO) {
            Log.d("Crop", "Saving Image")
            val file = File(context.filesDir, name)
            var outputStream: OutputStream? = null
            try {
                outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream) // 85 is the quality percentage
                outputStream.flush()
                Log.d("Crop", "Image saved successfully at ${file.absolutePath}")
                Uri.fromFile(file)
            } catch (e: IOException) {
                Log.e("Crop", "Failed to save image: ${e.message}")
                e.printStackTrace()
                null
            } finally {
                outputStream?.close()
                Log.d("Crop", "Exiting saveImageToInternalStorage")
            }
        }
    }
    suspend fun loadCardImageFromInternalStorage(context: Context,name:String,imageView:ImageView,keepPlaceHolders:Boolean=false){
        val file=File(getApplication(context).filesDir,"$name.jpg")
        return withContext(Dispatchers.Main){
            if(file.exists()){
                if(keepPlaceHolders) {
                    Glide.with(context)
                        .load(file)
                        .placeholder(R.drawable.baseline_person_24)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache on disk
                        .error(R.drawable.baseline_person_24)
                        .skipMemoryCache(false)
                        .into(imageView)
                }else{
                    loadImageFromFile(context,file,imageView)
                }
            }else{
                Log.d("cardImage","404 file not found $name")
            }
        }
    }
    fun downloadImageFromUrlToImageView(context: Context,url:String,imageView: ImageView){
        if(url!="") {
            Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache on disk
                .skipMemoryCache(false)
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(imageView)
        }
    }

    private fun loadImageFromFile(context:Context,file:File,imageView: ImageView){
        Glide.with(context)
            .load(file)
            .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache on disk
            .skipMemoryCache(false)
            .into(imageView)
    }
    suspend fun loadImageBitmapFromInternalStorage(context:Context,name:String):Bitmap?{
        val file=File(getApplication(context).filesDir,name)
        return withContext(Dispatchers.IO) {
            if (file.exists()) {
                BitmapFactory.decodeFile(file.absolutePath)
            } else {
                null
            }
        }
    }
    suspend fun uploadImageToFirebase(
        firebaseStorage: FirebaseStorage,
        directoryName: String,
        imageId: String,
        image:Bitmap,
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Create a reference to the Firebase Storage location
                val storageRef = firebaseStorage.reference
                val imageRef = storageRef.child("$directoryName/$imageId.jpg")
                val outputStream = ByteArrayOutputStream()
                // Upload the pre-compressed image data
                image.compress(Bitmap.CompressFormat.JPEG,100,outputStream)
                val uploadData=outputStream.toByteArray()
                imageRef.putBytes(uploadData).await()
                val downloadUrl = imageRef.downloadUrl.await()
                downloadUrl.toString()
            } catch (e: Exception) {
                Log.e("FirebaseUpload", "Failed to upload image: ${e.message}")
                null
            }
        }
    }
    suspend fun downloadMultipleImagesFromFirebase(
        firebaseStorage: FirebaseStorage,
        directoryName: String,
        imageList: List<Int>,
        context: Context,
        saveToPrivateFiles: Boolean,
        progressListener: (Int) -> Unit,
        doneListener: (Boolean) -> Unit,
    ) {
        var downloaded = 0
        withContext(Dispatchers.IO) {
            try {
                val storageRef = firebaseStorage.reference

                imageList.map { image ->
                    async {
                        try {
                            val imageRef = storageRef.child("$directoryName/${ImageNames.CARD.txt}$image.jpg")

                            // Avoid loading data into memory, use getFile()
                            val targetDir = if (saveToPrivateFiles) {
                                context.filesDir
                            } else {
                                context.cacheDir
                            }
                            val file = File(targetDir, "${ImageNames.CARD.txt}$image.jpg")

                            // Download directly to the file
                            imageRef.getFile(file).await()

                            synchronized(this@withContext) { downloaded++ }
                            withContext(Dispatchers.Main) {
                                progressListener(downloaded)
                            }

                            Log.d("Download", "Image downloaded at: ${file.toUri()}")
                            Log.d("FirebaseDownload", "Image saved to: ${file.absolutePath}")
                        } catch (e: Exception) {
                            Log.e("FirebaseDownload", "Failed to download $image: ${e.message}")
                            throw e // Propagate exception to stop further downloads
                        }
                    }
                }.awaitAll() // Wait for all downloads to complete
                withContext(Dispatchers.Main) {
                    doneListener(true)
                }
            } catch (exception: Exception) {
                Log.e("FirebaseDownload", "Failed during image downloads: ${exception.message}")
                withContext(Dispatchers.Main) {
                    doneListener(false)
                }
            }
        }
    }

    suspend fun downloadAndSetImageFromFirebaseOrInternalStorage(
        firebaseStorage: FirebaseStorage,
        directoryName: String,
        imageId: String,
        context: Context,
        nameToSave: String,
        directoryToSave:String,
        fromPrivateFiles: Boolean,
        imageView: ImageView
    ){
        withContext(Dispatchers.IO){
            try{
                val targetDir=if(fromPrivateFiles){
                    getApplication(context).filesDir
                }else{
                    getApplication(context).cacheDir
                }
                val file = File(targetDir, "$directoryToSave/$nameToSave.jpg")
                if(file.exists()){
                    loadImageFromFile(context,file,imageView)
                }else{
                    val storageRef = firebaseStorage.reference
                    val imageRef = storageRef.child("$directoryName/$imageId.jpg")
                    imageRef.getFile(file).await()
                    loadImageFromFile(context,file,imageView)
                }
            }catch (e:Exception){
                Log.e("Download Image","unable to download: ${e.message}",e)
            }
        }
    }


    suspend fun downloadImageFromFirebase(
        firebaseStorage: FirebaseStorage,
        directoryName: String,
        imageId: String,
        context: Context,
        nameToSave: String,
        saveToPrivateFiles: Boolean
    ): Uri? {
        return withContext(Dispatchers.IO) {
            try {
                // Create a reference to the Firebase Storage location
                val storageRef = firebaseStorage.reference
                val imageRef = storageRef.child("$directoryName/$imageId.jpg")

                // Determine the directory to save the image
                val targetDir = if (saveToPrivateFiles) {
                    context.filesDir // Private files directory
                } else {
                    context.cacheDir // Cache directory
                }

                // Create the target file
                val file = File(targetDir, "$nameToSave.jpg")

                // Download the image directly to the file
                imageRef.getFile(file).await()

                Log.d("Download", "Image downloaded at: ${file.toUri()}")
                Log.d("FirebaseDownload", "Image saved to: ${file.absolutePath}")
                file.toUri()
            } catch (e: Exception) {
                Log.e("FirebaseDownload", "Failed to download image: ${e.message}")
                null
            }
        }
    }


}