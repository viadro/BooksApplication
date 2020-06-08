package com.seweryn.booksapplication.di

import com.seweryn.booksapplication.data.Constants
import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.data.remote.BooksApi
import com.seweryn.booksapplication.tools.network.AccessDeniedInterceptor
import com.seweryn.booksapplication.tools.network.NetworkConnectionInterceptor
import com.seweryn.booksapplication.utils.network.ConnectionManager
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(connectionManager: ConnectionManager, userPreferences: UserPreferences): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(NetworkConnectionInterceptor(connectionManager))
        .addInterceptor(AccessDeniedInterceptor(userPreferences))
        .build()

    @Provides
    @Singleton
    fun provideBooksApiClient(okHttpClient: OkHttpClient): Retrofit {
        return buildRetrofitClient(
            okHttpClient,
            Constants.BASE_URL
        )
    }

    @Provides
    @Singleton
    fun provideBooksApiInterface(retrofit: Retrofit): BooksApi {
        return retrofit.create(BooksApi::class.java)
    }

    private fun buildRetrofitClient(okHttpClient: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder().client(okHttpClient).baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}