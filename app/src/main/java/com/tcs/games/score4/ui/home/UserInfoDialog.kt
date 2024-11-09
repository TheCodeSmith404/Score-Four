package com.tcs.games.score4.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogUserInfoBinding
import dagger.hilt.android.AndroidEntryPoint
import data.PreferenceManager
import data.repository.UserRepository
import model.UserData
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
    }
    private fun setOnClickListeners(){
        binding.imageButtonClose.setOnClickListener{
            dismiss()
        }
        binding.editName.setOnClickListener{
            Toast.makeText(requireContext(),"Edit name",Toast.LENGTH_SHORT).show()
        }
        binding.imageButtonEditProfile.setOnClickListener{
            Toast.makeText(requireContext(),"Edit Profile",Toast.LENGTH_SHORT).show()
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