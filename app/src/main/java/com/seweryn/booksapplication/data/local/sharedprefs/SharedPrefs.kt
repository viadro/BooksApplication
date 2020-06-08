package com.seweryn.booksapplication.data.local.sharedprefs

import android.content.Context
import android.content.SharedPreferences

abstract class SharedPrefs(context: Context) {

    protected val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(preferenceKey(), Context.MODE_PRIVATE)

    protected abstract fun preferenceKey(): String

    protected fun putString(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    protected fun getString(key: String): String {
        return sharedPreferences.getString(key, "")
    }
}