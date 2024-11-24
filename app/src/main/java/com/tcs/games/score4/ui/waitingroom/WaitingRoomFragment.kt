package com.tcs.games.score4.ui.waitingroom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentWaitingRoomBinding
import dagger.hilt.android.AndroidEntryPoint
import data.repository.GameDetailsRepository
import data.repository.WaitingRoomRepository
import kotlinx.coroutines.launch
import model.gameroom.GameRoom
import model.gameroom.PlayersStatus
import utils.ImageUtils
import utils.constants.ImageNames
import utils.views.WaitingRoomItem
import javax.inject.Inject

@AndroidEntryPoint
class WaitingRoomFragment:Fragment() {
    private var _binding:FragmentWaitingRoomBinding?=null
    private val binding get()=_binding!!
    private val viewModel:WaitingRoomViewModel by viewModels()
    private val waitingRoomGameDetailsViewModel:WaitingRoomSharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentWaitingRoomBinding.inflate(layoutInflater,container,false)
        val root=binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        lifecycleScope.launch {
            fetchGameRoom().observe(viewLifecycleOwner,{gameRoom->
                if(gameRoom!=null) {
                    if (viewModel.allPlayersReady(gameRoom.players)) {
                        navigate()
                    } else {
                        setUpWaitingRoom(gameRoom)
                    }
                    if(gameRoom.numberOfPlayers+gameRoom.numberOfBots==4)
                        binding.waitingRoomTvStatus.text=getString(R.string.fragment_waiting_room_waiting_for_players_to_be_ready)
                    else
                        binding.waitingRoomTvStatus.text=getString(R.string.fragment_waiting_room_waiting_for_players)
                }
                else
                    dataFetchError()
            })
        }

    }
    override fun onStart() {
        super.onStart()

    }
    override fun onStop() {
        super.onStop()
    }
    private fun setOnClickListeners(){
        binding.fragmentWaitingRoomTvGameDetails.setOnClickListener{
            findNavController().navigate(R.id.action_waiting_room_to_game_details)
        }
        binding.checkBox.setOnCheckedChangeListener{ view,checked->
            if(checked) {
                view.isEnabled = false
                updateUserStatus()
            }
        }
    }
    private fun fetchGameRoom(): LiveData<GameRoom?> {
        return viewModel.fetchGameRoom()
    }
    private fun setUpWaitingRoom(data:GameRoom){
        binding.host.setText(data.players[0].playerName)

        if(data.players[0].isReady){
            binding.host.setState(WaitingRoomItem.PlayerState.IS_READY)
        }else{
            binding.host.setState(WaitingRoomItem.PlayerState.PLAYER_JOINED)
        }
        if(data.players[0].playerProfile!=null){
            if(viewModel.isUserHost()){
                lifecycleScope.launch {
                    ImageUtils.loadCardImageFromInternalStorage(
                        requireActivity().applicationContext,
                        ImageNames.PROFILE.txt,
                        binding.host.getImageView(),
                        true
                    )
                }
            }else{
                viewModel.setImageToPlayerIcon(requireContext(),data.players[0].playerProfile,binding.host.getImageView())
            }
        }
        for(i in 1..<data.numberOfPlayers){
            addPlayer(i,data.players[i])
        }
        Log.d("Waiting Room",data.toString())

    }
    private fun addPlayer(index:Int,data:PlayersStatus){
        val view=getPlayerItem(index)
        view.setText(data.playerName)
        if(data.isReady){
            view.setState(WaitingRoomItem.PlayerState.IS_READY)
        }else {
            view.setState(WaitingRoomItem.PlayerState.PLAYER_JOINED)
        }
        viewModel.setImageToPlayerIcon(requireContext(),data.playerProfile,view.getImageView())
    }
    private fun getPlayerItem(index:Int):WaitingRoomItem{
        val map=mapOf(
            1 to binding.player1,
            2 to binding.player2,
            3 to binding.player3,
        )
        return map[index]?:binding.player3
    }
    private fun dataFetchError(){
        Toast.makeText(requireContext(),"Unable to fetch data retry",Toast.LENGTH_SHORT).show()
    }
    private fun updateUserStatus(){
        viewModel.updatePlayerStatus()
    }
    private fun navigate(){
        downloadAssets()
        try {
            findNavController().navigate(R.id.action_waiting_room_to_game_room)
        }catch (e:IllegalStateException){
            findNavController().navigateUp()
            findNavController().navigate(R.id.action_waiting_room_to_game_room)
        }
    }
    private fun downloadAssets(){

    }

}