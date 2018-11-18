package ru.pyrovsergey.cameraandfiltersproject.di

import android.app.Application

class App : Application() {
    lateinit var instance: Application

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}