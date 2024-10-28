package com.tcs.games.score4.ui.waitingroom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.tcs.games.score4.databinding.DialogViewGameDetailsBinding

class DialogViewGameDetails:DialogFragment() {
    private var _binding:DialogViewGameDetailsBinding?= null
    private val binding get()=_binding!!
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
        binding.recyclerView2.adapter=GameDetailsAdapter()
        binding.recyclerView2.layoutManager=GridLayoutManager(requireContext(),2)
    }
}