package com.seweryn.booksapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.seweryn.booksapplication.data.BooksRepository
import com.seweryn.booksapplication.data.model.Book
import com.seweryn.booksapplication.data.model.asResultSuccess
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.utils.WithMockito
import com.seweryn.booksapplication.viewmodel.liveDataModels.Message
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class BookDetailsViewModelTest : WithMockito {

    private lateinit var systemUnderTest: BookDetailsViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @Mock
    lateinit var bookRepository: BooksRepository
    @Mock
    lateinit var schedulerProvider: SchedulerProvider

    @Mock
    lateinit var actionListener: (BookDetailsViewModel.Action?) -> Unit

    @Before
    fun setUp() {
        systemUnderTest = BookDetailsViewModel(bookRepository, schedulerProvider)
        mockSchedulers()
    }

    @Test
    fun `should load book on initialization`() {
        simulateBookCanBeLoaded()
        systemUnderTest.init("id")

        Mockito.verify(bookRepository).getBook("id")
    }

    @Test
    fun `should set book when it was loaded`() {
        simulateBookCanBeLoaded()
        systemUnderTest.init("id")


        assertNotNull(systemUnderTest.book)
        assertEquals("id", systemUnderTest.book.value?.id)
    }

    @Test
    fun `should delete book when requested`() {
        simulateBookCanBeLoaded()
        simulateBookCanBeDeleted()
        systemUnderTest.init("id")
        systemUnderTest.deleteBook()

        Mockito.verify(bookRepository).deleteBook(any())
    }

    @Test
    fun `should close on successful deletion`() {
        simulateBookCanBeLoaded()
        simulateBookCanBeDeleted()
        systemUnderTest.init("id")
        systemUnderTest.action.observeForever(actionListener)
        systemUnderTest.deleteBook()

        Mockito.verify(actionListener)
            .invoke(Mockito.any(BookDetailsViewModel.Action.Close::class.java))
    }

    @Test
    fun `should show message on successful deletion`() {
        simulateBookCanBeLoaded()
        simulateBookCanBeDeleted()
        systemUnderTest.init("id")
        systemUnderTest.deleteBook()

        assertNotNull(systemUnderTest.deleteMessage)
        assertThat(systemUnderTest.deleteMessage.value, CoreMatchers.instanceOf(Message.Success::class.java))
    }

    @Test
    fun `should show message on failed deletion`() {
        simulateBookCanBeLoaded()
        simulateBookCanNotBeDeleted()
        systemUnderTest.init("id")
        systemUnderTest.deleteBook()

        assertNotNull(systemUnderTest.deleteMessage)
        assertThat(systemUnderTest.deleteMessage.value, CoreMatchers.instanceOf(Message.Fail::class.java))
    }

    private fun simulateBookCanBeLoaded() {
        Mockito.`when`(bookRepository.getBook(any()))
            .thenReturn(Single.just(Book("id","a","a","a").asResultSuccess()))
    }

    private fun simulateBookCanBeDeleted() {
        Mockito.`when`(bookRepository.deleteBook(any()))
            .thenReturn(Completable.complete())
    }

    private fun simulateBookCanNotBeDeleted() {
        Mockito.`when`(bookRepository.deleteBook(any()))
            .thenReturn(Completable.error(Exception()))
    }

    private fun mockSchedulers() {
        Mockito.`when`(schedulerProvider.ioScheduler()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulerProvider.uiScheduler()).thenReturn(Schedulers.trampoline())
    }
}