package org.ca1.studyapp.main

import android.app.Application
import com.google.firebase.FirebaseApp
import org.ca1.studyapp.data.FirebaseStore
import timber.log.Timber


class MainApp : Application() {

    lateinit var store: FirebaseStore

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        store = FirebaseStore()
        Timber.plant(Timber.DebugTree())
        Timber.i("Task started")
    }
}