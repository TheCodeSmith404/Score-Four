package com.tcs.games.score4.ui.uploadedimages

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.tcs.games.score4.databinding.FragmentUploadImageFooterItemBinding
import com.tcs.games.score4.databinding.FragmentUploadImageItemBinding
import data.defaults.DefaultCardOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import utils.ImageUtils
import utils.constants.ImageNames

class UploadedImagesAdapter(
    private val applicationContext:Application,
    private val context: Context,
    private var imageData:MutableList<Int>,
    private var cardsData:MutableMap<Int,Pair<String,Int>>,
    private val maxImageCount:Int,
    private val coroutineScope: CoroutineScope,
    private val adapterClickListener: OnUploadImageAdapterClickListener,
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        imageData.forEach{data->
            if(cardsData[data]==null) {
                Log.d("UploadImg", cardsData[data].toString())
                cardsData[data] = Pair("", 0)
            }
            else{
            Log.d("UploadImg", cardsData[data].toString())
            }
        }
    }


    companion object {
        const val FOOTER = 0
        const val ITEM = 1
    }
    private var hasAddImageFunctionality=false

    class ItemViewHolder(binding: FragmentUploadImageItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val view=binding.root
        val backGround=binding.backgroundColor
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
            Log.d("Adapter Null Pointer","$position Footer Item")
            FOOTER
        }else{
            Log.d("Adapter Null Pointer","$position Item")
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
                val id=imageData[position]
            Log.d("Null pointer",id.toString())
                Log.d("Null pointer",cardsData[id].toString())
                Log.d("Null pointer",cardsData.entries.toString())
                coroutineScope.launch {
                    ImageUtils.loadCardImageFromInternalStorage(
                        applicationContext,
                        "${ImageNames.CARD.txt}$id",
                        holder.cardImage
                    )
                }
                if(cardsData[id]==null){
                    cardsData[id]=Pair("",0)
                }
                if(cardsData[id]?.first!=""){
                    holder.cardName.text="Card ${cardsData[id]?.first}"
                    holder.cardName.visibility=View.VISIBLE
                    holder.backGround.setBackgroundColor(DefaultCardOptions.getColor(cardsData[id]!!.second,false))
                }else{
                    holder.backGround.setBackgroundColor(Color.rgb(99,99,99))
                    holder.cardName.text=""
                    holder.cardName.visibility= View.INVISIBLE
                }
                Log.d("cardImage","card_image$id")
                holder.view.setOnClickListener{
                    adapterClickListener.onItemClick(position,id)
                }
                holder.deleteButton.setOnClickListener{
                    adapterClickListener.onDeleteClick(position,id)
                }
            }
        }
    }
    fun updateCardItem(position: Int,imageId:Int,cardInitial:String,cardColor:Int){
        cardsData.entries.forEach { entry ->
            if (entry.value.first == cardInitial) {
                cardsData[entry.key] = Pair("", 0) // Reset to default value
                val tempPos=imageData.indexOf(entry.key)
                if(tempPos>=0)
                    notifyImageItemChanged(tempPos)
            }
        }
        cardsData[imageId]=Pair(cardInitial,cardColor)
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateImageData(images:MutableList<Int>,newId:Int){
        Log.d("Null pointer","ImageData has been changed dudes")
        Log.d("Null pointer",images.toString())
        imageData=images
        Log.d("Null pointer",cardsData.entries.toString())
        cardsData[newId]=Pair("",0)
        Log.d("Null pointer",cardsData.entries.toString())
        notifyDataSetChanged()
    }
    fun updateImageItemRemoved(position: Int){
        imageData.remove(position)
        notifyItemRemoved(position)
    }
    fun notifyImageItemChanged(position: Int){
        notifyItemChanged(position)
    }

}