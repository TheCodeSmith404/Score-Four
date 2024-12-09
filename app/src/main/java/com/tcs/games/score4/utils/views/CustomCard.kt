package com.tcs.games.score4.utils.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentGameRoomCardSmallItemBinding
import com.tcs.games.score4.data.defaults.DefaultCardOptions
import com.tcs.games.score4.model.gameroom.CardInfo

class CustomCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private var color=R.color.purple_700
    private var isSelectedState: Boolean = false
    private var binding: FragmentGameRoomCardSmallItemBinding

    init {
        val view = LayoutInflater.from(context).inflate(
            R.layout.fragment_game_room_card_small_item,
            this,
            true
        ) // Inflate directly into the CustomCard
        binding = FragmentGameRoomCardSmallItemBinding.bind(view) // Bind the inflated layout
        isSelectedState = false
        setOnClickListener {
            isSelectedState = !isSelectedState
            updateView()
        }
        updateView()
    }
    @SuppressLint("ResourceAsColor")
    fun setUpView(cardInfo: CardInfo){
        Log.d("Mini","setting up views with ${cardInfo.toString()}")
        color=DefaultCardOptions.getColor(cardInfo.color,false)
        binding.smallCardIcon.setImageDrawable(DefaultCardOptions.getDrawable(context,cardInfo.icon))
        binding.smallCardIcon.setColorFilter(color,PorterDuff.Mode.SRC_IN)
        binding.smallCard.strokeColor=color
        binding.smallCardBackground.setImageDrawable(ColorDrawable(color))
    }

    private fun updateView() {
        if (isSelectedState) {
            // Change to selected state
//            strokeColor=color // Change stroke color for selected
            binding.smallCardIcon.setColorFilter(
                context.getColor(R.color.black),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            // Change to unselected state
//            strokeColor=color // Change stroke color for unselected
            binding.smallCardIcon.setColorFilter(
                color,
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    override fun isSelected(): Boolean {
        return isSelectedState
    }

    override fun setSelected(selected: Boolean) {
        isSelectedState = selected
        updateView()
    }
}