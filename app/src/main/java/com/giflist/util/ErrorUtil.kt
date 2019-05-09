package com.giflist.util

import android.content.Context
import android.widget.Toast
import com.giflist.R
import java.net.UnknownHostException

object ErrorUtil {

    fun showErrorToast(context: Context,  throwable: Throwable) {
        val connectionError = if (throwable is UnknownHostException) {
            R.string.connection_error
        } else {
            R.string.unknown_error
        }
        Toast.makeText(context, connectionError, Toast.LENGTH_SHORT).show()
    }
}