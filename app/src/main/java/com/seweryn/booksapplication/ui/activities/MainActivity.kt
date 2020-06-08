package com.seweryn.booksapplication.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.seweryn.booksapplication.R
import com.seweryn.booksapplication.ui.fragments.BooksListFragment
import com.seweryn.booksapplication.ui.fragments.LoginFragment
import com.seweryn.booksapplication.viewmodel.MainViewModel
import dagger.android.AndroidInjection

class MainActivity : BaseActivity<MainViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        viewModel.state.observe(this, Observer { state ->
            showFragment(
                when (state) {
                    MainViewModel.InitialState.LOGIN -> {
                        supportActionBar?.hide()
                        LoginFragment()
                    }
                    MainViewModel.InitialState.MAIN -> {
                        supportActionBar?.show()
                        setTitle(R.string.title_list)
                        BooksListFragment()
                    }
                },
                state.name
            )


        })
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        val foundFragment = supportFragmentManager.findFragmentByTag(tag)
        if (foundFragment == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, fragment, tag)
                commit()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == AddBookActivity.REQUEST_CODE) {
            supportFragmentManager.findFragmentByTag(MainViewModel.InitialState.MAIN.name)?.let {
                (it as BooksListFragment).booksChanged()
            }
        }
    }

    override fun viewModel() =
        ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
}
