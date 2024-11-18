package com.tcs.games.score4.ui.gamesettingfragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentGameSettingsBinding
import com.tcs.games.score4.ui.gamesettingfragment.GameSettingViewModel
import dagger.hilt.android.AndroidEntryPoint
import data.repository.UserRepository
import kotlinx.coroutines.launch
import model.gameroom.GameRoom
import model.gameroom.PlayersStatus
import utils.gamelogic.DeckCreator
import utils.gamelogic.GenerateGameIdPass
import javax.inject.Inject

@AndroidEntryPoint
class GameSettingFragment:Fragment(),OptionsBottomSheet.OptionsBottomSheetListener {
    private var _binding: FragmentGameSettingsBinding?= null
    private val binding get() = _binding!!
    private val viewModel:GameSettingViewModel by viewModels()
    private val optionSheetViewModel:OptionSheetViewModel by activityViewModels()
    private lateinit var adapter: GameSettingRecycleViewAdapter
    private lateinit var optionsBottomSheet: OptionsBottomSheet
    @Inject
    lateinit var userRepository: UserRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentGameSettingsBinding.inflate(layoutInflater,container,false)
        val root=binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBottomSheet()
        setUpAdapter()
        setOnClickListeners()
        setUpRecycleView()
        setUpNumberPicker()
    }
    private fun setOnClickListeners(){
        binding.fragmentGameSettingsCreate.setOnClickListener{
            createGameRoom()
        }

    }
    private fun setUpAdapter(){
        adapter= GameSettingRecycleViewAdapter(
            context=requireContext(),
            cards = viewModel.cards,
            onEditClick = { position ->
                viewModel.cards[position].isEdit=true
            },
            onPreViewClick = { position ->
                viewModel.cards[position].isEdit=false
            },
            onNameChange = { position, name ->
                viewModel.cards[position].name=name
                // TODO name Stuff
//                val temp=name.trim()
//                if(temp.isNotBlank()) {
//                    if (viewModel.setNames.contains(temp)) {
//                        Toast.makeText(requireContext(), "Two cards can not have a same name",Toast.LENGTH_SHORT).show()
//                    } else {
//                        val temp2 = viewModel.cards[position].name
//                        viewModel.setNames.remove(temp2)
//                        viewModel.setNames.add(temp)
//                        viewModel.cards[position].name =temp
//                    }
//                }else{
//                    Toast.makeText(requireContext(), "Card Name can not be empty",Toast.LENGTH_SHORT).show()
//                }
            },
            pickColor = {position,colorId->
                Log.d("Options sheet","Color Click Received with data pos:$position and id: $colorId")
                optionSheetViewModel.updateState(true,position,colorId)
                showOptionsBottomSheet()
            },
            pickIcon = {position,iconId->
                Log.d("Options sheet","Icon Click Received with data pos:$position and id: $iconId")
                optionSheetViewModel.updateState(false,position,iconId)
                showOptionsBottomSheet()
            },
        )
    }
    private fun setUpRecycleView(){
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=adapter
    }
    private fun setUpBottomSheet(){
        optionsBottomSheet= OptionsBottomSheet()
        optionsBottomSheet.listener=this
    }
    private fun showOptionsBottomSheet() {
        Log.d("Options sheet","Inside Show sheet")
        if (!optionsBottomSheet.isAdded) {
            Log.d("Options sheet","Showing Sheet")
            optionsBottomSheet.show(requireActivity().supportFragmentManager, "OptionsBottomSheet")
        }
    }
    private fun setUpNumberPicker(){
        binding.setTurnsTime.setOnClickListener{
            optionSheetViewModel.updateState(false,0,binding.numberPicker.text.toString().toInt()/10,true)
            showOptionsBottomSheet()
        }
    }
    override fun onCardsOptionSelected(isColor: Boolean, position:Int, id: Int) {
        if(isColor){
            optionSheetViewModel.setColors.remove(viewModel.cards[position].color)
            optionSheetViewModel.setColors.add(id)
            viewModel.cards[position].color = id
            adapter.changeCardColor(position, id)
        }else{
            viewModel.cards[position].icon=id
            adapter.updateCardIcon(position,id)
        }
        hideOptionsBottomSheet()
    }

    override fun onTimeOptionSelected(value: Int) {
        binding.numberPicker.text=value.toString()
        hideOptionsBottomSheet()
    }

    private fun hideOptionsBottomSheet() {
        optionsBottomSheet.dismiss()
    }

    private fun createGameRoom(){
        val hostData=userRepository.user!!
        lifecycleScope.launch {
            val result=viewModel.createGameRoom(hostData,binding.numberPicker.text.toString().toInt())
            if(result){
                findNavController().navigate(R.id.action_game_settings_to_waiting_room)
            }else{
                Log.d("GameSettings","Error Creating game room")
            }
        }
    }
}