package com.seweryn.booksapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import com.seweryn.booksapplication.data.AuthenticationManager
import com.seweryn.booksapplication.data.local.sharedprefs.user.UserPreferences
import com.seweryn.booksapplication.utils.SchedulerProvider

class MainViewModel(authenticationManager: AuthenticationManager,
                    schedulerProvider: SchedulerProvider
) : BaseViewModel(schedulerProvider) {

    val state = MutableLiveData<InitialState>()

    init {
        load(
            command = authenticationManager.isUserLoggedIn(),
            onNext = { isLoggedIn ->
                state.value =
                    if(isLoggedIn) InitialState.MAIN else InitialState.LOGIN
            },
            onError = {}
        )

    }

    enum class InitialState{
        LOGIN, MAIN
    }
}