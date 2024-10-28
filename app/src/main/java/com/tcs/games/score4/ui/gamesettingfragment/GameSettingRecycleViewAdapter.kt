package com.tcs.games.score4.ui.gamesettingfragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.databinding.FragmentGameSettingsItemBinding

class GameSettingRecycleViewAdapter:
    RecyclerView.Adapter<GameSettingRecycleViewAdapter.ViewHolder>() {
    inner class ViewHolder(binding:FragmentGameSettingsItemBinding):RecyclerView.ViewHolder(binding.root){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=FragmentGameSettingsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }
}