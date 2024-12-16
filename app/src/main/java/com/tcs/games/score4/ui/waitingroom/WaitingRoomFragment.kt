package com.tcs.games.score4.ui.waitingroom

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentWaitingRoomBinding
import com.tcs.games.score4.model.gameroom.GameRoom
import com.tcs.games.score4.model.gameroom.PlayersStatus
import com.tcs.games.score4.utils.AlertDialogManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.tcs.games.score4.utils.views.WaitingRoomItem
import kotlin.math.min

@AndroidEntryPoint
class WaitingRoomFragment:Fragment() {
    private var _binding:FragmentWaitingRoomBinding?=null
    private val binding get()=_binding!!
    private val viewModel:WaitingRoomViewModel by viewModels()
    private val downloadResourcesSharedViewModel:DownloadResourcesSharedViewModel by activityViewModels()

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
            viewModel.startListeningToGameDetails()
            fetchGameRoom().observe(viewLifecycleOwner) { gameRoom ->
                if (gameRoom != null) {
                    Log.d("Wait", gameRoom.running.toString())
                    if (gameRoom.running&&viewModel.allPlayersReady()) {
                        Log.d("Waiting room", "Navigating")
                        navigate()
                    } else {
                        Log.d("Waiting room", "setting up waiting room")
                        setUpWaitingRoom(gameRoom)
                    }
                    if (gameRoom.numberOfPlayers + gameRoom.numberOfBots == 4)
                        binding.waitingRoomTvStatus.text =
                            getString(R.string.fragment_waiting_room_waiting_for_players_to_be_ready)
                    else
                        binding.waitingRoomTvStatus.text =
                            getString(R.string.fragment_waiting_room_waiting_for_players)
                } else
                    dataFetchError()
            }
        }

    }
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            viewModel.startListeningToGameDetails()
            fetchGameRoom().observe(viewLifecycleOwner) { gameRoom ->
                if (gameRoom != null) {
                    Log.d("Wait", gameRoom.running.toString())
                    if (gameRoom.running&&viewModel.allPlayersReady()) {
                        Log.d("Waiting room", "Navigating")
                        navigate()
                    } else {
                        Log.d("Waiting room", "setting up waiting room")
                        setUpWaitingRoom(gameRoom)
                    }
                    if (gameRoom.numberOfPlayers + gameRoom.numberOfBots == 4)
                        binding.waitingRoomTvStatus.text =
                            getString(R.string.fragment_waiting_room_waiting_for_players_to_be_ready)
                    else
                        binding.waitingRoomTvStatus.text =
                            getString(R.string.fragment_waiting_room_waiting_for_players)
                } else
                    dataFetchError()
            }
        }
        Log.d(this::class.simpleName,"Waiting room has started")

    }
    override fun onStop() {
        super.onStop()
        fetchGameRoom().removeObservers(viewLifecycleOwner)
    }
    private fun setOnClickListeners(){
        binding.fragmentWaitingRoomTvGameDetails.setOnClickListener{
            if(downloadResourcesSharedViewModel.isDownloaded.value==true){
                findNavController().navigate(R.id.action_waiting_room_to_game_details)
            }else{
                lifecycleScope.launch {
                    val result=downloadResourcesToCacheDir()
                    if(result){
                        //TODO show and hide progressbar
                        downloadResourcesSharedViewModel.setResourcesDownloaded()
                        findNavController().navigate(R.id.action_waiting_room_to_game_details)
                    }else{
                        Toast.makeText(requireContext(),"Unable to download resources",Toast.LENGTH_LONG).show()
                        //retry or show relevant error
                    }
                }
            }
        }
        binding.checkBox.setOnCheckedChangeListener{ view,checked->
            if(checked) {
                if(downloadResourcesSharedViewModel.isDownloaded.value == true){
                    updateUserStatus()
                    view.isEnabled=false
                }else{
                    lifecycleScope.launch {
                        val result=downloadResourcesToCacheDir()
                        if(result){
                            view.isEnabled=false
                            binding.checkBox.setOnCheckedChangeListener(null)
                            downloadResourcesSharedViewModel.setResourcesDownloaded()
                            updateUserStatus()
                        }else{
                            view.isEnabled=true
                            view.isChecked=false
                            Toast.makeText(requireContext(),"Unable to download resources",Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        binding.shareCredentialContainer.setOnClickListener{
            val key=viewModel.getKeys()
            AlertDialogManager.showShareCredentialDialog(
                requireContext(),
                key.first,
                key.second)
        }
        binding.imageButtonBack.setOnClickListener{
            findNavController().navigateUp()
        }
    }
    private suspend fun downloadResourcesToCacheDir():Boolean{
        return withContext(Dispatchers.IO){
            val gameRoom=fetchGameRoom().value!!
            downloadResourcesSharedViewModel.downloadCardResources(
                requireContext(),
                gameRoom.hostId,
                gameRoom.roomId,
                gameRoom.cards
            )
        }
    }
    private fun fetchGameRoom(): LiveData<GameRoom?> {
        return viewModel.fetchGameRoom()
    }
    private fun setUpWaitingRoom(data:GameRoom){
        for(i in 0..min(data.numberOfPlayers+data.numberOfBots-1,3)){
            Log.d("AddBot","Adding player:$i")
            addPlayer(i,data.players[i])
        }
        val players=data.numberOfPlayers+data.numberOfBots
        Log.d("AddBot","$players")
        if(viewModel.isUserHost()){
            Log.d("AddBot","User is host")
            for(i in players..<4){
                refreshContainers(i,true,data.numberOfBots+1)
            }
        }else{
            Log.d("AddBot","User is not host")
            for(i in players..<4){
                refreshContainers(i,false,0)
            }
        }

        Log.d("Waiting Room",data.toString())

    }
    private fun refreshContainers(index:Int,isHost:Boolean,numberOfBots:Int){
        Log.d("AddBot","refreshing container for host:$isHost at index $index")
        val view=getPlayerItem(index)
        if(isHost){
            view.setState(WaitingRoomItem.PlayerState.ADD_BOT)
            view.getAddBotContainer().setOnClickListener{
                if(viewModel.canAddBot()) {
                    Log.d("AddBot","ItemClicked at $index")
                    viewModel.addBot(index,numberOfBots)
                }else{
                    Toast.makeText(requireContext(),"Can not add more than two bots in an online game",Toast.LENGTH_LONG).show()
                }
            }
        }else{
            view.setState(WaitingRoomItem.PlayerState.WAITING_TO_JOIN)
        }
    }
    private fun addPlayer(index:Int,data:PlayersStatus){
        val view=getPlayerItem(index)
        view.setText(data.playerName)
        view.setOnClickListener{
            AlertDialogManager.showPlayerDetailsDialog(requireContext(),data)
        }
        if(data.ready){
            view.setState(WaitingRoomItem.PlayerState.IS_READY)
        }else {
            view.setState(WaitingRoomItem.PlayerState.PLAYER_JOINED)
        }
        if(data.bot){
            viewModel.setImageToBot(
                requireContext(),
                view.getImageView()
            )
        }else {
            viewModel.setImageToPlayerIcon(
                requireContext(),
                data.playerProfile,
                view.getImageView()
            )
        }
    }
    private fun getPlayerItem(index:Int):WaitingRoomItem{
        val map=mapOf(
            0 to binding.player0,
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
        viewModel.fetchGameRoom().removeObservers(viewLifecycleOwner)
        try {
            findNavController().navigate(R.id.action_waiting_room_to_game_room)
        }catch (e:IllegalStateException){
            findNavController().navigateUp()
            findNavController().navigate(R.id.action_waiting_room_to_game_room)
        }
    }

}