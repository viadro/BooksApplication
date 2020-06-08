package com.seweryn.booksapplication.viewmodel

import androidx.lifecycle.MutableLiveData
import com.seweryn.booksapplication.data.BooksRepository
import com.seweryn.booksapplication.data.model.Book
import com.seweryn.booksapplication.utils.SchedulerProvider
import com.seweryn.booksapplication.utils.isValidUrl
import com.seweryn.booksapplication.viewmodel.liveDataModels.InputError
import com.seweryn.booksapplication.viewmodel.liveDataModels.Message

class AddBookViewModel(private val booksRepository: BooksRepository, schedulerProvider: SchedulerProvider) : BaseViewModel(schedulerProvider) {

    val titleError = MutableLiveData<InputError?>()
    val descriptionError = MutableLiveData<InputError?>()
    val coverUrlError = MutableLiveData<InputError?>()

    val progress = MutableLiveData<Boolean>()
    val message = MutableLiveData<Message>()
    var action: MutableLiveData<Action?> = MutableLiveData()

    fun confirmForm(title: String, description: String, coverUrl: String) {
        if(validate(title, description, coverUrl)) {
            progress.value = true
            titleError.value = null
            descriptionError.value = null
            coverUrlError.value = null
            executeForm(title, description, coverUrl)
        }
    }

    private fun executeForm(title: String, description: String, coverUrl: String) {
        execute(
            command = booksRepository.createBook(
                Book(
                    id = "id",
                    title = title,
                    description = description,
                    coverUrl = coverUrl
                )
            ),
            onSuccess = {
                progress.value = false
                message.value = Message.Success
                sendSingleEvent(action, Action.Close)
            },
            onError = {
                progress.value = false
                message.value = Message.Fail(handleError(it, null))
            }
        )
    }

    private fun validate(title: String, description: String, coverUrl: String): Boolean {
        var isValid = true
        if(!validateInput(title)) {
            isValid = false
            titleError.value = InputError.Empty
        }
        if(!validateInput(description)) {
            isValid = false
            descriptionError.value = InputError.Empty
        }
        if(!validateInput(coverUrl)) {
            isValid = false
            coverUrlError.value = InputError.Empty
        } else if(!coverUrl.isValidUrl()) {
            isValid = false
            coverUrlError.value = InputError.Invalid
        }
        return isValid
    }

    private fun validateInput(input: String): Boolean {
        return input.isNotEmpty()
    }

    sealed class Action {
        object Close : Action()
    }

}