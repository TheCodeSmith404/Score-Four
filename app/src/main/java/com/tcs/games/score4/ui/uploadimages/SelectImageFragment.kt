package com.tcs.games.score4.ui.uploadimages


import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentSelectImageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import utils.ImageUtils
import utils.views.CropOverlayView

@AndroidEntryPoint
class SelectImageFragment: Fragment() {
    private var _binding:FragmentSelectImageBinding?= null
    private val binding get()=_binding!!
    private val viewModel:SelectImageViewModel by viewModels()
    private val sharedViewModel:SelectImageSharedViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectImageBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sourceId = arguments?.getInt("sourceId")
        Log.d("backStack","$sourceId ${R.id.dialog_user_info} ${R.id.images_uploaded}")
        when(sourceId){
            R.id.dialog_user_info->binding.cropOverlay.cropMode=CropOverlayView.CropMode.CIRCLE
            R.id.images_uploaded->binding.cropOverlay.cropMode=CropOverlayView.CropMode.RECTANGLE
            else->binding.cropOverlay.cropMode=CropOverlayView.CropMode.RECTANGLE
        }
        ImageUtils.loadImageAsBitmapIntoImageView(requireContext(),binding.imagePreview,sharedViewModel.imageUri.value!!)
        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        binding.selectImageDone.setOnClickListener{
            val uri=sharedViewModel.imageUri.value
            if(uri!=null){
                lifecycleScope.launch {
                    viewModel.cropCompressAndSaveImage(ImageUtils.getBitmapFromImageView(binding.imagePreview)!!,binding.cropOverlay,requireActivity().applicationContext){done->
                        Log.d("Crop",done.toString())
                        if(done){
                            lifecycleScope.launch {
                                viewModel.uploadToFirebase(requireContext()){done,url->
                                    if(done){
                                        lifecycleScope.launch {
                                            val updateDone=viewModel.updateProfileUrl(url)
                                            if(updateDone){
                                                findNavController().navigateUp()
                                            }else{
                                                findNavController().navigateUp()
                                            }
                                        }
                                    }else{
                                        Log.d("Upload","Unable to upload")
                                    }
                                }
                                viewModel.preferenceManager.profileImageChanged=true
                                viewModel.preferenceManager.profileUrl=viewModel.savedImageUri.value
                                Log.d("Crop", "${viewModel.savedImageUri.value}")
                            }
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