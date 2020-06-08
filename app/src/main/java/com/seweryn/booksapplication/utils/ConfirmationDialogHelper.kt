package com.seweryn.booksapplication.utils

import android.app.AlertDialog
import android.content.Context
import com.seweryn.booksapplication.R

class ConfirmationDialogHelper  {
    companion object {
        fun showConfirmationDialog(context: Context, message: String, confirmAction: () -> Unit) {
            AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.confirmation_dialog_positive_button) { _, _ -> confirmAction.invoke() }
                .setNegativeButton(R.string.confirmation_dialog_negative_button, null)
                .show()
        }
    }
}