package com.seweryn.booksapplication.data.model

sealed class Result<T> {
    class Success<T>(val value: T): Result<T>()
    class Error<T>(val cachedValue: T?, val throwable: Throwable): Result<T>()
}

fun <T> T.asResultSuccess(): Result<T> {
    return Result.Success(this)
}

fun <T> T.asResultError(throwable: Throwable): Result<T> {
    return Result.Error(this, throwable)
}