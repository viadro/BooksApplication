package com.seweryn.booksapplication.data.remote

import com.seweryn.booksapplication.data.model.Book
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

interface BooksApi {

    @GET("api/Books")
    fun getBooks(@Header("Authorization") credentials: String): Single<List<Book>>

    @GET("api/Book/{id}")
    fun getBook(@Header("Authorization") credentials: String, @Path("id") id: String): Single<Book>

    @POST("api/Book")
    fun createBook(@Header("Authorization") credentials: String, @Body book: Book): Completable

    @DELETE("api/Book/{id}")
    fun deleteBook(@Header("Authorization") credentials: String, @Path("id") id: String): Completable
}