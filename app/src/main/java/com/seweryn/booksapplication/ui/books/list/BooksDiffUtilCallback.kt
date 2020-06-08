package com.seweryn.booksapplication.ui.books.list

import androidx.recyclerview.widget.DiffUtil
import com.seweryn.booksapplication.viewmodel.BooksListViewModel.BookData

class BooksDiffUtilCallback(
    private val newList: List<BookData>,
    private val oldList: List<BookData>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return newList[newItemPosition].book.id == oldList[oldItemPosition].book.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val newItem = newList[newItemPosition]
        val oldItem = oldList[oldItemPosition]
        return newItem == oldItem
    }
}