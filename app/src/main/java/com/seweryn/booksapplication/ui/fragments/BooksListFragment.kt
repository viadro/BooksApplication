package com.seweryn.booksapplication.ui.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.seweryn.booksapplication.R
import com.seweryn.booksapplication.ui.activities.AddBookActivity
import com.seweryn.booksapplication.ui.activities.BookDetailsActivity
import com.seweryn.booksapplication.ui.books.list.BooksListRecyclerAdapter
import com.seweryn.booksapplication.ui.extensions.showConditionally
import com.seweryn.booksapplication.viewmodel.BooksListViewModel
import kotlinx.android.synthetic.main.fragment_books_list.*
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error
import kotlinx.android.synthetic.main.fragment_books_list.view.*

class BooksListFragment : BaseFragment<BooksListViewModel>() {

    private var errorSnackbar: Snackbar? = null

    private val adapter = BooksListRecyclerAdapter()

    override fun viewModel() = ViewModelProvider(this, viewModelFactory).get(BooksListViewModel::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_books_list, container, false)
        view.booksList.layoutManager = LinearLayoutManager(context)
        view.booksList.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeBooks()
        observeAction()
        observeError()
        observeProgress()
        fabAddBook.setOnClickListener {
            context?.let {
                (it as Activity).startActivityForResult(Intent(it, AddBookActivity::class.java), AddBookActivity.REQUEST_CODE)
            }
        }
    }

    fun booksChanged() {
        viewModel.booksChanged()
    }

    private fun observeBooks() {
        viewModel.books.observe(viewLifecycleOwner, Observer {
            adapter.updateBooks(it)
        })
    }

    private fun observeProgress() {
        viewModel.progress.observe(viewLifecycleOwner, Observer {
            listProgress.showConditionally(it)
        })
    }

    private fun observeAction() {
        viewModel.action.observe(viewLifecycleOwner, Observer { action ->
            when(action) {
                is BooksListViewModel.Action.ShowBookDetails -> {
                    context?.let {
                        (it as Activity).startActivity(BookDetailsActivity.createIntent(it, action.id))
                    }
                }
            }
        })
    }

    private fun observeError() {
        viewModel.error.observe(viewLifecycleOwner, Observer {error ->
            when(error) {
                is Error.ConnectionError -> showErrorSnackbar(R.string.no_internet_error_message, error.retryAction)
                null -> {
                    errorSnackbar?.dismiss()
                    errorSnackbar = null
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