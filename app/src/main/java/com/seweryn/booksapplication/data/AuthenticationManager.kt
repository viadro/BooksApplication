package com.seweryn.booksapplication.data

import io.reactivex.Completable
import io.reactivex.Observable

interface AuthenticationManager  {
    fun isUserLoggedIn(): Observable<Boolean>

    fun logInUser(username: String, password: String): Completable
}