package com.tcs.games.score4.ui.gameroom

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.tcs.games.score4.R
import com.tcs.games.score4.bot.ProbabilityBot
import com.tcs.games.score4.databinding.FragmentGameRoomBinding
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.ui.gameroom.adapter.LargeCardAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import com.tcs.games.score4.utils.views.CustomCard

@AndroidEntryPoint
class GameRoomFragment:Fragment() {
    private var _binding:FragmentGameRoomBinding?=null
    private val binding get()=_binding!!
    private val viewModel:GameRoomViewModel by viewModels()
    private val timerViewModel:PlayerTimerViewModel by viewModels()
    private lateinit var adapter: LargeCardAdapter
    private lateinit var handler:Handler
    private lateinit var runnable: Runnable
    private val job:Job by lazy { Job() }
    private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO + job) }
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
        setUpPlayerIcons()
        setUpOnClickListeners()
        setUpMiniCardsObserver()
        setUpPlayerStateObserver()
    }

    override fun onStart() {
        super.onStart()
//        handler.postDelayed(runnable,3000)
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        _binding=null
    }
    private fun setUpPlayerIcons(){
        val gameRoom=viewModel.getGameRoom().value
        if(gameRoom!=null){
            val players=gameRoom.players
            players.forEachIndexed { index, player ->
                if(index<4) {
                    val view = viewModel.getPlayerIcon(index, binding)
                    view.setIcon(player.playerProfile)
                }
            }
        }
        viewModel.getGameRoom().observe(viewLifecycleOwner){game->
            Log.d("GameFinishedDialog","Observer triggered")
            if(game!=null) {
                if (game.winner >= 0) {
                    updateUserWon(game.winner)
                    Log.d("GameFinishedDialog","Winner is ${game.winner}")
                    findNavController().navigate(R.id.action_game_room_to_game_finished)
                }
            }
        }
    }
    private fun updateUserWon(userIndex:Int){
        if(userIndex==viewModel.userIndex){
            viewModel.updateGameFinishedStats(true)
        }else{
            viewModel.updateGameFinishedStats(false)
        }
    }

    private fun setUpMiniCardsObserver(){
        viewModel.startListeningToDeck()
        viewModel.getDeck().observe(viewLifecycleOwner){deck->
            Log.d("DeckObserver","Data Changed")
            if(deck!=null){
                timerViewModel.cancelCountdown()
                when(deck.currentlyPlaying){
                    0->{
                        timerViewModel.updateState(GameRoomCurrentlyPlayingState.Player1.Progress(100))
                    }
                    1-> {
                        timerViewModel.updateState(GameRoomCurrentlyPlayingState.Player2.Progress(100))
                    }
                    2-> {
                        timerViewModel.updateState(GameRoomCurrentlyPlayingState.Player3.Progress(100))
                    }
                    else-> {
                        timerViewModel.updateState(GameRoomCurrentlyPlayingState.Player4.Progress(100))
                    }
                }
                val myDeck=when(viewModel.userIndex){
                    0->{
                        deck.playerA
                    }
                    1-> {
                        deck.playerB
                    }
                    2-> {
                        deck.playerC
                    }
                    else-> {
                        deck.playerD
                    }
                }
                if(viewModel.isUserCurrentlyPlaying()){
                    timerViewModel.startCountdown(viewModel.getTurnTime(false))
                    binding.swipeAllowed.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_keyboard_double_arrow_up_24))
                }else{
                    timerViewModel.startCountdown(viewModel.getTurnTime(true))
                    binding.swipeAllowed.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_lock_outline_24))
                }
                Log.d("Deck","${viewModel.userIndex}")

                //Setting up turn systems
                Log.d("Deck Observer","${viewModel.previousDeck.toString()} and current deck: ${myDeck.toString()}")
                if(viewModel.previousDeck != myDeck){
                    if(viewModel.checkIfWon(myDeck)){
                        Log.d("winner","setting winner")
                        setWinner(viewModel.userIndex)
                    }
                    viewModel.previousDeck.clear()
                    viewModel.previousDeck.addAll(myDeck)
                    Log.d("Deck Observer","Updating previous Deck")
                    binding.cardsContainer.removeAllViews()
                    val temp=prepareCardsFromIds(myDeck)
                    setUpLargeCards(temp)
                    temp.forEachIndexed{index,id->
                        val card=prepareCardFromId(id)
                        card.setOnClickListener{
                            binding.cards.setCurrentItem(index+1,true)
                        }
                        binding.cardsContainer.addView(card)
                    }
                }
                if(!viewModel.currentlySelectedItem.hasObservers()){
                    setUpMiniCardsState()
                }
                if(viewModel.isUserHost()){
                    if(viewModel.getGameRoom().value!!.players[deck.currentlyPlaying].bot){
                        lifecycleScope.launch {
                            if(viewModel.checkIfWon(myDeck)){
                                setWinner(deck.currentlyPlaying)
                            }
                            val index=ProbabilityBot().start(myDeck).await()
                            Handler(Looper.getMainLooper()).postDelayed(
                                {removeCardAndEndTurn(index,true)},
                                3000
                            )
                        }
                    }
                }
            }
        }
    }
    private fun setWinner(current:Int){
        viewModel.setWinner(current)
    }
    private fun setUpPlayerStateObserver(){
        lifecycleScope.launch {
            timerViewModel.state.collect { state ->
                when(state){
                    is GameRoomCurrentlyPlayingState.Player1.Progress->{
                        binding.playerA.setProgress(state.progress)
                    }
                    is GameRoomCurrentlyPlayingState.Player1.TimeOut->{
                        removeCardAndEndTurn(0)
                    }
                    is GameRoomCurrentlyPlayingState.Player2.Progress->{
                        binding.playerB.setProgress(state.progress)
                    }
                    is GameRoomCurrentlyPlayingState.Player2.TimeOut->{
                        removeCardAndEndTurn(0)
                    }
                    is GameRoomCurrentlyPlayingState.Player3.Progress->{
                        binding.playerC.setProgress(state.progress)
                    }
                    is GameRoomCurrentlyPlayingState.Player3.TimeOut->{
                        removeCardAndEndTurn(0)
                    }
                    is GameRoomCurrentlyPlayingState.Player4.Progress->{
                        binding.playerD.setProgress(state.progress)
                    }
                    is GameRoomCurrentlyPlayingState.Player4.TimeOut->{
                        removeCardAndEndTurn(0)
                    }
                }
            }
        }
    }
    private fun setUpMiniCardsState(){
        viewModel.currentlySelectedItem.observe(viewLifecycleOwner){position->
            binding.cardsContainer.getChildAt(position)?.isSelected=true
            if(viewModel.previouslySelectedItem >=0)
                binding.cardsContainer.getChildAt(viewModel.previouslySelectedItem)?.isSelected=false
            viewModel.previouslySelectedItem=position
        }
    }
    private fun prepareCardsFromIds(ids:MutableList<String>):MutableList<CardInfo>{
        return viewModel.getCardsDetailsFromIds(ids)
    }
    private fun prepareCardFromId(details:CardInfo):CustomCard{
        val customCard=CustomCard(requireContext())
        val params = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val marginInPx = resources.getDimensionPixelSize(R.dimen.margin_4dp)
        params.setMargins(marginInPx, marginInPx, marginInPx, marginInPx)
        customCard.setUpView(details)
        return customCard
    }
    private fun setUpOnClickListeners(){
        binding.imageButtonGameRoomUp.setOnClickListener{
            warnPlayer()
        }

    }
    private fun warnPlayer(){
        findNavController().navigateUp()
    }
    private fun setUpLargeCards(cards:MutableList<CardInfo>){
        adapter=LargeCardAdapter(cards,requireContext(),viewModel.getRoomId(),requireActivity().application, coroutineScope)
        binding.cards.adapter=adapter
        val recyclerView = binding.cards.getChildAt(0) as RecyclerView
        //Clipping Children can be disabled
        (binding.cards.parent as ViewGroup).clipChildren = false


        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            private var isSwiping = false

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return if(viewModel.isUserCurrentlyPlaying()){
                    makeMovementFlags(0, ItemTouchHelper.UP)
                }else{
                    0
                }
                // Allow only upward swipe, disable downward swipe
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
                // Handle the upward swipe (removal)
                if (direction == ItemTouchHelper.UP) {
                    val position = viewHolder.adapterPosition
                    fadeOutAndRemove(viewHolder, position)
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
                // Apply fade effect while swiping upward
                val alpha = 1.0f - (Math.abs(dY) / viewHolder.itemView.height)
                viewHolder.itemView.alpha = alpha
                viewHolder.itemView.translationY = dY

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)
                // Reset alpha and translation if swipe is canceled
                viewHolder.itemView.alpha = 1.0f
                viewHolder.itemView.translationY = 0.0f
            }

            private fun fadeOutAndRemove(viewHolder: RecyclerView.ViewHolder, position: Int) {
                val itemView = viewHolder.itemView
                itemView.animate()
                    .alpha(0.0f)
                    .setDuration(300) // Duration of fade-out animation
                    .withEndAction {
                        removeCardAndEndTurn(position-1)
                        // Remove the item from the adapter after the animation
                        //Trigger item removal from small cards container
                        //Update firebase and disable item touch listener
                    }.start()
            }
        })

// Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(recyclerView)
        binding.cards.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("Adapter","current position: $position item count: ${adapter.itemCount}")
                when (position) {
                    0 -> { // If the user reaches the first phantom item
                        Log.d("Adapter","switching to ${adapter.itemCount-2}")
                        binding.cards.setCurrentItem(adapter.itemCount-2, false) // Jump to the last real item
                        viewModel.currentlySelectedItem.value=adapter.itemCount-3
                    }
                    adapter.itemCount-1 -> { // If the user reaches the last phantom item
                        Log.d("Adapter","switching to 1")
                        binding.cards.setCurrentItem(1, false) // Jump to the first real item
                        viewModel.currentlySelectedItem.value=0
                    }
                    else->{
                        viewModel.currentlySelectedItem.value=position-1
                    }
                }
            }
        })
        binding.cards.setCurrentItem(1, false)
    }
    private fun removeCardAndEndTurn(position:Int,forBot:Boolean=false) {

        val deck = if(forBot) viewModel.modifyDeckForBot(position) else viewModel.modifyDeckForPlayer(position)
        if(!forBot) {
            adapter.removeItem(position)
        }
        viewModel.uploadDeck(deck) { done ->
            binding.swipeAllowed.setImageDrawable(ContextCompat.getDrawable(requireContext(),R.drawable.baseline_lock_outline_24))
            Log.d("Deck done", done.toString())
        }
    }



}