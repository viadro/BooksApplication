package com.seweryn.booksapplication.tools.network

import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.tools.network.error.AccessDeniedError
import okhttp3.Interceptor
import okhttp3.Response

class AccessDeniedInterceptor(private val userPreferences: UserPreferences) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if(response.code() == 401) {
            userPreferences.clearCredentials()
            throw AccessDeniedError()
        }

        return response
    }

}