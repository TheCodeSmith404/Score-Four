package com.tcs.games.score4.ui.gameroom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogGameFinishedBinding
import com.tcs.games.score4.utils.ImageUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFinishedDialog: DialogFragment() {
    private var _binding:DialogGameFinishedBinding?=null
    private val binding get()=_binding!!
    private val viewModel:GameFinishedViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=DialogGameFinishedBinding.inflate(layoutInflater,container,false)
        val root=binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
        setUpViews()
        setUpGameRoomObserver()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        isCancelable=false
        // Define the Runnable
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    private fun setOnClickListener(){
        binding.backToHome.setOnClickListener{
            dismiss()
        }
        binding.rejoinRoom.setOnClickListener{
            Toast.makeText(requireContext(),"Implementation Pending",Toast.LENGTH_SHORT).show()
        }
        binding.restartRoom.setOnClickListener{
            Toast.makeText(requireContext(),"Implementation Pending",Toast.LENGTH_SHORT).show()
        }
    }
    private fun setUpViews(){
        val winner=viewModel.getGameRoomWinner()
        if(winner.bot){
            binding.profileWinnerImage.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.bot))
        }else {
            if(winner.playerProfile!="")
                ImageUtils.downloadImageFromUrlToImageView(
                    requireContext(),
                    winner.playerProfile,
                    binding.profileWinnerImage
                )
            else
                binding.profileWinnerImage.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_person_24))
        }
        if(viewModel.isPlayerWinner()){
            binding.curvedTextViewVictory.visibility=View.VISIBLE
            binding.curvedTextView.visibility=View.GONE
            binding.textViewWinnerName.text="You Win!"
            binding.textViewCongrats.text="Congratulations"
        }else{
            binding.curvedTextView.visibility=View.VISIBLE
            binding.curvedTextViewVictory.visibility=View.GONE
            binding.textViewWinnerName.text="${winner.playerName} Wins!"
            binding.textViewCongrats.text="Congratulate"
        }
        if(viewModel.isUserHost()){
            binding.restartRoom.visibility=View.VISIBLE
            binding.linearLayout.visibility=View.GONE
            binding.rejoinRoom.visibility=View.GONE
        }else{
            binding.linearLayout.visibility=View.VISIBLE
            binding.linearLayout.visibility=View.GONE
            binding.rejoinRoom.visibility=View.GONE
        }
    }
    private fun setUpGameRoomObserver(){
        viewModel.getGameRoomLiveData().observe(viewLifecycleOwner){data->
            if(data==null){
                binding.linearLayout.visibility=View.GONE
            }else{
                //TODO check if there room is restarted
                binding.linearLayout.visibility=View.GONE
                binding.rejoinRoom.visibility=View.VISIBLE
            }
        }
    }
}