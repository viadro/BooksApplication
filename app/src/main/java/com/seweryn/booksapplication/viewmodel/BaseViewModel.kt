package com.seweryn.booksapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.seweryn.booksapplication.tools.network.error.AccessDeniedError
import com.seweryn.booksapplication.tools.network.error.ConnectionError
import com.seweryn.booksapplication.utils.SchedulerProvider
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error

abstract class BaseViewModel(private val schedulersProvider: SchedulerProvider) : ViewModel() {

    private val subscriptions = CompositeDisposable()

    protected fun clearSubscriptions() {
        subscriptions.clear()
    }

    override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }

    protected fun execute(
        command: Completable,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        subscriptions.add(
            command.subscribeOn(schedulersProvider.ioScheduler())
                .observeOn(schedulersProvider.uiScheduler())
                .subscribe(
                    { onSuccess.invoke() },
                    { error -> onError.invoke(error) })
        )
    }

    protected fun <T> execute(
        command: Single<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        subscriptions.add(
            command.subscribeOn(schedulersProvider.ioScheduler())
                .observeOn(schedulersProvider.uiScheduler())
                .subscribe(
                    { result -> onSuccess.invoke(result) },
                    { error -> onError.invoke(error) })
        )
    }

    protected fun <T> load(
        command: Single<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        subscriptions.add(
            command.subscribeOn(schedulersProvider.ioScheduler())
                .observeOn(schedulersProvider.uiScheduler())
                .subscribe(
                    { result -> onSuccess.invoke(result) },
                    { error -> onError.invoke(error) }
        ))
    }

    protected fun <T> load(
        command: Observable<T>,
        onNext: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit = {}
    ) {
        subscriptions.add(
            command.subscribeOn(schedulersProvider.ioScheduler())
                .observeOn(schedulersProvider.uiScheduler())
                .subscribe(
                    { result -> onNext.invoke(result) },
                    { error -> onError.invoke(error) },
                    { onComplete.invoke() })
        )
    }

    protected fun <T> load(
        command: Flowable<T>,
        onNext: (T) -> Unit,
        onError: (Throwable) -> Unit,
        onComplete: () -> Unit = {}
    ) {
        subscriptions.add(
            command.subscribeOn(schedulersProvider.ioScheduler())
                .observeOn(schedulersProvider.uiScheduler())
                .subscribe(
                    { result -> onNext.invoke(result) },
                    { error -> onError.invoke(error) },
                    { onComplete.invoke() })
        )
    }



    protected fun <T> sendSingleEvent(liveData: MutableLiveData<T?>, value: T) {
        liveData.value = value
        liveData.value = null
    }

    protected fun handleError(throwable: Throwable, retryAction: (() -> Unit)?): Error{
        return when(throwable) {
            is ConnectionError -> Error.ConnectionError(retryAction)
            is AccessDeniedError -> Error.AccessDeniedError(retryAction)
            else -> Error.GenericError(retryAction)
        }
    }
}