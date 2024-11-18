package com.tcs.games.score4.ui.uploadimages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.databinding.FragmentHomeBinding
import com.tcs.games.score4.databinding.FragmentSelectImageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import utils.ImageUtils
import utils.views.CropOverlayView
import java.io.File
import java.io.FileInputStream

@AndroidEntryPoint
class SelectImageFragment: Fragment() {
    private var _binding:FragmentSelectImageBinding?= null
    private val binding get()=_binding!!
    private val viewModel:SelectImageViewModel by activityViewModels()
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                ImageUtils.loadImageAsBitmapIntoImageView(requireContext(),binding.imagePreview,it) // Load image into ImageView
                viewModel.imageUri.value=it         // Store URI in ViewModel
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectImageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.cropOverlay.cropMode=CropOverlayView.CropMode.RECTANGLE
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectImageLauncher.launch("image/*")
        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.selectImageDone.setOnClickListener{
            val uri=viewModel.imageUri.value
            if(uri!=null){
                lifecycleScope.launch {
                    viewModel.cropCompressAndSaveImage(ImageUtils.getBitmapFromImageView(binding.imagePreview)!!,binding.cropOverlay,requireActivity().applicationContext){done->
                        Log.d("Crop",done.toString())
                        if(done){
                            Log.d("Crop","${viewModel.savedImageUri.value}")
                            // TODO upload images to firebase and needed UI
                        }else{
                            // TODO show relevant error
                        }
                    }
                }
            }
        }
        binding.selectImageCancel.setOnClickListener{
            findNavController().navigateUp()
        }
    }
}