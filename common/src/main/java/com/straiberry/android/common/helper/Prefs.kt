package com.straiberry.android.common.helper

import android.content.Context
import androidx.preference.PreferenceManager

object Prefs {
    fun putString(
        context: Context,
        key: String?,
        value: String?
    )=
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putString(key, value).apply()


    fun getString(context: Context, key: String?): String? =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getString(key, "")

    fun putBoolean(
        context: Context,
        key: String?,
        value: Boolean
    ) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean(key, value).apply()
    }


    fun getBoolean(context: Context, key: String?): Boolean? {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(key, false)
    }

    fun putInteger(
        context: Context,
        key: String?,
        value: Int
    ) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putInt(key, value).apply()
    }

    fun getInteger(context: Context, key: String?): Int {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getInt(key,0)
    }
}