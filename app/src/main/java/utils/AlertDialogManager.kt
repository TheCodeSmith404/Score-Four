package utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import com.tcs.games.score4.R
import com.tcs.games.score4.databinding.DialogShowProgressBinding
import model.messages.Message

object AlertDialogManager {
    @SuppressLint("StaticFieldLeak")
    private var binding: DialogShowProgressBinding? = null
    private var alertDialog: AlertDialog? = null
    fun showDialog(
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
        binding = DialogShowProgressBinding.bind(customView)

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
            binding?.determinateProgressContainer?.visibility = View.GONE
            binding?.indeterminateProgressContainer?.visibility = View.VISIBLE
            binding?.indeterminateMessage?.text = message
        } else {
            binding?.determinateProgressContainer?.visibility = View.VISIBLE
            binding?.indeterminateProgressContainer?.visibility = View.GONE
            binding?.determinateMessage?.text = message
        }

        alertDialog?.show()
    }

    fun updateProgress(progress: Int, message: String) {
        binding?.determinateProgress?.progress=progress
        binding?.determinateMessage?.text=message
    }

    fun hideDialog() {
        alertDialog?.dismiss()
        alertDialog = null
        binding = null
    }
}
