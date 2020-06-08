package com.seweryn.booksapplication.data.local

import androidx.room.*
import com.seweryn.booksapplication.data.model.Book
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface BooksDao {
    @Query("SELECT * FROM book")
    fun queryBooks(): Flowable<List<Book>>

    @Query("SELECT * FROM book WHERE id = :bookId")
    fun queryBookById(bookId: String): Single<Book>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooks(books: List<Book>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBook(book: Book)

    @Update
    fun updateBook(book: Book)

    @Query("DELETE FROM book WHERE id = :bookId")
    fun deleteBookById(bookId: String)

    @Query("SELECT * FROM book where description IS NOT NULL OR coverUrl IS NOT NULL;")
    fun queryBooksWithDetails(): List<Book>

    @Query("DELETE FROM book")
    fun clearBooks()

    @Transaction
    fun synchronizeBooks(books: List<Book>) {
        val booksWithDetails = queryBooksWithDetails()
        clearBooks()
        insertBooks(books.map { book ->
            book.copy(description = booksWithDetails.find { it.id == book.id }?.description,
                coverUrl = booksWithDetails.find { it.id == book.id }?.coverUrl)
        })
    }

    @Transaction
    fun insertOrUpdateBook(book: Book) {
        queryBookById(book.id).subscribe(
            { updateBook(book) },
            { insertBook(book) }
        )
    }
}