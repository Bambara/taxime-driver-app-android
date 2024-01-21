package com.ynr.taximedriver

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

object Helpers {


    enum class AlertDialogType() {
        WARNING,
        ERROR,
        COMFIRMATION
    }

    fun showAlertDialog(
            activity: Activity,
            title: String,
            type: AlertDialogType,
            callback: (confirm: Boolean) -> Unit
    ) {
        val builder = AlertDialog.Builder(activity!!, R.style.TransparentDialog)
        // Get the layout inflater
        val inflater = activity.layoutInflater;
        val dialogView = inflater.inflate(R.layout.confirmation_dialog_view, null)
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
        dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val yesButton =  dialogView.findViewById<Button>(R.id.btnYes)
        val noButton =  dialogView.findViewById<Button>(R.id.btnNo)
        val dialogIcon =  dialogView.findViewById<ImageView>(R.id.dialogIcon)

        when(type) {
            AlertDialogType.WARNING -> {
                dialogIcon?.setImageResource(R.drawable.ic_error)
                noButton.visibility = View.GONE
                yesButton.text = "Ok"
            }
            AlertDialogType.ERROR -> {
                dialogIcon?.setImageResource(R.drawable.ic_error)
                noButton.visibility = View.GONE
                yesButton.text = "Ok"
            }
            AlertDialogType.COMFIRMATION -> {

            }
        }


        dialogView.findViewById<TextView>(R.id.txtDialogTitle).text = title

        yesButton.setOnClickListener {
            callback(true)
            dialog.dismiss()
        }

        noButton.setOnClickListener {
            callback(false)
            dialog.dismiss()
        }

    }

    fun currencyFormat(amount: String): String? {
        val formatter = DecimalFormat("###,###,##0.00")
        return formatter.format(amount.toDouble())
    }


    fun convertIsoDate(dateString: String, targetFormat: String) : String{
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val date = formatter.parse(dateString)
        val targetFormat = SimpleDateFormat(targetFormat, Locale.getDefault())
        return targetFormat.format(date)
    }
}