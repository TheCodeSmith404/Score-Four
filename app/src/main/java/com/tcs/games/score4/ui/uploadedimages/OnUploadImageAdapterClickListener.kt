package com.tcs.games.score4.ui.uploadedimages

interface OnUploadImageAdapterClickListener {
    fun onItemClick(position: Int,imageId: Int)
    fun onDeleteClick(position: Int,imageId:Int)
    fun onFooterClick()
}