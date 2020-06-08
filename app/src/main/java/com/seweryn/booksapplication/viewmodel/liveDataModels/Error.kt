package com.seweryn.booksapplication.viewmodel.liveDataModels

sealed class Error(val retryAction: (() -> Unit)?) {
    class GenericError(retryAction: (() -> Unit)? = null): Error(retryAction)
    class ConnectionError(retryAction: (() -> Unit)? = null): Error(retryAction)
    class AccessDeniedError(retryAction: (() -> Unit)? = null): Error(retryAction)
}