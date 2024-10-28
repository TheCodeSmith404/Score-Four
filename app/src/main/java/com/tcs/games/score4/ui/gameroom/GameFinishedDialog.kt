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

class GameFinishedDialog: DialogFragment() {
    private var _binding:DialogGameFinishedBinding?=null
    private val binding get()=_binding!!
    private lateinit var handler: Handler
    private lateinit var navigateRunnable: Runnable
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
        handler = Handler(Looper.getMainLooper())

        // Define the Runnable
        navigateRunnable = Runnable {
            dismiss()
            findNavController().navigate(R.id.action_game_finished_to_waiting_room)
        }

        // Post the Runnable with a delay
        handler.postDelayed(navigateRunnable, 2000)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(navigateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    private fun setOnClickListener(){
        binding.dialogGameFinished.setOnClickListener{
            dismiss()
            findNavController().navigate(R.id.action_game_finished_to_fragment_home)
        }
    }
}