package com.finverse

import android.app.Application
import com.finverse.di.AppContainer
import com.finverse.di.DefaultAppContainer

class FinverseApp : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
