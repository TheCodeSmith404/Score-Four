package com.tcs.games.score4.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import data.PreferenceManager
import data.repository.UserRepository
import utils.ImageUtils
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel:HomeViewModel by viewModels()
    @Inject
    lateinit var preferenceManager: PreferenceManager
    @Inject
    lateinit var userRepository: UserRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setOnClickListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews(){
        binding.showUserName.text=preferenceManager.userName
        if(preferenceManager.isSignedIn){
            loadImage()
        }else{
            setImageResource()
        }
    }
    private fun loadImage(){
        if(preferenceManager.profileUrl!=null)
            ImageUtils.loadImageUriIntoImageView(requireContext(),
                preferenceManager.profileUrl!!,binding.imageView3)
    }
    private fun setImageResource(){

    }

    private fun setOnClickListeners(){
        binding.homeCreateGame.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_home_to_game_settings)
        }
        binding.homeJoinGame.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_home_to_dialog_enter_credentials)
        }
        binding.cardView.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_home_to_dialog_user_info)
        }
        binding.showUserName.setOnClickListener{
            findNavController().navigate(R.id.action_fragment_home_to_dialog_user_info)
        }
    }
}