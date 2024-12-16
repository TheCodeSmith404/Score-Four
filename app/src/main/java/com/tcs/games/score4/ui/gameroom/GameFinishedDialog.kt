package com.tcs.games.score4.ui.gameroom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogGameFinishedBinding
import com.tcs.games.score4.utils.AlertDialogManager
import com.tcs.games.score4.utils.ImageUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            val id=viewModel.newId
            if(id!=""){
                lifecycleScope.launch {
                    showProgressDialog("Joining Room")
                    val result=viewModel.joinRestartedGameRoom(id)
                    if(result.isSuccess){
                        Log.d("Restart","Room Joined Navigating")
                        hideProgressDialog()
                        viewModel.getGameRoomLiveData().removeObservers(viewLifecycleOwner)
                        dismiss()
                        findNavController().navigate(R.id.action_game_finished_to_waiting_room)
                    }else{
                        Log.d("Restart","Unable to Join room")
                        hideProgressDialog()
                        Toast.makeText(requireContext(),"Error Joining Room",Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                binding.rejoinRoom.visibility=View.GONE
            }
        }
        binding.restartRoom.setOnClickListener{
            lifecycleScope.launch {
                showProgressDialog("Creating New Room")
                if(viewModel.isUserHost()) {
                    val done=viewModel.createGameRoom()
                    if(done){
                        Log.d("Restart","Room Created")
                        hideProgressDialog()
                        viewModel.getGameRoomLiveData().removeObservers(viewLifecycleOwner)
                        dismiss()
                        findNavController().navigate(R.id.action_game_finished_to_waiting_room)
                    }else{
                        Log.d("Restart","Error creating room")
                        hideProgressDialog()
                    }
                }else{
                    Log.d("Restart","User Ain't Host")
                    hideProgressDialog()
                    Log.d("Restart","Restart Room user is not a host")
                    binding.restartRoom.visibility=View.GONE
                }
            }
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
            binding.restartRoom.visibility=View.GONE
            binding.linearLayout.visibility=View.VISIBLE
            binding.rejoinRoom.visibility=View.GONE
        }
    }
    private fun setUpGameRoomObserver(){
        viewModel.getGameRoomLiveData().observe(viewLifecycleOwner){data->
            if(data==null){
                Log.d("Restart","Room is null")
                binding.linearLayout.visibility=View.GONE
            }else{
                if(data.winner>=0&&data.restart) {
                    viewModel.getGameRoomLiveData().removeObservers(viewLifecycleOwner)
                    viewModel.newId=data.newRoomId
                    Log.d("Restart","Room restarted")
                    binding.linearLayout.visibility = View.GONE
                    binding.rejoinRoom.visibility = View.VISIBLE
                }
            }
        }
    }
    private fun showProgressDialog(message:String){
        AlertDialogManager.showLoadingDialog(
            requireContext(),
            true,
            message
        )
    }
    private fun hideProgressDialog(){
        AlertDialogManager.hideDialog()
    }
}