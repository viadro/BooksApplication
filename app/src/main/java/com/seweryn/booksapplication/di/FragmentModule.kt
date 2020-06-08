package com.seweryn.booksapplication.di

import com.seweryn.booksapplication.ui.fragments.BooksListFragment
import com.seweryn.booksapplication.ui.fragments.LoginFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {
    @ContributesAndroidInjector
    abstract fun contributeBooksListFragment(): BooksListFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment
}