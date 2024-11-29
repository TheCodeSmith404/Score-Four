package com.tcs.games.score4.ui.gameroom.adapter

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.databinding.FragmentGameRoomCardLargeItemBinding
import com.tcs.games.score4.databinding.FragmentGameSettingsBinding
import data.defaults.DefaultCardOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.ImagesData
import model.gameroom.CardInfo
import utils.ImageUtils
import utils.constants.ImageNames

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
            coroutineScope.launch {
                ImageUtils.loadCardImageFromCacheDir(
                    application,
                    "${ImageNames.CARD.txt}${data.imageRes}",
                    gameId,
                    image
                )
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