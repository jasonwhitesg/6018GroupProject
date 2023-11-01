package com.example.drawingapp

import android.app.Application
import com.google.firebase.FirebaseApp
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Need to Initialize Firebase before anything else
        FirebaseApp.initializeApp(this)
    }
    val client = HttpClient(Android)
    val scope = CoroutineScope(SupervisorJob())
    val db by lazy { DrawingDatabase.getDatabase(applicationContext) }
    val drawingRepository by lazy { DrawingRepository(scope, db.drawingDao()) }

}
