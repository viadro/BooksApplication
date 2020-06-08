package com.seweryn.booksapplication.ui.activities


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.seweryn.booksapplication.R
import com.seweryn.booksapplication.ui.extensions.showConditionally
import com.seweryn.booksapplication.utils.ConfirmationDialogHelper
import com.seweryn.booksapplication.viewmodel.BookDetailsViewModel
import com.seweryn.booksapplication.viewmodel.liveDataModels.Error
import com.seweryn.booksapplication.viewmodel.liveDataModels.Message
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_book_details.*

class BookDetailsActivity : BaseActivity<BookDetailsViewModel>() {

    companion object {
        private val BOOK_ID = "id"

        fun createIntent(context: Context, bookId: String): Intent {
            return Intent(context, BookDetailsActivity::class.java).apply {
                putExtra(BOOK_ID, bookId)
            }
        }

        private fun getBookId(intent: Intent): String {
            return intent.getStringExtra(BOOK_ID)
        }
    }

    override fun viewModel()= ViewModelProvider(this, viewModelFactory).get(BookDetailsViewModel::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_details)
        setTitle(R.string.title_book_details)
        viewModel.init(getBookId(intent))
        observeBook()
        observeAction()
        observeMessage()
        observeProgress()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.details_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_book -> {
                ConfirmationDialogHelper.showConfirmationDialog(this,
                    resources.getString(R.string.book_delete_confirmation_message)) {  viewModel.deleteBook() }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun observeBook() {
        viewModel.book.observe(this, Observer { book ->
            bookTitle.text = book.title
            bookDescription.text = book.description
            if(!book.coverUrl.isNullOrEmpty()) {
                Picasso.get().load(book.coverUrl).error(R.drawable.cover_placeholder)
                    .placeholder(R.drawable.image_progress_animation)
                    .into(bookCover)
            } else {
                bookCover.setImageDrawable(getDrawable(R.drawable.cover_placeholder))
            }
        })
    }

    private fun observeMessage() {
        viewModel.deleteMessage.observe(this, Observer {message ->
            when(message) {
                is Message.Success -> {
                    Toast.makeText(this, R.string.book_delete_success_msg, Toast.LENGTH_LONG).show()
                }
                is Message.Fail -> {
                    when(message.error) {
                        is Error.ConnectionError -> Toast.makeText(this, R.string.no_internet_error_message, Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(this, R.string.book_delete_fail_msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        })
    }

    private fun observeProgress() {
        viewModel.progress.observe(this, Observer {
            detailsProgress.showConditionally(it)
        })
    }

    private fun observeAction() {
        viewModel.action.observe(this, Observer { action ->
            when(action) {
                is BookDetailsViewModel.Action.Close -> onBackPressed()
            }
        })
    }

}
