package com.seweryn.booksapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import com.seweryn.booksapplication.data.BooksRepository
import com.seweryn.booksapplication.data.model.Book
import com.seweryn.booksapplication.data.model.Result
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.viewmodel.liveDataModels.Message

class BookDetailsViewModel(
    private val booksRepository: BooksRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    private var bookId: String? = null

    val book = MutableLiveData<Book>()
    val progress = MutableLiveData<Boolean>()
    val deleteMessage = MutableLiveData<Message>()
    var action: MutableLiveData<Action?> = MutableLiveData()

    fun init(bookId: String) {
        progress.value = true
        this.bookId = bookId
        load(
            command = booksRepository.getBook(bookId),
            onSuccess = { result ->
                progress.value = false
                when (result) {
                    is Result.Success -> book.value = result.value
                    is Result.Error -> book.value = result.cachedValue
                }
            },
            onError = {
                progress.value = false
            }
        )
    }

    fun deleteBook() {
        progress.value = true
        bookId?.let {
            execute(
                command = booksRepository.deleteBook(it),
                onSuccess = {
                    deleteMessage.value = Message.Success
                    progress.value = false
                    sendSingleEvent(action, Action.Close)
                },
                onError = { throwable ->
                    progress.value = false
                    deleteMessage.value = Message.Fail(handleError(throwable, null))
                }
            )
        }
    }

    sealed class Action {
        object Close : Action()
    }

}