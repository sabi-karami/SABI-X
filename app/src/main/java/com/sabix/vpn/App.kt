package com.sabix.vpn

import android.app.Application

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SettingsStore.init(this)
    }
}
