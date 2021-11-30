package com.hosco.nextcrm.callcenter.common

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.Snackbar
import com.hosco.nextcrm.callcenter.R
import kotlinx.android.synthetic.main.custom_progress_loading.view.*

object DialogUtils {
    var dialog: AlertDialog? = null
    var dialogCallNote: AlertDialog? = null
    var dialogCrm: AlertDialog? = null

    fun showLoadingDialog(context: Context?) {
        var dialogBuilder = AlertDialog.Builder(context, R.style.CustomProgress)
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.custom_progress_loading, null)
        val gradientDrawable = GradientDrawable()
        dialogView?.progress_bar?.progressDrawable = gradientDrawable
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        dialog = dialogBuilder.create()
        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun showCrmLoadingDialog(context: Context?) {
        var dialogBuilder = AlertDialog.Builder(context)
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.crm_progress_loading, null)
        val gradientDrawable = GradientDrawable()
        dialogView?.progress_bar?.progressDrawable = gradientDrawable
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        dialogCrm = dialogBuilder.create()
        dialogCrm?.show()
    }

    fun dismissCrm() {
        dialogCrm?.dismiss()
    }

    fun dismissCallNote() {
        dialogCallNote?.dismiss()
    }

    fun showSnackBar(view: View, message: String, snackBarListener: SnackBarListener?) {
    }

    fun showSnackBar(
        view: View,
        message: String
    ) { // Create the Snackbar
        val snackbar = Snackbar.make(view, message, LENGTH_LONG)
        val snackView: View = LayoutInflater.from(view.context)
            .inflate(com.hosco.nextcrm.callcenter.R.layout.crm_snackbar, null)
        // for rounded edges
        snackbar.view.setBackgroundColor(view.context.getColor(com.hosco.nextcrm.callcenter.R.color.bg_snackbar))
        val snackBarView = snackbar.view as Snackbar.SnackbarLayout
        snackBarView.addView(snackView, 0)
        snackbar.show()
    }

    fun showSnackBarWithListener(
        view: View,
        message: String, snackBarListener: SnackBarListener?
    ) { // Create the Snackbar
        val snackbar = Snackbar.make(view, message, LENGTH_INDEFINITE)
        val snackView: View = LayoutInflater.from(view.context)
            .inflate(R.layout.crm_snackbar, null)
        // for rounded edges
        snackbar.view.setBackgroundColor(view.context.getColor(com.hosco.nextcrm.callcenter.R.color.bg_snackbar))
        snackbar.setAction(
            view.context.resources.getString(R.string.txt_ok),
            object : View.OnClickListener {
                override fun onClick(v: View?) {
                    snackBarListener.let { it?.onOk() }
                }
            })
        val snackBarView = snackbar.view as Snackbar.SnackbarLayout
        snackBarView.addView(snackView, 0)
        snackbar.show()
    }

    interface SnackBarListener {
        fun onOk()
    }

    fun showCallNote(context: Context?, callNoteListener: callNoteListener) {
        var dialogBuilder = AlertDialog.Builder(context)
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_call_note, null)
        val edCallNote: EditText = dialogView.findViewById(R.id.edCallNote)
        val btnOkCallNote: TextView = dialogView.findViewById(R.id.btnOKCallNote)
        val btnCancelCallNote: TextView = dialogView.findViewById(R.id.btnCancelCallNote)
        val tvErrorCallNote: TextView = dialogView.findViewById(R.id.tvErrorCallNote)
        tvErrorCallNote.visibility = View.INVISIBLE

        edCallNote.doAfterTextChanged {
            if (!edCallNote.text.trim().toString().isEmpty())
                tvErrorCallNote.visibility = View.INVISIBLE
        }

        btnOkCallNote.setOnClickListener {
            if (edCallNote.text.toString().trim().isEmpty()) {
                tvErrorCallNote.visibility = View.VISIBLE
            } else {
                tvErrorCallNote.visibility = View.INVISIBLE
                callNoteListener.let {
                    it.onOK(edCallNote.text.trim().toString())
                    dismissCallNote()
                }
            }
        }

        btnCancelCallNote.setOnClickListener {
            callNoteListener.let { it.onCancel() }
            dismissCallNote()
        }
        dialogBuilder.setView(dialogView)
        dialogBuilder.setCancelable(false)
        dialogCallNote = dialogBuilder.create()
        dialogCallNote?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialogCallNote?.show()
    }


    interface callNoteListener {
        fun onOK(data: String)

        fun onCancel()
    }

}