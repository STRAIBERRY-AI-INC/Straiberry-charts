package com.straiberry.android.common.helper

import android.content.Context

const val USER_ACCESS = "USER_ACCESS"
class AuthorizationHelper(private val context: Context) {
    fun setHeaders(): MutableMap<String, String?> {
        val header: MutableMap<String, String?> = HashMap()
        header["Authorization"] = "Bearer "+ Prefs.getString(
            context,
            USER_ACCESS
        )
        header["Connection"] = "close"
        return header
    }
}