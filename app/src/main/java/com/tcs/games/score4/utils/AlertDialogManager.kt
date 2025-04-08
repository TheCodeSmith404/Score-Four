package com.tcs.games.score4.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogShareCredentialsBinding
import com.tcs.games.score4.databinding.DialogShowPlayerDetailsBinding
import com.tcs.games.score4.databinding.DialogShowProgressBinding
import com.tcs.games.score4.model.gameroom.PlayersStatus
import com.tcs.games.score4.utils.convertors.TimeUtils

@SuppressLint("StaticFieldLeak")
object AlertDialogManager {
    private var dialogShowProgressBinding: DialogShowProgressBinding? = null
    private var alertDialog: AlertDialog? = null
    private var dialogShowUserDetails:DialogShowPlayerDetailsBinding?=null
    private var dialogShareCredentialsBinding:DialogShareCredentialsBinding?=null
    fun showLoadingDialog(
        context: Context,
        isIndeterminate:Boolean,
        message: String
    ) {
        // If a dialog is already showing, dismiss it before showing a new one
        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }

        // Inflate the custom view
        val customView = LayoutInflater.from(context).inflate(R.layout.dialog_show_progress, null)
        dialogShowProgressBinding = DialogShowProgressBinding.bind(customView)

        // Build and show the AlertDialog
        alertDialog = AlertDialog.Builder(context)
            .setView(customView)
            .setCancelable(false)
            .create()

        alertDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        if (isIndeterminate) {
            dialogShowProgressBinding?.determinateProgressContainer?.visibility = View.GONE
            dialogShowProgressBinding?.indeterminateProgressContainer?.visibility = View.VISIBLE
            dialogShowProgressBinding?.indeterminateMessage?.text = message
        } else {
            dialogShowProgressBinding?.determinateProgressContainer?.visibility = View.VISIBLE
            dialogShowProgressBinding?.indeterminateProgressContainer?.visibility = View.GONE
            dialogShowProgressBinding?.determinateMessage?.text = message
        }

        alertDialog?.show()
    }

    fun showShareCredentialDialog(
        context: Context,
        key:String,
        password:String,
    ) {
        // If a dialog is already showing, dismiss it before showing a new one
        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }

        // Inflate the custom view
        val customView = LayoutInflater.from(context).inflate(R.layout.dialog_share_credentials, null)
        dialogShareCredentialsBinding = DialogShareCredentialsBinding.bind(customView)

        // Build and show the AlertDialog
        alertDialog = AlertDialog.Builder(context)
            .setView(customView)
            .create()

        alertDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialogShareCredentialsBinding?.back?.setOnClickListener{
            hideDialog()
        }
        dialogShareCredentialsBinding?.id?.text=key
        dialogShareCredentialsBinding?.pass?.text=password
        dialogShareCredentialsBinding?.send?.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Id: $key\nPass: $password")
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            // Ensure you're calling startActivity with the correct parameters
            context.startActivity(shareIntent)
        }

        alertDialog?.show()
    }

    @SuppressLint("SetTextI18n")
    fun showPlayerDetailsDialog(
        context: Context,
        playersStatus: PlayersStatus
        ){

        if (alertDialog?.isShowing == true) {
            alertDialog?.dismiss()
        }

        // Inflate the custom view
        val customView = LayoutInflater.from(context).inflate(R.layout.dialog_show_player_details, null)
        dialogShowUserDetails=DialogShowPlayerDetailsBinding.bind(customView)
        alertDialog = AlertDialog.Builder(context)
            .setView(customView)
            .create()

        alertDialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        dialogShowUserDetails?.userName?.text=playersStatus.playerName
        dialogShowUserDetails?.shownId?.text=playersStatus.playerId
        dialogShowUserDetails?.gamesPlayed?.text=playersStatus.numberOfGamesPlayed.toString()
        dialogShowUserDetails?.gamesWon?.text=playersStatus.numberOfGamesWon.toString()
        dialogShowUserDetails?.dateJoined?.text=TimeUtils.getDateTimeString(playersStatus.dateJoined)
        if(playersStatus.playerProfile==""){
            if(playersStatus.bot){
                dialogShowUserDetails?.profile?.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.bot))
            }else{
                dialogShowUserDetails?.profile?.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.baseline_person_24))
            }
        }else{
            Glide.with(context)
                .load(playersStatus.playerProfile)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
                .into(dialogShowUserDetails?.profile!!)
        }

        alertDialog?.show()
    }

    fun updateLoadingDialogProgress(progress: Int, message: String) {
        dialogShowProgressBinding?.determinateProgress?.progress=progress
        dialogShowProgressBinding?.determinateMessage?.text=message
    }

    fun updateProgressDialogText(message: String) {
        // Check if the dialog is showing and the binding exists
        if (alertDialog?.isShowing == true && dialogShowProgressBinding != null) {
            // Update the text based on which progress container is visible
            if (dialogShowProgressBinding?.indeterminateProgressContainer?.visibility == View.VISIBLE) {
                dialogShowProgressBinding?.indeterminateMessage?.text = message
            } else if (dialogShowProgressBinding?.determinateProgressContainer?.visibility == View.VISIBLE) {
                dialogShowProgressBinding?.determinateMessage?.text = message
            }
        }
    }

    fun hideDialog() {
        alertDialog?.dismiss()
        alertDialog = null
        dialogShowProgressBinding = null
        dialogShowUserDetails=null
        dialogShareCredentialsBinding=null
    }
}
