package com.utaite.slidegallery.util

import android.content.Context
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.utaite.slidegallery.R


fun setView(activity: AppCompatActivity, resId: Int): View =
        LayoutInflater.from(activity).inflate(resId, null).also { view ->
            activity.setContentView(view)
        }

fun getDialog(context: Context, message: Int, allow: () -> Unit, denied: () -> Unit) {
    AlertDialog.Builder(context)
            .setPositiveButton(R.string.alert_allow) { _, _ -> allow() }
            .setNegativeButton(R.string.alert_denied) { _, _ -> denied() }
            .setCancelable(false)
            .setMessage(message)
            .show()
}

fun getToast(context: Context, message: Int) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun getColorInt(context: Context, resId: Int): Int =
        context.resources.getColor(resId)
