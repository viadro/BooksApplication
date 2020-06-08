package com.seweryn.booksapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seweryn.booksapplication.data.model.Book

@Database(entities = [Book::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun booksDao(): BooksDao
}