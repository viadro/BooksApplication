package com.seweryn.booksapplication.viewmodel.liveDataModels

sealed class Message {
    object Success : Message()
    class Fail(val error: Error) : Message()
}