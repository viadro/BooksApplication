package com.seweryn.booksapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.seweryn.booksapplication.data.BooksRepository
import com.seweryn.booksapplication.data.model.Book
import com.seweryn.booksapplication.data.model.asResultError
import com.seweryn.booksapplication.data.model.asResultSuccess
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.utils.WithMockito
import io.reactivex.Flowable
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
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error

@RunWith(MockitoJUnitRunner.Silent::class)
class BooksListViewModelTest : WithMockito {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @Mock
    lateinit var bookdRepository: BooksRepository
    @Mock
    lateinit var schedulerProvider: SchedulerProvider

    @Mock
    lateinit var progressListener: (Boolean) -> Unit

    @Mock
    lateinit var actionListener: (BooksListViewModel.Action?) -> Unit

    @Before
    fun setUp() {
        mockSchedulers()
    }

    @Test
    fun `should get books on start`() {
        simulateBooksCanBeLoaded()
        BooksListViewModel(bookdRepository, schedulerProvider)
        Mockito.verify(bookdRepository).getBooks()
    }

    @Test
    fun `should display books when loaded`() {
        simulateBooksCanBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        assertNotNull(systemUnderTest.books)
        assertEquals(1, systemUnderTest.books.value?.size)
    }

    @Test
    fun `should display error when loading failed`() {
        simulateBooksCanNotBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        assertNotNull(systemUnderTest.error)
        assertThat(systemUnderTest.error.value, CoreMatchers.instanceOf(Error::class.java))
    }

    @Test
    fun `should display error when got cached data`() {
        simulateOnlyCachedBooksCanBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        assertNotNull(systemUnderTest.error)
        assertThat(systemUnderTest.error.value, CoreMatchers.instanceOf(Error::class.java))
    }

    @Test
    fun `should display bookswhen got cached data`() {
        simulateOnlyCachedBooksCanBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        assertNotNull(systemUnderTest.books)
        assertEquals(1, systemUnderTest.books.value?.size)
    }

    @Test
    fun `should reload books when selected retry option from error message`() {
        simulateBooksCanNotBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        systemUnderTest.error.value?.retryAction?.invoke()
        Mockito.verify(bookdRepository, Mockito.times(2)).getBooks()
    }

    @Test
    fun `should show book details when it was selected`() {
        simulateBooksCanBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        systemUnderTest.action.observeForever {
            actionListener.invoke(it)
        }
        systemUnderTest.books.value?.first()?.selectAction?.invoke()
        Mockito.verify(actionListener)
            .invoke(Mockito.any(BooksListViewModel.Action.ShowBookDetails::class.java))
    }
    @Test
    fun `should show progress when trying to load books`() {
        simulateBooksCanNotBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        systemUnderTest.progress.observeForever {
            progressListener.invoke(it)
        }
        systemUnderTest.error.value?.retryAction?.invoke()
        Mockito.verify(progressListener).invoke(true)
    }

    @Test
    fun `should hide progress when books loaded`() {
        simulateBooksCanNotBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        systemUnderTest.progress.observeForever {
            progressListener.invoke(it)
        }
        simulateBooksCanBeLoaded()
        systemUnderTest.error.value?.retryAction?.invoke()
        Mockito.verify(progressListener).invoke(true)
        Mockito.verify(progressListener, Mockito.times(2)).invoke(false)
    }

    @Test
    fun `should hide progress on loading error`() {
        simulateBooksCanNotBeLoaded()
        val systemUnderTest = BooksListViewModel(bookdRepository, schedulerProvider)
        systemUnderTest.progress.observeForever {
            progressListener.invoke(it)
        }
        systemUnderTest.error.value?.retryAction?.invoke()
        Mockito.verify(progressListener).invoke(true)
        Mockito.verify(progressListener, Mockito.times(2)).invoke(false)
    }


    private fun mockSchedulers() {
        Mockito.`when`(schedulerProvider.ioScheduler()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulerProvider.uiScheduler()).thenReturn(Schedulers.trampoline())
    }

    private fun simulateBooksCanBeLoaded() {
        Mockito.`when`(bookdRepository.getBooks()).thenReturn(Flowable.just(listOf(createBook()).asResultSuccess()))
    }

    private fun simulateOnlyCachedBooksCanBeLoaded() {
        Mockito.`when`(bookdRepository.getBooks()).thenReturn(
            Flowable.just(
                listOf(createBook()).asResultError(
                    Exception()
                )
            )
        )
    }

    private fun simulateBooksCanNotBeLoaded() {
        Mockito.`when`(bookdRepository.getBooks()).thenReturn(Flowable.error(Exception()))
    }

    private fun createBook(): Book {
        return Book(
            id = "id",
            title = "title",
            description = "desc",
            coverUrl = "url"
        )
    }

}