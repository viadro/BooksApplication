package com.seweryn.booksapplication.tools.network

import com.seweryn.booksapplication.tools.network.error.ConnectionError
import com.seweryn.booksapplication.utils.network.ConnectionManager
import okhttp3.Interceptor
import okhttp3.Response

class NetworkConnectionInterceptor(private val connectionManager: ConnectionManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if(!connectionManager.isConnected()) throw ConnectionError()
        return chain.proceed(chain.request().newBuilder().build())
    }

}