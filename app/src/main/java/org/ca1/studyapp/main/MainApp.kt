package org.ca1.studyapp.main

import android.app.Application
import com.google.firebase.FirebaseApp
import org.ca1.studyapp.data.FirebaseAuthManager
import org.ca1.studyapp.data.FirebaseStore
import timber.log.Timber


class MainApp : Application() {

    lateinit var store: FirebaseStore
    lateinit var authManager: FirebaseAuthManager

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        authManager = FirebaseAuthManager(this)

        store = FirebaseStore(authManager)

        Timber.plant(Timber.DebugTree())

        Timber.i("Task started")
    }
}