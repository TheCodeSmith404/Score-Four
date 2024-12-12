package com.tcs.games.score4.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentWaitingRoomPlayerItemBinding

class WaitingRoomItem @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private var binding= FragmentWaitingRoomPlayerItemBinding
        .inflate(LayoutInflater.from(context),
            this, true)

    enum class PlayerState {
        WAITING_TO_JOIN,
        PLAYER_JOINED,
        IS_READY,
        ADD_BOT,
    }

    init {
        inflate(
            context,
            R.layout.fragment_waiting_room_player_item,
            this
        )
        // Retrieve and apply custom attribute for the text
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WaitingRoomItem,
            0, 0
        ).apply {
            try {
                val playerText = getString(R.styleable.WaitingRoomItem_playerName)?: ""
                binding.playerName.text = playerText  // Ensure your TextView ID is correct here
            } finally {
                recycle()
            }
        }
    }
    fun setState(state:PlayerState){
        when(state){
            PlayerState.WAITING_TO_JOIN -> {
                binding.addBot.visibility=View.GONE
                binding.waitingRoomPlayerProfileContainer.visibility=View.VISIBLE
                binding.waitingRoomPlayerProfile.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.baseline_person_24))
                binding.waitingRoomWaitingToJoin.visibility= View.VISIBLE
                binding.waitingRoomPlayerStatus.visibility=View.GONE
                setText("Waiting to Join")
            }
            PlayerState.PLAYER_JOINED-> {
                binding.addBot.visibility=View.GONE
                binding.waitingRoomPlayerProfileContainer.visibility=View.VISIBLE
                binding.waitingRoomWaitingToJoin.visibility=View.GONE
                binding.waitingRoomPlayerStatus.visibility=View.VISIBLE
                binding.waitingRoomPlayerStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.round_warning_amber_24))
            }
            PlayerState.IS_READY-> {
                binding.waitingRoomPlayerProfileContainer.visibility=View.VISIBLE
                binding.addBot.visibility=View.GONE
                binding.waitingRoomPlayerStatus.visibility=View.VISIBLE
                binding.waitingRoomWaitingToJoin.visibility=View.GONE
                binding.waitingRoomPlayerStatus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.outline_check_circle_outline_24))
            }
            PlayerState.ADD_BOT->{
                binding.waitingRoomPlayerProfileContainer.visibility=View.GONE
                binding.addBot.visibility=View.VISIBLE
            }
        }
    }
    fun setText(text:String){
        binding.playerName.text=text
    }
    fun getImageView():ImageView{
        return binding.waitingRoomPlayerProfile
    }
    fun getAddBotContainer(): CardView {
        return binding.addBot
    }


}