package utils.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.cardview.widget.CardView
import com.google.android.material.card.MaterialCardView
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentGameRoomCardLargeItemBinding
import com.tcs.games.score4.databinding.FragmentGameRoomCardSmallItemBinding

class CustomCard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : MaterialCardView(context, attrs, defStyleAttr) {
    private var isSelectedState: Boolean = false
    private var binding: FragmentGameRoomCardSmallItemBinding

    init {
        inflate(
            context,
            R.layout.fragment_game_room_card_small_item,
            this
        ) // Inflate your custom layout
        binding = FragmentGameRoomCardSmallItemBinding.inflate(LayoutInflater.from(context))
        isSelectedState=false
        setOnClickListener {
            isSelectedState = !isSelectedState
            updateView()
        }
        updateView()
    }

    private fun updateView() {
        if (isSelectedState) {
            // Change to selected state
//            strokeColor=color // Change stroke color for selected
            binding.smallCardIcon.setColorFilter(
                context.getColor(R.color.purple_700),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            // Change to unselected state
//            strokeColor=color // Change stroke color for unselected
            binding.smallCardIcon.setColorFilter(
                context.getColor(R.color.black),
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