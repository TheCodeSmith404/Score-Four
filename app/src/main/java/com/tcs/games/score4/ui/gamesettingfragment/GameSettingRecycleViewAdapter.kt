package com.tcs.games.score4.ui.gamesettingfragment

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.FragmentGameSettingsItemBinding
import data.defaults.DefaultCardOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import model.gamesettings.CardInfoAdapter
import com.tcs.games.score4.utils.ImageUtils
import com.tcs.games.score4.utils.constants.ImageNames

class GameSettingRecycleViewAdapter(
    private val application:Application,
    private val context:Context,
    private val cards:List<CardInfoAdapter>,
    private val coroutineScope: CoroutineScope,
    private val onEditClick:(Int)->Unit,
    private val onPreViewClick:(Int)->Unit,
    private val onNameChange:(Int,String)->Unit,
    private val pickColor:(Int,Int)->Unit,
    private val pickIcon:(Int,Int)->Unit,
    private val pickImage:(Int,Int)->Unit,
):
    RecyclerView.Adapter<GameSettingRecycleViewAdapter.ViewHolder>() {
    inner class ViewHolder(binding:FragmentGameSettingsItemBinding):RecyclerView.ViewHolder(binding.root){
        val cardId=binding.textViewCardName
        val root=binding.root
        val edit=binding.imageButtonEdit
        val preview=binding.imageButtonPreview
        val previewCardContainer=binding.cardContainerConstraint
        val editCardContainer=binding.editCardContainerLinear
        val previewCardCard=binding.cardPreviewCard
        val previewCardIcon=binding.cardPreviewIcon
        val previewCardTextView=binding.cardPreviewText
        val editCardName=binding.editCardName
        val editCardIcon=binding.editCardIcon
        val editCardColor=binding.editColor
        val colorPicker=binding.editColorPicker
        val iconPicker=binding.editIconPicker
        val selectImage=binding.uploadImageButton
        val imageView=binding.cardImage
        fun setOnClickListeners(position: Int){
            edit.setOnClickListener{
                setMode(true)
                cards[position].isEdit=true
                onEditClick(position)
            }
            preview.setOnClickListener{
                setMode(false)
                cards[position].isEdit=false
                onPreViewClick(position)
            }
        }
        fun setUpColorPicker(position: Int,colorId: Int){
            colorPicker.setOnClickListener{
                pickColor(position,colorId)
            }
        }
        fun setUpIconPicker(position: Int,iconId:Int){
            iconPicker.setOnClickListener{
                pickIcon(position,iconId)
            }
        }
        fun setUpImageSelector(position: Int,currentImg:Int){
            selectImage.setOnClickListener{
                pickImage(position,currentImg)
            }
        }
        fun setMode(isEdit:Boolean){
            if(isEdit){
                editCardContainer.visibility=View.VISIBLE
                previewCardContainer.visibility=View.GONE
                edit.visibility=View.GONE
                preview.visibility=View.VISIBLE
            }else{
                editCardContainer.visibility=View.GONE
                previewCardContainer.visibility=View.VISIBLE
                edit.visibility=View.VISIBLE
                preview.visibility=View.GONE
            }
        }
        fun setCardTint(color:Int){
            setCardTint(root,previewCardCard,previewCardIcon,previewCardTextView,editCardColor,editCardIcon,color)
        }
        fun setCardName(name:String){
            setCardName(previewCardTextView,editCardName,name)
        }
        fun loadDefaultImage(id:Int){
            Glide.with(context)
                .load(id)  // Resource ID for WebP image
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache on disk for future use
                .skipMemoryCache(false)  // Allow Glide to use memory cache for faster loading
                .centerCrop()
                .into(imageView)
        }
        fun setImageSelectorText(txt:String){
            selectImage.text=txt
        }
        fun loadUploadedImage(id:Int){
            coroutineScope.launch {
                ImageUtils.loadCardImageFromInternalStorage(
                    application,
                    "${ImageNames.CARD.txt}$id",
                    imageView
                )
            }
        }
        fun setUpKeyListener(position: Int){
            editCardName.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Hide the keyboard
                    val imm = editCardName.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(editCardName.windowToken, 0)
                    // Update the name (your logic for updating the name goes here)
                    val newName = editCardName.text.toString()

                    updateName(newName,position)  // Call your update function with the new name

                    true  // Return true to indicate the action was handled
                } else {
                    false
                }
            }
        }
        private fun updateName(name:String,position: Int){
            previewCardTextView.text=name
            onNameChange(position,name)
        }
        fun setIcon(id:Int){
            editCardIcon.setImageDrawable(DefaultCardOptions.getDrawable(context,id))
            previewCardIcon.setImageDrawable(DefaultCardOptions.getDrawable(context,id))
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding=FragmentGameSettingsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card=cards[position]
        holder.cardId.text=getCardName(position)
        holder.setMode(card.isEdit)
        holder.setOnClickListeners(position)
        holder.setCardTint(card.color)
        holder.setCardName(card.name)
        holder.setUpKeyListener(position)
        holder.setIcon(card.icon)
        holder.setUpIconPicker(position,card.icon)
        holder.setUpColorPicker(position,card.color)
        holder.setUpImageSelector(position, card.imageRes)
        if(card.imageRes<=0) {
            holder.setImageSelectorText("UPLOAD IMAGE")
            holder.loadDefaultImage(getDefaultImageId(card.imageRes))
        } else {
            holder.setImageSelectorText("Image: ${card.imageRes}")
            holder.loadUploadedImage(card.imageRes)
        }
    }
    private fun getCardName(index:Int):String{
        return when(index){
            0->"Card A"
            1->"Card B"
            2->"Card C"
            else->"Card D"
        }
    }
    private fun getDefaultImageId(index:Int):Int{
        return when(index){
            0-> R.drawable.i0
            -1->R.drawable.i1
            -2->R.drawable.i2
            else->R.drawable.i3
        }
    }
    private fun setCardTint(cardParent:MaterialCardView,cardPreview:MaterialCardView,icon:ImageView,text:TextView,editColor:ImageView,editIconColor:ImageView,colorId:Int){
        val selectedColor=DefaultCardOptions.getColor(colorId,false)
        cardParent.strokeColor=selectedColor
        cardPreview.strokeColor=selectedColor
        text.setBackgroundColor(DefaultCardOptions.getColor(colorId,true))
        icon.setColorFilter(selectedColor, android.graphics.PorterDuff.Mode.SRC_IN)
        editColor.setColorFilter(selectedColor,android.graphics.PorterDuff.Mode.SRC_IN)
        editIconColor.setColorFilter(selectedColor,android.graphics.PorterDuff.Mode.SRC_IN)
    }
    fun updateCardIcon(position:Int,iconId:Int){
        cards[position].icon=iconId
        notifyItemChanged(position)
    }
    fun changeCardColor(position:Int,colorId:Int){
        cards[position].color=colorId
        notifyItemChanged(position)
    }
    private fun setCardName(view:TextView,editText:EditText,text:String){
        view.text=text
        editText.setText(text)
    }
    fun updateCardImage(position: Int,imageId:Int){
        cards[position].imageRes=imageId
        notifyItemChanged(position)
    }
}