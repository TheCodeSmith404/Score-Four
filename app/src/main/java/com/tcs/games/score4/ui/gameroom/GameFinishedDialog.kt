package com.tcs.games.score4.ui.gameroom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogGameFinishedBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFinishedDialog: DialogFragment() {
    private var _binding:DialogGameFinishedBinding?=null
    private val binding get()=_binding!!
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
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

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

    }
}