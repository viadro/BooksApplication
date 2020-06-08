package com.seweryn.booksapplication.utils

import android.util.Patterns

fun String.isValidUrl(): Boolean {
    return return Patterns.WEB_URL.matcher(this).matches()
}