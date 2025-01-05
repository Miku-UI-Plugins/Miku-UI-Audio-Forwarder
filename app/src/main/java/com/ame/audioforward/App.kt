package com.ame.audioforward

import android.app.Application
import com.ame.audioforward.util.SPUtil

class App : Application() {
    companion object {
        private lateinit var instance: App

        val application: App
            get() {
                return instance
            }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        SPUtil.init(this)
    }
}
