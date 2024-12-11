package com.tcs.games.score4.ui.gamesettingfragment

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentOptionsBottomSheetBinding
import dagger.hilt.android.AndroidEntryPoint
import data.defaults.DefaultCardOptions
import kotlinx.coroutines.launch
import model.gamesettings.OptionsSheetUiState
import javax.inject.Inject

@AndroidEntryPoint
class OptionsBottomSheet : BottomSheetDialogFragment() {

    interface OptionsBottomSheetListener {
        fun onCardsOptionSelected(isColor:Boolean, position:Int, id:Int) // Define return type according to your needs
        fun onTimeOptionSelected(value:Int)
    }

    private var _binding:FragmentOptionsBottomSheetBinding?=null
    private val binding get()=_binding!!
    private val viewModel:OptionSheetViewModel by activityViewModels()
    var listener: OptionsBottomSheetListener?=null
    @Inject
    lateinit var cardsOptions:DefaultCardOptions

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("OptionsSheet", "Created")
        _binding=FragmentOptionsBottomSheetBinding.inflate(inflater,container,false)
        val root=binding.root
        return root
    }

    override fun onStart() {
        super.onStart()
        Log.d("OptionsSheet", "Started")
    }

    override fun onResume() {
        Log.d("OptionsSheet", "Resumed")
        super.onResume()
        viewModel.byUser=true
    }
    override fun onStop() {
        Log.d("OptionsSheet", "Stopped")
        super.onStop()
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.d("OptionsSheet","Dismissed")
        super.onDismiss(dialog)
        viewModel.byUser=false
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect{state->
                    when(state){
                        is OptionsSheetUiState.Icons->{
                            binding.textViewType.text="Select Colors"
                            binding.optionsSheetSelectIcons.visibility=View.VISIBLE
                            binding.optionsSheetSelectColors.visibility=View.GONE
                            binding.selectTurnsTime.visibility=View.GONE
                            viewModel.position=state.position
                        }
                        is OptionsSheetUiState.Colors->{
                            binding.textViewType.text="Select Icons"
                            binding.optionsSheetSelectIcons.visibility=View.GONE
                            binding.optionsSheetSelectColors.visibility=View.VISIBLE
                            binding.selectTurnsTime.visibility=View.GONE
                            binding.colorsRadioGroup.check(cardsOptions.getColoursRadioButtonId(state.colorId))
                            viewModel.position=state.position
                            val list=viewModel.setColors.toMutableList()
                            list.remove(state.colorId)
                            for(id in list){
                                binding.colorsRadioGroup.removeView(binding.colorsRadioGroup.findViewById(cardsOptions.getColoursRadioButtonId(id)))
                            }
                        }
                        is OptionsSheetUiState.Time->{
                            binding.textViewType.text="Select Time for Turns"
                            binding.optionsSheetSelectIcons.visibility=View.GONE
                            binding.optionsSheetSelectColors.visibility=View.GONE
                            binding.selectTurnsTime.visibility=View.VISIBLE
                            binding.selectTurnsTime.check(cardsOptions.getTurnsRadioButtonId(state.numId))
                        }
                    }
                }
            }
        }
        setUpViews()
    }

    private fun setUpViews(){
        binding.optionsSheetCancel.setOnClickListener{
            dismiss()
        }
        setUpRadioGroup()
        val gridChildren=binding.optionsSheetSelectIcons.children
        gridChildren.forEachIndexed { index, view ->
            view.setOnClickListener{
                listener?.onCardsOptionSelected(false,viewModel.position,index+1)
            }
        }

    }
    private fun setUpRadioGroup(){
        binding.colorsRadioGroup.setOnCheckedChangeListener{_,checkedId->
            Log.d("OptionsSheet","Listener Triggered 1 ${viewModel.byUser}")
            if(viewModel.byUser) {
                Log.d("OptionsSheet","Listener Triggered 2 ${viewModel.byUser}")
                when (checkedId) {
                    R.id.card_color_option_1 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position, 1)
                    }

                    R.id.card_color_option_2 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position, 2)
                    }

                    R.id.card_color_option_3 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position, 3)
                    }

                    R.id.card_color_option_4 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position, 4)
                    }

                    R.id.card_color_option_5 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position, 5)
                    }

                    R.id.card_color_option_6 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position, 6)
                    }

                    R.id.card_color_option_7 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position ,7)
                    }

                    R.id.card_color_option_8 -> {
                        listener?.onCardsOptionSelected(true,viewModel.position, 8)
                    }
                    else -> {
                    }
                }
            }

        }
        binding.selectTurnsTime.setOnCheckedChangeListener { _, checkedId ->
            if(viewModel.byUser){
                var num=20
                when(checkedId){
                    R.id.seconds_10->num=20
                    R.id.seconds_20->num=30
                    R.id.seconds_30->num=40
                    R.id.seconds_50->num=50
                }
                listener?.onTimeOptionSelected(num)
            }
        }
    }



}