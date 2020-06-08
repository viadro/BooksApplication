package com.seweryn.booksapplication.ui.extensions

import android.view.View

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun View.showConditionally(isVisible: Boolean) {
    if(isVisible) this.show() else this.hide()
}