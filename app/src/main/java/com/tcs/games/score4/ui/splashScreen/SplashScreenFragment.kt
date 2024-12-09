package com.tcs.games.score4.ui.splashScreen

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentSplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SplashScreenFragment : Fragment() {
    private val viewModel:SplashScreenViewModel by viewModels()
    private var _binding:FragmentSplashScreenBinding?=null
    private val binding get()=_binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentSplashScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.splashProgress.progress=10
        binding.splashText.text="Getting Account Details"
        if(viewModel.user.value==null){
            binding.splashProgress.progress=100
            Handler(Looper.getMainLooper()).postDelayed({
                findNavController().navigate(R.id.action_splash_screen_to_fragment_login)
            },500)
        }else{
            binding.splashText.text="Downloading configs"
            binding.splashProgress.progress=30
            viewModel.userData.observe(viewLifecycleOwner){data->
                if(data!=null){
                    binding.splashProgress.progress=100
                    binding.splashText.text="Data Received"
                    Handler(Looper.getMainLooper()).postDelayed({
                        findNavController().navigate(R.id.action_splash_screen_to_fragment_home)
                    },500)
                }
            }
        }

    }

}