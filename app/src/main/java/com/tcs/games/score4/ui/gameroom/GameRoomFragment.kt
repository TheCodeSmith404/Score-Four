package com.tcs.games.score4.ui.gameroom

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentGameRoomBinding
import com.tcs.games.score4.ui.gameroom.adapter.LargeCardAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameRoomFragment:Fragment() {
    private var _binding:FragmentGameRoomBinding?=null
    private val binding get()=_binding!!
    private val viewModel:GameRoomViewModel by viewModels()
    private lateinit var handler:Handler
    private lateinit var runnable: Runnable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentGameRoomBinding.inflate(layoutInflater,container,false)
        val root=binding.root
        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLargeCards()
        setUpOnClickListeners()
    }

    override fun onStart() {
        super.onStart()
        handler=Handler(Looper.getMainLooper())
        runnable=Runnable{
            findNavController().navigate(R.id.action_game_room_to_game_finished)
        }
//        handler.postDelayed(runnable,3000)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacks(runnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
    private fun setUpOnClickListeners(){

    }
    private fun setUpLargeCards(){
        binding.cards.adapter=LargeCardAdapter()
        val recyclerView = binding.cards.getChildAt(0) as RecyclerView

        //Clipping Children can be disabled
        (binding.cards.parent as ViewGroup).clipChildren = false

        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            private var isSwiping = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Allow only upward swipe, disable downward swipe
                return makeMovementFlags(0, ItemTouchHelper.UP)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Disable item moving (drag and drop)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Handle the upward swipe (removal or modification)
                val position = viewHolder.adapterPosition
                if (direction == ItemTouchHelper.UP) {
                    Toast.makeText(requireContext(),"Moved UP",Toast.LENGTH_SHORT).show()  // Action for upward swipe
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                // Detect swipe start and stop
                if (isCurrentlyActive && !isSwiping) {
                    isSwiping = true
                    onSwipeStarted(viewHolder)
                }

                if (!isCurrentlyActive && isSwiping) {
                    isSwiping = false
                    onSwipeStopped(viewHolder)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            private fun onSwipeStarted(viewHolder: RecyclerView.ViewHolder) {
                // Handle swipe start (custom logic)
                println("Swipe started on position: ${viewHolder.adapterPosition}")
            }

            private fun onSwipeStopped(viewHolder: RecyclerView.ViewHolder) {
                // Handle swipe stop without completion (custom logic)
                println("Swipe stopped on position: ${viewHolder.adapterPosition}")
            }
        })



        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}