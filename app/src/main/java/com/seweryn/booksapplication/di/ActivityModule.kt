package com.seweryn.booksapplication.di

import com.seweryn.booksapplication.ui.activities.AddBookActivity
import com.seweryn.booksapplication.ui.activities.BookDetailsActivity
import com.seweryn.booksapplication.ui.activities.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule  {
    @ContributesAndroidInjector
    abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    abstract fun contributeAddBookActivity(): AddBookActivity

    @ContributesAndroidInjector
    abstract fun contributeBookDetailsActivity(): BookDetailsActivity
}