package com.philisa.volunteers

import android.app.Application
import com.google.firebase.FirebaseApp

class PhilisaApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
