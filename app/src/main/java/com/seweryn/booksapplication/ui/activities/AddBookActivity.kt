package com.seweryn.booksapplication.ui.activities

import android.app.Activity
import android.os.Bundle
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.seweryn.booksapplication.R
import com.seweryn.booksapplication.ui.extensions.showConditionally
import com.seweryn.booksapplication.viewmodel.AddBookViewModel
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error
import com.seweryn.booksapplication.viewmodel.liveDataModels.InputError
import com.seweryn.booksapplication.viewmodel.liveDataModels.Message
import kotlinx.android.synthetic.main.activity_add_book.*

class AddBookActivity: BaseActivity<AddBookViewModel>() {

    companion object {
        val REQUEST_CODE = 23
    }

    override fun viewModel(): AddBookViewModel= ViewModelProvider(this, viewModelFactory).get(AddBookViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        setTitle(R.string.title_add_book)
        observeErrors()
        observeMessage()
        observeAction()
        observeProgress()
        formConfirmBtn.setOnClickListener {
            viewModel.confirmForm(
                title = titleInput.text.toString(),
                description = descriptionInput.text.toString(),
                coverUrl = coverInput.text.toString()
            )
        }
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    private fun observeMessage() {
        viewModel.message.observe(this, Observer { message ->
            when(message) {
                is Message.Success -> {
                    Toast.makeText(this, R.string.book_creation_success_msg, Toast.LENGTH_LONG).show()
                }
                is Message.Fail -> {
                    when(message.error) {
                        is Error.ConnectionError -> Toast.makeText(this, R.string.no_internet_error_message, Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(this, R.string.book_creation_fail_msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun observeProgress() {
        viewModel.progress.observe(this, Observer {
            confirmationProgress.showConditionally(it)
            formConfirmBtn.isEnabled = !it
        })
    }

    private fun observeErrors() {
        viewModel.titleError.observe(this, Observer { error ->
            displayError(error, titleInput, resources.getString(R.string.book_add_title_invalid_error), resources.getString(R.string.book_add_title_empty_error))
        })
        viewModel.descriptionError.observe(this, Observer {error ->
            displayError(error, descriptionInput, resources.getString(R.string.book_add_description_invalid_error), resources.getString(R.string.book_add_description_empty_error))
        })
        viewModel.coverUrlError.observe(this, Observer { error ->
            displayError(error, coverInput, resources.getString(R.string.book_add_cover_invalid_error), resources.getString(R.string.book_add_cover_empty_error))
        })
    }

    private fun displayError(error: InputError?, input: EditText, invalidErrorMessage: String, emptyErrorMessage: String) {
        when(error) {
            is InputError.Invalid -> input.error = invalidErrorMessage
            is InputError.Empty -> input.error = emptyErrorMessage
        }
    }

    private fun observeAction() {
        viewModel.action.observe(this, Observer { action ->
            when(action) {
                is AddBookViewModel.Action.Close -> {
                    setResult(Activity.RESULT_OK)
                    onBackPressed()
                }
            }
        })
    }

}