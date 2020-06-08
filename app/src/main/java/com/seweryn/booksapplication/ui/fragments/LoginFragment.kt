package com.seweryn.booksapplication.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.seweryn.booksapplication.R
import com.seweryn.booksapplication.ui.extensions.showConditionally
import com.seweryn.booksapplication.viewmodel.LoginViewModel
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error
import com.seweryn.booksapplication.viewmodel.liveDataModels.InputError
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class LoginFragment : BaseFragment<LoginViewModel>() {

    private var errorSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        view.loginBtn.setOnClickListener { viewModel.logIn(username.text.toString(), password.text.toString()) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observePasswordError()
        observeUsernameError()
        observeProgress()
        observeError()
    }

    override fun viewModel() = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)

    private fun observeUsernameError() {
        viewModel.usernameError.observe(viewLifecycleOwner, Observer {error ->
            when(error) {
                is InputError.Empty -> username.error = resources.getString(R.string.login_username_empty_error)
            }
        })
    }

    private fun observePasswordError() {
        viewModel.passwordError.observe(viewLifecycleOwner, Observer {error ->
            when(error) {
                is InputError.Empty -> password.error = resources.getString(R.string.login_password_empty_error)
            }
        })
    }

    private fun observeProgress() {
        viewModel.progress.observe(viewLifecycleOwner, Observer {
            loginBtn.isEnabled = !it
            progress.showConditionally(it)
        })
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner, Observer {error ->
            when(error) {
                null -> {
                    errorSnackbar?.dismiss()
                    errorSnackbar = null
                }
                is Error.AccessDeniedError -> {
                    showErrorSnackbar(R.string.login_failed_msg, error.retryAction)
                }
                is Error.ConnectionError -> {
                    showErrorSnackbar(R.string.no_internet_error_message, error.retryAction)
                }
                else -> {
                    showErrorSnackbar(R.string.generic_error_message, error.retryAction)
                }
            }
        })
    }

    private fun showErrorSnackbar(messageResId: Int, retryAction: (() -> Unit)?) {
        errorSnackbar = Snackbar.make(root, messageResId, Snackbar.LENGTH_INDEFINITE)
        retryAction?.let { action -> errorSnackbar?.setAction(R.string.action_retry) { action.invoke() } }
        errorSnackbar?.show()
    }
}