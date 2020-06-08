package com.seweryn.booksapplication.ui.books.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.seweryn.booksapplication.R
import com.seweryn.booksapplication.ui.extensions.showConditionally
import com.seweryn.booksapplication.viewmodel.BooksListViewModel.BookData
import kotlinx.android.synthetic.main.item_book_list.view.*

class BooksListRecyclerAdapter : RecyclerView.Adapter<BooksListRecyclerAdapter.ViewHolder>() {

    private var books: List<BookData> = mutableListOf()

    fun updateBooks(books: List<BookData>) {
        val diff = DiffUtil.calculateDiff(
            BooksDiffUtilCallback(
                books,
                this.books
            )
        )
        this.books = books
        diff.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_book_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = books.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(books[position], isItemOnLastPosition(position))
    }

    private fun isItemOnLastPosition(position: Int) = position == itemCount - 1

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(bookData: BookData, isLastPosition: Boolean) {
            itemView.bookTitle.text = bookData.book.title
            itemView.contentDivider.showConditionally(!isLastPosition)
            itemView.setOnClickListener { bookData.selectAction.invoke() }
        }
    }
}