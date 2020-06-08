package com.seweryn.booksapplication.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.seweryn.booksapplication.data.AuthenticationManager
import com.seweryn.booksapplication.data.BooksRepository
import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.viewmodel.*
import dagger.MapKey
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import javax.inject.Provider
import kotlin.reflect.KClass

@Module
class ViewModelModule  {

    @MapKey
    @Target(AnnotationTarget.FUNCTION)
    annotation class ViewModelKey(
        val value: KClass<out ViewModel>
    )

    @Provides
    @IntoMap
    @ViewModelKey(BooksListViewModel::class)
    fun provideBooksListViewModel(booksRepository: BooksRepository, schedulerProvider: SchedulerProvider): ViewModel {
        return BooksListViewModel(booksRepository, schedulerProvider)
    }

    @Provides
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun provideMainViewModel(authenticationManager: AuthenticationManager, schedulerProvider: SchedulerProvider): ViewModel {
        return MainViewModel(authenticationManager, schedulerProvider)
    }

    @Provides
    @IntoMap
    @ViewModelKey(AddBookViewModel::class)
    fun provideAddBookViewModel(booksRepository: BooksRepository, schedulerProvider: SchedulerProvider): ViewModel {
        return AddBookViewModel(booksRepository, schedulerProvider)
    }

    @Provides
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun provideLoginViewModel(authenticationManager: AuthenticationManager, schedulerProvider: SchedulerProvider): ViewModel {
        return LoginViewModel(authenticationManager, schedulerProvider)
    }

    @Provides
    @IntoMap
    @ViewModelKey(BookDetailsViewModel::class)
    fun provideBookDetailsViewModel(booksRepository: BooksRepository, schedulerProvider: SchedulerProvider): ViewModel {
        return BookDetailsViewModel(booksRepository, schedulerProvider)
    }

    @Provides
    fun provideViewModelFactory(
        providers: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return requireNotNull(providers[modelClass as Class<out ViewModel>]).get() as T
        }
    }

}