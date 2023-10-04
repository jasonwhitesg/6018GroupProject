package com.example.drawingapp

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.SupervisorJob

class DrawingApplication : Application() {

    val scope = CoroutineScope(SupervisorJob())
    val db by lazy {DrawingDatabase.getDatabase(applicationContext)}
    val drawingRepository by lazy {DrawingRepository(scope, db.drawingDao())}
}