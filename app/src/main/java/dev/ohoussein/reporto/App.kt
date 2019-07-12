package dev.ohoussein.reporto

import android.app.Application
import dev.ohoussein.reportoandroid.Reporto
import dev.ohoussein.reportoandroid.module.LogcatModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Reporto
            .Builder()
            .addLogcatModule()
            .addLogcatModule(LogcatModule.LogParams(bufferName = LogcatModule.BUFFER_EVENTSLOG))
            .addPreferencesModule()
            .addDatabaseModule()
            .showNotification(true)
            .create(this)
    }
}