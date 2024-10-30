package com.tcs.games.score4.ui.waitingroom

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentWaitingRoomBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaitingRoomFragment:Fragment() {
    private var _binding:FragmentWaitingRoomBinding?=null
    private val binding get()=_binding!!
    private lateinit var handler: Handler
    private lateinit var navigateRunnable: Runnable
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
    }

    override fun onStart() {
        super.onStart()
        handler = Handler(Looper.getMainLooper())

        // Define the Runnable
        navigateRunnable = Runnable {
            findNavController().navigate(R.id.action_waiting_room_to_game_room)
        }

        // Post the Runnable with a delay
        handler.postDelayed(navigateRunnable, 4000)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(navigateRunnable)
    }
    private fun setOnClickListeners(){
        binding.fragmentWaitingRoomTvGameDetails.setOnClickListener{
            handler.removeCallbacks(navigateRunnable)
            findNavController().navigate(R.id.action_waiting_room_to_game_details)
        }
    }

}