package com.seweryn.booksapplication.viewmodel.liveDataModels

sealed class InputError {
    object Empty : InputError()
    object Invalid : InputError()
}