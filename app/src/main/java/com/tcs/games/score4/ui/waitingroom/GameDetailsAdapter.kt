package com.tcs.games.score4.ui.waitingroom

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogViewGameDetailsCardItemBinding
import data.defaults.DefaultCardOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.gameroom.CardInfo
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.constants.ImageNames

class GameDetailsAdapter(
    private val application: Application,
    private val context: Context,
    private val cards:List<CardInfo>,
    private val coroutineScope: CoroutineScope,
    private val gameId:String,
): RecyclerView.Adapter<GameDetailsAdapter.ViewHolder>()
{
    inner class ViewHolder(binding:DialogViewGameDetailsCardItemBinding):RecyclerView.ViewHolder(binding.root){
        private val icon=binding.cardIcon
        private val text=binding.cardText
        private val image=binding.dialogViewCardImage
        private val card=binding.cardTint
        fun setTint(colorId:Int){
            val color=DefaultCardOptions.getColor(colorId,false)
            val colorLight=DefaultCardOptions.getColor(colorId,true)
            text.setBackgroundColor(colorLight)
            card.strokeColor=color
            icon.setColorFilter(color,android.graphics.PorterDuff.Mode.SRC_IN)
        }
        fun setIcon(iconId:Int){
            icon.setImageDrawable(DefaultCardOptions.getDrawable(context,iconId))
        }
        fun setName(name:String){
            text.text=name
        }
        fun setImage(imageId:Int){
            if(imageId<=0){
                loadDefaultImage(getDefaultImageId(imageId))
            }else{
                loadUploadedImage(imageId,gameId)
            }
        }
        private fun loadDefaultImage(id:Int){
            Glide.with(context)
                .load(id)  // Resource ID for WebP image
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache on disk for future use
                .skipMemoryCache(false)  // Allow Glide to use memory cache for faster loading
                .centerCrop()
                .into(image)
        }
        private fun loadUploadedImage(id:Int,gameId:String){
            coroutineScope.launch {
                ImageUtils.loadCardImageFromCacheDir(
                    application,
                    "${ImageNames.CARD.txt}$id",
                    gameId,
                    image,
                )
            }
        }
        private fun getDefaultImageId(index:Int):Int{
            return when(index){
                0-> R.drawable.i0
                -1-> R.drawable.i1
                -2-> R.drawable.i2
                else-> R.drawable.i3
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=DialogViewGameDetailsCardItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card=cards[position]
        holder.setTint(card.color)
        holder.setName(card.name)
        holder.setIcon(card.icon)
        holder.setImage(card.imageRes)
    }

}