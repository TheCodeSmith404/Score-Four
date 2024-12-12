package com.tcs.games.score4.ui.selectimage


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentSelectImageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.views.CropOverlayView

@AndroidEntryPoint
class SelectImageFragment: Fragment() {
    private var _binding:FragmentSelectImageBinding?= null
    private val binding get()=_binding!!
    private val viewModel: SelectImageViewModel by viewModels()
    private val sharedViewModel: SelectImageSharedViewModel by activityViewModels()


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
        viewModel.sourceId = arguments?.getInt("sourceId")!!.toInt()
        viewModel.imageName= arguments?.getString("imageName").toString()
        Log.d("backStack","${viewModel.sourceId} ${R.id.dialog_user_info} ${R.id.images_uploaded}")
        when(viewModel.sourceId){
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
                    viewModel.cropCompressAndSaveImage(viewModel.imageName,ImageUtils.getBitmapFromImageView(binding.imagePreview)!!,binding.cropOverlay,requireActivity().applicationContext){done->
                        Log.d("Crop",done.toString())
                        if(done){
                            lifecycleScope.launch {
                                val directory= if(viewModel.sourceId==R.id.dialog_user_info){
                                    "profile_images"
                                }else{
                                    "card_images/${viewModel.getUserId()}"
                                }
                                val imageName= if(viewModel.sourceId==R.id.dialog_user_info){
                                    viewModel.getUserId()
                                }else{
                                    viewModel.imageName
                                }
                                viewModel.uploadToFirebase(requireContext(), directory, imageName){done,url->
                                    if(done){
                                        lifecycleScope.launch {
                                            if(viewModel.sourceId==R.id.dialog_user_info) {
                                                val updateDone = viewModel.updateProfileUrl(url)
                                                if (updateDone) {
                                                    findNavController().navigateUp()
                                                } else {
                                                    findNavController().navigateUp()
                                                }
                                            }else{
                                                sharedViewModel.updateImageUploaded(true)
                                                findNavController().navigateUp()
                                            }
                                        }
                                    }else{
                                        sharedViewModel.updateImageUploaded(false)
                                        //Do some Error ui
                                        Log.d("Upload","Unable to upload")
                                    }
                                }
                                if(viewModel.sourceId==R.id.dialog_user_info) {
                                    viewModel.preferenceManager.profileImageChanged = true
                                    viewModel.preferenceManager.profileUrl =
                                        viewModel.savedImageUri.value
                                }
                                Log.d("Crop", "${viewModel.savedImageUri.value}")
                            }
                            // TODO upload images to firebase and needed UI
                        }else{
                            sharedViewModel.updateImageUploaded(false)
                            // TODO show relevant error
                        }
                    }
                }
            }
        }
        binding.selectImageCancel.setOnClickListener{
            sharedViewModel.updateImageUploaded(false)
            findNavController().navigateUp()
        }
    }
}