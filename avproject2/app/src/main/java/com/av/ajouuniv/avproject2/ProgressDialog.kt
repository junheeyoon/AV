package com.av.ajouuniv.avproject2

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.view.WindowManager

class ProgressDialog private constructor() {

    private var pdialog: ProgressDialog? = null

    fun closeProgressDialog() {

        if (pdialog != null) {
            pdialog!!.dismiss()
            pdialog = null
        }
    }

    fun showProgressDialog(resID: Int, ctx: Context) {
        val message = ctx.getString(resID)
        pdialog = ProgressDialog.show(ctx, null, message, true, true)
        pdialog!!.setCancelable(false)
    }

    companion object {
        private var dialogs: com.av.ajouuniv.avproject2.ProgressDialog? = null

        val instance: com.av.ajouuniv.avproject2.ProgressDialog
            @Synchronized get() {
                if (dialogs == null) {
                    dialogs = ProgressDialog()
                }
                return dialogs!!
            }

        fun showErrorDialog(activityContext: Context, msg: String, btnNameResId: Int) {
            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle(R.string.title_error).setMessage(msg).setPositiveButton(btnNameResId, null)
            val alert = builder.create()
            alert.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            if (!(activityContext as Activity).isFinishing) {
                alert.show()
            }

        }

        fun showAuthenticationErrorDialog(
                activityContext: Activity, msg: String, btnNameResId: Int) {
            val builder = AlertDialog.Builder(activityContext)
            builder.setTitle(R.string.title_error).setMessage(msg)
                    .setPositiveButton(btnNameResId) { _, _ -> activityContext.finish() }
            val alert = builder.create()
            alert.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            alert.show()
        }
    }

}
