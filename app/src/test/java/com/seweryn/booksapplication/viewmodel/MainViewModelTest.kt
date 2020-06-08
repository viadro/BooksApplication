package com.seweryn.booksapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.seweryn.booksapplication.data.AuthenticationManager
import com.seweryn.booksapplication.utils.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner.Silent::class)
class MainViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var authenticationManager: AuthenticationManager

    @Mock
    lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setUp() {
        mockSchedulers()
    }

    @Test
    fun `should set state to login when user is not logged`() {
        simulateThatUserIsNotLoggedIn()
        val systemUnderTest = MainViewModel(authenticationManager, schedulerProvider)
        assertNotNull(systemUnderTest.state)
        assertEquals(MainViewModel.InitialState.LOGIN, systemUnderTest.state.value)
    }

    @Test
    fun `should set state to main when user is logged in`() {
        simulateThatUserIsLoggedIn()
        val systemUnderTest = MainViewModel(authenticationManager, schedulerProvider)
        assertNotNull(systemUnderTest.state)
        assertEquals(MainViewModel.InitialState.MAIN, systemUnderTest.state.value)
    }

    private fun simulateThatUserIsNotLoggedIn() {
        Mockito.`when`(authenticationManager.isUserLoggedIn()).thenReturn(Observable.just(false))
    }

    private fun simulateThatUserIsLoggedIn() {
        Mockito.`when`(authenticationManager.isUserLoggedIn()).thenReturn(Observable.just(true))
    }

    private fun mockSchedulers() {
        Mockito.`when`(schedulerProvider.ioScheduler()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulerProvider.uiScheduler()).thenReturn(Schedulers.trampoline())
    }
}