package com.seweryn.booksapplication.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.seweryn.booksapplication.data.AuthenticationManager
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.utils.WithMockito
import com.seweryn.booksapplication.viewmodel.liveDataModels.InputError
import io.reactivex.Completable
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
import java.lang.Exception
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error

@RunWith(MockitoJUnitRunner.Silent::class)
class LoginViewModelTest : WithMockito {
    private lateinit var systemUnderTest: LoginViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()
    @Mock
    lateinit var authenticationManager: AuthenticationManager

    @Mock
    lateinit var schedulerProvider: SchedulerProvider

    @Mock
    lateinit var progressListener: (Boolean) -> Unit

    @Before
    fun setUp() {
        systemUnderTest = LoginViewModel(authenticationManager, schedulerProvider)
        mockSchedulers()
    }

    @Test
    fun `should not log in when username is empty`() {
        systemUnderTest.logIn("", "password")
        Mockito.verify(authenticationManager, Mockito.never()).logInUser(any(), any())
    }

    @Test
    fun `should not log in when password is empty`() {
        systemUnderTest.logIn("username", "")
        Mockito.verify(authenticationManager, Mockito.never()).logInUser(any(), any())
    }

    @Test
    fun `should set username error when empty`() {
        systemUnderTest.logIn("", "password")
        assertNotNull(systemUnderTest.usernameError)
        assertEquals(InputError.Empty, systemUnderTest.usernameError.value)
    }

    @Test
    fun `should set password error when empty`() {
        systemUnderTest.logIn("username", "")
        assertNotNull(systemUnderTest.passwordError)
        assertEquals(InputError.Empty, systemUnderTest.passwordError.value)
    }

    @Test
    fun `should log in when input is valid`() {
        simulateUserCanLogIn()
        systemUnderTest.logIn("username", "password")
        Mockito.verify(authenticationManager).logInUser(any(), any())
    }

    @Test
    fun `should show progress when trying to log in`() {
        simulateUserCanLogIn()
        systemUnderTest.progress.observeForever {
            progressListener.invoke(it)
        }
        systemUnderTest.logIn("username", "password")
        Mockito.verify(progressListener).invoke(true)
    }

    @Test
    fun `should hide progress when logged in`() {
        simulateUserCanLogIn()
        systemUnderTest.progress.observeForever {
            progressListener.invoke(it)
        }
        systemUnderTest.logIn("username", "password")
        Mockito.verify(progressListener).invoke(true)
        Mockito.verify(progressListener).invoke(false)
    }

    @Test
    fun `should hide progress on login error`() {
        simulateUserCanNotLogIn()
        systemUnderTest.progress.observeForever {
            progressListener.invoke(it)
        }
        systemUnderTest.logIn("username", "password")
        Mockito.verify(progressListener).invoke(true)
        Mockito.verify(progressListener).invoke(false)
    }

    @Test
    fun `should show error on login error`() {
        simulateUserCanNotLogIn()
        systemUnderTest.progress.observeForever {
            progressListener.invoke(it)
        }
        systemUnderTest.logIn("username", "password")
        assertNotNull(systemUnderTest.error)
        assertThat(systemUnderTest.error.value, CoreMatchers.instanceOf(Error::class.java))
    }

    private fun simulateUserCanLogIn() {
        Mockito.`when`(authenticationManager.logInUser(any(), any())).thenReturn(Completable.complete())
    }

    private fun simulateUserCanNotLogIn() {
        Mockito.`when`(authenticationManager.logInUser(any(), any()))
            .thenReturn(Completable.error(Exception()))
    }


    private fun mockSchedulers() {
        Mockito.`when`(schedulerProvider.ioScheduler()).thenReturn(Schedulers.trampoline())
        Mockito.`when`(schedulerProvider.uiScheduler()).thenReturn(Schedulers.trampoline())
    }


}