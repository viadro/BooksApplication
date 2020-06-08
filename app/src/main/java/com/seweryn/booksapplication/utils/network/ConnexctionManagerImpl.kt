package com.seweryn.booksapplication.utils.network

import android.content.Context
import android.net.ConnectivityManager

class ConnectionManagerImpl(context: Context) : ConnectionManager {

    private val systemConnectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun isConnected(): Boolean = systemConnectivityManager.activeNetworkInfo?.isConnected == true

}