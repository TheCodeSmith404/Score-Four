package com.tcs.games.score4.ui.uploadimages

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.databinding.FragmentUploadImageFooterItemBinding
import com.tcs.games.score4.databinding.FragmentUploadImageItemBinding
import utils.ImageUtils
import kotlin.math.max

class UploadImageAdapter(
    private val context: Context,
    private val imageData:MutableList<Any>,
    private val maxImageCount:Int,
    private val adapterClickListener: OnUploadImageAdapterClickListener,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    companion object {
        const val FOOTER = 0
        const val ITEM = 1
    }
    private var hasAddImageFunctionality=false

    class ItemViewHolder(binding: FragmentUploadImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val view=binding.root
        val cardImage=binding.cardImage
        val deleteButton=binding.deleteImage
        val cardName=binding.cardName
    }

    // Custom ViewHolder for Header
    class FooterViewHolder(binding: FragmentUploadImageFooterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val view=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            FOOTER -> {
                val binding = FragmentUploadImageFooterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FooterViewHolder(binding)
            }
            ITEM -> {
                val binding = FragmentUploadImageItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(hasAddImageFunctionality&&imageData.size==position){
            FOOTER
        }else{
            ITEM
        }
    }

    override fun getItemCount(): Int {
        if(imageData.size< maxImageCount){
            hasAddImageFunctionality=true
            return imageData.size+1
        }else{
            hasAddImageFunctionality=false
            return imageData.size
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is FooterViewHolder->{
                holder.view.setOnClickListener{
                    adapterClickListener.onFooterClick()
                }
            }
            is ItemViewHolder->{
                ImageUtils.loadImageUriIntoImageView(context,"String".toUri(),holder.cardImage)
                holder.deleteButton.setOnClickListener{
                    adapterClickListener.onDeleteClick(position,0)
                }
            }
        }
    }

}