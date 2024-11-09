package com.tcs.games.score4.ui.gamesettingfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentGameSettingsBinding
import com.tcs.games.score4.ui.gamesettingfragment.GameSettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameSettingFragment:Fragment() {
    private var _binding: FragmentGameSettingsBinding?= null
    private val binding get() = _binding!!
    private val viewModel:GameSettingViewModel by viewModels()
    private val optionSheetViewModel:OptionSheetViewModel by activityViewModels()
    private lateinit var adapter: GameSettingRecycleViewAdapter

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
        setUpAdapter()
        setOnClickListeners()
        setUpRecycleView()
    }
    private fun setOnClickListeners(){
        binding.fragmentGameSettingsCreate.setOnClickListener{
            findNavController().navigate(R.id.action_game_settings_to_waiting_room)
        }
    }
    private fun setUpAdapter(){
        adapter= GameSettingRecycleViewAdapter(
            cards = viewModel.cards,
            onEditClick = { position ->
                viewModel.cards[position].isEdit=true
            },
            onPreViewClick = { position ->

            },
            onNameChange = { position, name ->
                viewModel.cards[position].name=name
            },
            pickColor = {position,colorId->
                
            },
            pickIcon = {position,iconId->

            },
        )
    }
    private fun setUpRecycleView(){
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=adapter
    }
}