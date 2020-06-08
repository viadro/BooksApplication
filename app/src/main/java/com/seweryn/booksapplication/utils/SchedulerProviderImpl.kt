package com.seweryn.booksapplication.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerProviderImpl : SchedulerProvider {
    override fun ioScheduler(): Scheduler = Schedulers.io()

    override fun uiScheduler(): Scheduler = AndroidSchedulers.mainThread()

}