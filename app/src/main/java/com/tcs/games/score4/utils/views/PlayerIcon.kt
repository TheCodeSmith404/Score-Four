package com.tcs.games.score4.utils.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.GameRoomPlayerIconItemBinding

class PlayerIcon @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    // Inflate and attach the view binding
    private var binding: GameRoomPlayerIconItemBinding =
        GameRoomPlayerIconItemBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // Retrieve and apply custom attribute for the text
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlayerIcon,
            0, 0
        ).apply {
            try {
                val playerText = getString(R.styleable.PlayerIcon_playerText) ?: ""
                binding.tv.text = playerText  // Ensure your TextView ID is correct here
            } finally {
                recycle()
            }
        }
    }
    fun setProgress(value:Int){
        binding.progress.progress=value
    }

    fun setIcon(url:String){
        val drawable=ContextCompat.getDrawable(context,R.drawable.baseline_person_24)
        if(url=="") {
            binding.imageView4.setImageDrawable(drawable)
        }else{
            Glide.with(context)
                .load(url)
                .placeholder(drawable)
                .error(drawable)
                .into(binding.imageView4)
        }
    }
}
