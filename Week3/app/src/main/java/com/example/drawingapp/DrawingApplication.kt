package com.example.drawingapp

import android.app.Application
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Need to Initialize Firebase before anything else
        FirebaseApp.initializeApp(this)
    }

    val scope = CoroutineScope(SupervisorJob())
    val db by lazy { DrawingDatabase.getDatabase(applicationContext) }
    val drawingRepository by lazy { DrawingRepository(scope, db.drawingDao()) }

}
