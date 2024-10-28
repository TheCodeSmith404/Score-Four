package com.tcs.games.score4.ui.gameroom.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.databinding.FragmentGameRoomCardLargeItemBinding
import com.tcs.games.score4.databinding.FragmentGameSettingsBinding

class LargeCardAdapter:RecyclerView.Adapter<LargeCardAdapter.ViewHolder>() {
    inner class ViewHolder(binding:FragmentGameRoomCardLargeItemBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=FragmentGameRoomCardLargeItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 5
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}