package com.tcs.games.score4.ui.waitingroom

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.databinding.DialogViewGameDetailsCardItemBinding

class GameDetailsAdapter:RecyclerView.Adapter<GameDetailsAdapter.ViewHolder>() {
    inner class ViewHolder(binding:DialogViewGameDetailsCardItemBinding):RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=DialogViewGameDetailsCardItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 4;
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

}