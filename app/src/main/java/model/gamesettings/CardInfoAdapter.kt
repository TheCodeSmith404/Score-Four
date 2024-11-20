package model.gamesettings

data class CardInfoAdapter(
    val id:String,
    var name:String,
    var color:Int,
    var imageRes:Int,
    var icon:Int,
    var isEdit:Boolean,
)
