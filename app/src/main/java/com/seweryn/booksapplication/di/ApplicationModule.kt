package com.seweryn.booksapplication.di

import android.app.Application
import androidx.room.Room
import com.seweryn.booksapplication.data.*
import com.seweryn.booksapplication.data.local.Database
import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferencesImpl
import com.seweryn.booksapplication.data.remote.BooksApi
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.utils.SchedulerProviderImpl
import com.seweryn.booksapplication.utils.network.ConnectionManager
import com.seweryn.booksapplication.utils.network.ConnectionManagerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideConnectionManager(): ConnectionManager = ConnectionManagerImpl(application)

    @Provides
    @Singleton
    fun provideSchedulersProvider(): SchedulerProvider = SchedulerProviderImpl()

    @Provides
    @Singleton
    fun provideUserPreferences(): UserPreferences = UserPreferencesImpl(application)

    @Provides
    @Singleton
    fun provideAuthenticationManager(userPreferences: UserPreferences, booksApi: BooksApi): AuthenticationManager = AuthenticationManagerImpl(userPreferences, booksApi)

    @Provides
    @Singleton
    fun provideBooksRepository(booksApi: BooksApi, database: Database, userPreferences: UserPreferences): BooksRepository =
        BooksRepositoryImpl(booksApi, database, userPreferences)

    @Provides
    @Singleton
    fun provideBooksDatabase(): Database = Room.databaseBuilder(
        application,
        Database::class.java, Constants.BOOKS_DATABASE_NAME
    )
        .fallbackToDestructiveMigration()
        .build()
}