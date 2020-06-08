package com.seweryn.booksapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import com.seweryn.booksapplication.data.AuthenticationManager
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.viewmodel.liveDataModels.InputError
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error
import javax.inject.Inject

class LoginViewModel @Inject constructor(
    private val authenticationManager: AuthenticationManager,
    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val usernameError = MutableLiveData<InputError?>()
    val passwordError = MutableLiveData<InputError?>()
    val error = MutableLiveData<Error?>()
    val progress = MutableLiveData<Boolean>()

    fun logIn(username: String, password: String) {
        if (validate(username, password)) {
            progress.value = true
            error.value = null
            execute(
                command = authenticationManager.logInUser(username, password),
                onSuccess = {
                    progress.value = false
                },
                onError = {
                    progress.value = false
                    error.value = handleError(it, null)
                }
            )
        }
    }

    private fun validate(username: String, password: String): Boolean {
        var isValid = true
        if (!validateInput(username)) {
            isValid = false
            usernameError.value = InputError.Empty
        }
        if (!validateInput(password)) {
            isValid = false
            passwordError.value = InputError.Empty
        }
        return isValid
    }

    private fun validateInput(input: String): Boolean {
        return input.isNotEmpty()
    }
}
