package com.tcs.games.score4.ui.waitingroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tcs.games.score4.databinding.DialogViewGameDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

@AndroidEntryPoint
class DialogViewGameDetails:DialogFragment() {
    private var _binding:DialogViewGameDetailsBinding?= null
    private val binding get()=_binding!!
    private val viewModel:DialogViewGameDetailsViewModel by viewModels()
    private val job: Job by lazy{ Job() }
    private val coroutineScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.IO + job) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=DialogViewGameDetailsBinding.inflate(layoutInflater,container,false)
        val root=binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListeners()
        setUpRecycleView()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    private fun setOnClickListeners(){
        binding.dialogViewGameDetailsExit.setOnClickListener{
            dismiss()
        }
    }
    private fun setUpRecycleView(){
        val cards=viewModel.getGameInformation()
        binding.recyclerView2.adapter=GameDetailsAdapter(requireActivity().application,requireContext(),cards.first, coroutineScope,cards.second)
        binding.recyclerView2.layoutManager=GridLayoutManager(requireContext(),2)
        setUpTv(cards.third)
    }
    private fun setUpTv(text:String){
        binding.textViewTpt.text=text
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}