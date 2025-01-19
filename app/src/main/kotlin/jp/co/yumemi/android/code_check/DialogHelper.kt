package jp.co.yumemi.android.code_check

import android.app.AlertDialog
import android.content.Context

object DialogHelper {
    fun showErrorDialog(
        context: Context,
        message: String,
    ) {
        AlertDialog.Builder(context)
            .setTitle("エラー")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
