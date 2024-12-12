package com.tcs.games.score4.ui.gameroom.adapter

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tcs.games.score4.R
import com.tcs.games.score4.data.defaults.DefaultCardOptions
import com.tcs.games.score4.databinding.FragmentGameRoomCardLargeItemBinding
import com.tcs.games.score4.model.gameroom.CardInfo
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.constants.ImageNames
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LargeCardAdapter(private var list:MutableList<CardInfo>,private val context: Context,private val gameId:String,private val application: Application,private val coroutineScope: CoroutineScope):RecyclerView.Adapter<LargeCardAdapter.ViewHolder>() {
    inner class ViewHolder(binding:FragmentGameRoomCardLargeItemBinding):RecyclerView.ViewHolder(binding.root){
        private val image=binding.dialogViewCardImage
        private val icon=binding.cardIcon
        private val name=binding.cardName
        private val card=binding.card
        fun bind(data:CardInfo,position: Int){
            name.text=data.name
            icon.setImageDrawable(DefaultCardOptions.getDrawable(context,data.icon))
            icon.setColorFilter(DefaultCardOptions.getColor(data.color,false))
            name.setBackgroundColor(DefaultCardOptions.getColor(data.color,true))
            card.strokeColor=DefaultCardOptions.getColor(data.color,false)
            if(data.imageRes>0) {
                coroutineScope.launch {
                    ImageUtils.loadCardImageFromCacheDir(
                        application,
                        "${ImageNames.CARD.txt}${data.imageRes}",
                        gameId,
                        image
                    )
                }
            }else{
                loadDefaultImage(getDefaultImageId(card.id))
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
        val binding=FragmentGameRoomCardLargeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size+2
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position==0){
            val data=list.last()
            holder.bind(data,position)
        }else if(position==list.size+1){
            val data=list.first()
            holder.bind(data,position)
        }else{
            val data=list[position-1]
            holder.bind(data,position)
        }
    }
    fun getItem(position: Int):CardInfo{
        return list[position-2]
    }

    fun removeItem(position: Int){
        list.removeAt(position)
        notifyItemRemoved(position)
    }
}