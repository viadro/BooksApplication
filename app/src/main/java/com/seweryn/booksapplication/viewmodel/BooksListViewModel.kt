package com.seweryn.booksapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import com.seweryn.booksapplication.data.BooksRepository
import com.seweryn.booksapplication.data.model.Book
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.data.model.Result
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error
import javax.inject.Inject

class BooksListViewModel @Inject constructor(
    private val booksRepository: BooksRepository,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val books = MutableLiveData<List<BookData>>()
    val progress = MutableLiveData<Boolean>()
    var action: MutableLiveData<Action?> = MutableLiveData()
    val error = MutableLiveData<Error>()

    init {
        getBooks()
    }

    fun booksChanged() {
        getBooks()
    }

    private fun getBooks() {
        clearSubscriptions()
        progress.value = true
        error.value = null
        load(
            command = booksRepository.getBooks(),
            onNext = { result ->
                progress.value = false
                when(result) {
                    is Result.Success -> { books.value = result.value.map { book -> mapBook(book) }}
                    is Result.Error -> {
                        books.value = result.cachedValue?.map { book -> mapBook(book) }
                        handleError(result.throwable)
                    }
                }
            },
            onError = {
                progress.value = false
                handleError(it)
            }
        )
    }

    private fun handleError(throwable: Throwable) {
        error.value = handleError(throwable) { getBooks() }
    }

    private fun mapBook(book: Book) = BookData(
        book = book,
        selectAction = {
            sendSingleEvent(action, Action.ShowBookDetails(book.id))
        }
    )

    data class BookData(
        val book: Book,
        val selectAction: () -> Unit
    )

    sealed class Action{
        class ShowBookDetails(val id: String): Action()
    }
}