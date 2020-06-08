package com.seweryn.booksapplication.data

import com.seweryn.booksapplication.data.local.BooksDao
import com.seweryn.booksapplication.data.local.Database
import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.data.model.Book
import com.seweryn.booksapplication.data.remote.BooksApi
import com.seweryn.booksapplication.utils.WithMockito
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import com.seweryn.booksapplication.data.model.Result

@RunWith(MockitoJUnitRunner.Silent::class)
class BooksRepositoryImplTest : WithMockito {
    private lateinit var systemUnderTest: BooksRepositoryImpl

    @Mock
    lateinit var api: BooksApi

    @Mock
    lateinit var database: Database

    @Mock
    lateinit var booksDao: BooksDao

    @Mock
    lateinit var userPreferences: UserPreferences

    @Before
    fun setUp() {
        systemUnderTest = BooksRepositoryImpl(api, database, userPreferences)
        mockDatabase()
        mockPreferences()
    }

    @Test
    fun `should fetch books from api`() {
        simulateBooksCanBeFetched()
        systemUnderTest.getBooks()

        Mockito.verify(api).getBooks(any())
    }

    @Test
    fun `should synchronize books in database after api call`() {
        simulateBooksCanBeFetched()
        systemUnderTest.getBooks().test()

        Mockito.verify(booksDao).synchronizeBooks(any())
    }

    @Test
    fun `should emmit synchronised books from database after successful api call`() {
        simulateBooksCanBeFetched()
        simulateBooksCanBeFetchedFromDatabase()
        val result = systemUnderTest.getBooks().test().values().first()

        assertThat(result, CoreMatchers.instanceOf(Result.Success::class.java))
        assertEquals("db", (result as Result.Success).value.first().title)
    }

    @Test
    fun `should fetch cached data when api call failed`() {
        simulateBooksCanNotBeFetched()
        systemUnderTest.getBooks().test()

        Mockito.verify(booksDao).queryBooks()
    }

    @Test
    fun `should emmit cached books from database after failed api call`() {
        simulateBooksCanNotBeFetched()
        simulateBooksCanBeFetchedFromDatabase()
        val result = systemUnderTest.getBooks().test().values().first()

        assertThat(result, CoreMatchers.instanceOf(Result.Error::class.java))
        assertEquals("db", (result as Result.Error).cachedValue?.first()?.title)
    }

    @Test
    fun `should fetch book from api when requested`() {
        simulateBookCanBeFetchedFromApi()
        systemUnderTest.getBook("id")

        Mockito.verify(api).getBook(any(), any())
    }

    @Test
    fun `should update or insert book in database when fetched from api`() {
        simulateBookCanBeFetchedFromApi()
        systemUnderTest.getBook("id").test()

        Mockito.verify(booksDao).insertOrUpdateBook(any())
    }

    @Test
    fun `should create book`() {
        simulateBookCanBeCreated()
        systemUnderTest.createBook(createDbBook())

        Mockito.verify(api).createBook(any(), any())
    }

    @Test
    fun `should delete book`() {
        simulateBookCanBeDeleted()
        systemUnderTest.deleteBook("id")

        Mockito.verify(api).deleteBook(any(), any())
    }

    @Test
    fun `should delete book from database after api delete`() {
        simulateBookCanBeDeleted()
        systemUnderTest.deleteBook("id").test()

        Mockito.verify(booksDao).deleteBookById(any())
    }

    private fun simulateBooksCanBeFetched() {
        Mockito.`when`(api.getBooks(any())).thenReturn(Single.just(listOf()))
    }

    private fun simulateBookCanBeCreated() {
        Mockito.`when`(api.createBook(any(), any())).thenReturn(Completable.complete())
    }

    private fun simulateBookCanBeDeleted() {
        Mockito.`when`(api.deleteBook(any(), any())).thenReturn(Completable.complete())
    }

    private fun simulateBooksCanNotBeFetched() {
        Mockito.`when`(api.getBooks(any())).thenReturn(Single.error(Exception()))
    }

    private fun simulateBooksCanBeFetchedFromDatabase() {
        Mockito.`when`(booksDao.queryBooks()).thenReturn(Flowable.just(listOf(createDbBook())))
    }

    private fun simulateBookCanBeFetchedFromApi() {
        Mockito.`when`(api.getBook(any(), any()))
            .thenReturn(Single.just(createDbBook()))
    }

    private fun mockPreferences() {
        Mockito.`when`(userPreferences.getCredentials()).thenReturn("credentials")
    }

    private fun mockDatabase() {
        Mockito.`when`(database.booksDao()).thenReturn(booksDao)
    }

    private fun createDbBook() = Book("id", "db", "db","db")
}