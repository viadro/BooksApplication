package com.seweryn.booksapplication.data

import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.data.remote.BooksApi
import io.reactivex.Completable
import io.reactivex.Observable
import okhttp3.Credentials

class AuthenticationManagerImpl(private val userPreferences: UserPreferences,
                                private val booksApi: BooksApi) : AuthenticationManager {

    override fun isUserLoggedIn(): Observable<Boolean> {
        return userPreferences.observeCredentials()
            .map { it.isNotEmpty() }
    }

    override fun logInUser(username: String, password: String): Completable {
        val credentials = Credentials.basic(username, password)
        return booksApi.getBooks(credentials).
            doOnSuccess {
            userPreferences.rememberCredentials(credentials)
        }.ignoreElement()
    }

}