package com.seweryn.booksapplication.data.local.sharedprefs.user

import io.reactivex.Observable

interface UserPreferences  {
    fun getCredentials(): String
    fun observeCredentials(): Observable<String>
    fun rememberCredentials(credentials: String)
    fun clearCredentials()
}