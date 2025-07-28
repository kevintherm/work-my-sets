package com.example.workmysets.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast

class ErrorToast(
    private val activity: Activity,
    private val reason: String = "Invalid data",
    private val length: Int = Toast.LENGTH_SHORT
) {

    /**
     * Show a toast for an error message and close the activity.
     */
    fun show() {
        val context = activity.baseContext
        Toast.makeText(context, reason, length).show()
        activity.finish()
    }

    fun log() {
        val context = activity.baseContext
        Toast.makeText(context, reason, length).show()
        Log.d("CONSOLE LOG", reason)
    }
}