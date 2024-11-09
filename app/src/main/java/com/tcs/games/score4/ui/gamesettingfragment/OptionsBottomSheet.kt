package com.tcs.games.score4.ui.gamesettingfragment

import android.graphics.Path.Op
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tcs.games.score4.databinding.FragmentGameSettingsBinding
import com.tcs.games.score4.databinding.FragmentOptionsBottomSheetBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import model.gamesettings.OptionsSheetUiState

class OptionsBottomSheet : BottomSheetDialogFragment() {
    private var _binding:FragmentOptionsBottomSheetBinding?=null
    private val binding get()=_binding!!
    private val viewModel:OptionSheetViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentOptionsBottomSheetBinding.inflate(inflater,container,false)
        val root=binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect{state->
                    when(state){
                        is OptionsSheetUiState.Icons->{
                            binding.textViewType.text="Select Colors: ${state.iconId}"

                        }
                        is OptionsSheetUiState.Colors->{
                            binding.textViewType.text="Select Icons: ${state.colorId}"
                        }
                    }
                }
            }
        }
    }


}