package com.seweryn.booksapplication.data

import com.seweryn.booksapplication.data.model.Book
import io.reactivex.Completable
import io.reactivex.Flowable
import com.seweryn.booksapplication.data.model.Result
import io.reactivex.Single

interface BooksRepository  {
    fun getBooks(): Flowable<Result<List<Book>>>

    fun getBook(id: String): Single<Result<Book>>

    fun createBook(book: Book): Completable

    fun deleteBook(id: String): Completable
}