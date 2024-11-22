package com.tcs.games.score4.ui.home

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogUserInfoBinding
import com.tcs.games.score4.ui.selectimage.SelectImageSharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import data.PreferenceManager
import data.repository.UserRepository
import kotlinx.coroutines.launch
import utils.ImageUtils
import utils.convertors.TimeUtils
import javax.inject.Inject

@AndroidEntryPoint
class UserInfoDialog:DialogFragment() {
    private var _binding: DialogUserInfoBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var userRepository:UserRepository
    @Inject
    lateinit var preferenceManager: PreferenceManager
    private val selectImageViewModel: SelectImageSharedViewModel by activityViewModels()
    private val selectImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let { // Load image into ImageView
                selectImageViewModel.imageUri.value=it // Store URI in ViewModel
                val bundle = Bundle().apply {
                    putString("imageName","profile_image")
                    putInt("sourceId", R.id.dialog_user_info) // Pass the ID of the calling fragment
                }
                findNavController().navigate(R.id.action_dialog_user_info_to_select_image,bundle)
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUserInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setOnClickListeners()
        observeImageChanges()
    }
    @SuppressLint("SetTextI18n")
    private fun setUpViews(){
        val data=userRepository.user!!
        if(preferenceManager.isSignedIn){
            binding.signInContainer.visibility=View.GONE
        }else{
            binding.editName.visibility=View.GONE
            binding.imageButtonEditProfile.visibility=View.GONE
        }
        binding.userName.text=data.playerName
        binding.shownId.text=data.generatedId
        binding.dateJoined.text=TimeUtils.getDateTimeString(data.timeCreated)
        binding.email.text=data.gmail
        binding.gamesPlayed.text=data.numberGamesPlayed.toString()
        binding.gamesWon.text=data.numberGamesWon.toString()
        binding.imagesUploaded.text="${data.numberImagesUploaded}/5"
        loadImage()
    }
    private fun loadImage(){
        if(preferenceManager.profileUrl!=null)
            ImageUtils.loadImageUriIntoImageView(requireContext(),
                preferenceManager.profileUrl!!,binding.imageView4)
    }
    private fun setOnClickListeners(){
        binding.imageButtonClose.setOnClickListener{
            dismiss()
        }
        binding.editName.setOnClickListener{
            Toast.makeText(requireContext(),"Edit name",Toast.LENGTH_SHORT).show()
        }
        binding.imageButtonEditProfile.setOnClickListener{
            selectImageLauncher.launch("image/*")
        }
    }
    private fun observeImageChanges(){
        lifecycleScope.launch{
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                selectImageViewModel.isImageUploaded.collect{value->
                    when(value){
                        true->{
                            selectImageViewModel.updateImageUploaded(false)
                        }
                        false->{
                            // Do nothing
                        }
                    }
                }

            }
        }
    }
    override fun onStart() {
        super.onStart()
        // Optional: Set the dialog's window size or other configurations
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isCancelable=false
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}