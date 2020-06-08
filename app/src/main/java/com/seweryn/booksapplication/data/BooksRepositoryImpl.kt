package com.seweryn.booksapplication.data

import com.seweryn.booksapplication.data.local.Database
import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.data.model.Book
import com.seweryn.booksapplication.data.model.asResultSuccess
import com.seweryn.booksapplication.data.remote.BooksApi
import io.reactivex.Completable
import io.reactivex.Flowable
import com.seweryn.booksapplication.data.model.Result
import com.seweryn.booksapplication.data.model.asResultError
import io.reactivex.Single

class BooksRepositoryImpl(private val booksApi: BooksApi,
                          private val database: Database,
                          private val userPreferences: UserPreferences) : BooksRepository {

    override fun getBooks(): Flowable<Result<List<Book>>> {
        return booksApi.getBooks(userPreferences.getCredentials())
            .flatMapPublisher { books ->
                database.booksDao().synchronizeBooks(books)
                database.booksDao().queryBooks().map{
                    it.asResultSuccess()
                }
            }.onErrorResumeNext { throwable: Throwable ->
                database.booksDao().queryBooks().map {
                    it.asResultError(throwable)
                }
            }
    }

    override fun getBook(id: String): Single<Result<Book>> {
        return booksApi.getBook(userPreferences.getCredentials(), id)
            .doOnSuccess { book ->
                database.booksDao().insertOrUpdateBook(book) }
            .map { it.asResultSuccess() }
            .onErrorResumeNext { throwable: Throwable ->
                database.booksDao().queryBookById(id).map {
                    it.asResultError(throwable)
                }
            }
    }

    override fun createBook(book: Book): Completable {
        return booksApi.createBook(userPreferences.getCredentials(), book)
    }

    override fun deleteBook(id: String): Completable {
        return booksApi.deleteBook(userPreferences.getCredentials(), id)
            .doOnComplete { database.booksDao().deleteBookById(id) }
    }

}