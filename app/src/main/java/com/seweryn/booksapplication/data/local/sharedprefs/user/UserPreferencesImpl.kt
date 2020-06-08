package com.seweryn.booksapplication.data.local.sharedprefs.user

import android.content.Context
import android.content.SharedPreferences
import com.seweryn.booksapplication.data.local.sharedprefs.SharedPrefs
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class UserPreferencesImpl(context: Context) : SharedPrefs(context), UserPreferences {
    private val PREFERENCE_KEY = "userPrefs"
    private val CREDENTIALS = "credentials"

    private val prefSubject = BehaviorSubject.createDefault(sharedPreferences)

    private val prefChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, _ ->
            prefSubject.onNext(sharedPreferences)
        }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(prefChangeListener)
    }

    override fun preferenceKey() = PREFERENCE_KEY

    override fun getCredentials(): String {
        return getString(CREDENTIALS)
    }

    override fun observeCredentials(): Observable<String> {
        return prefSubject.map { getString(CREDENTIALS) }
    }

    override fun rememberCredentials(credentials: String) {
        putString(CREDENTIALS, credentials)
    }

    override fun clearCredentials() {
        putString(CREDENTIALS, "")
    }

}