package dev.ohoussein.reportoandroid

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.ohoussein.reportoandroid.activity.ReportActivity
import dev.ohoussein.reportoandroid.module.DatabaseModule
import dev.ohoussein.reportoandroid.module.LogcatModule
import dev.ohoussein.reportoandroid.module.PreferencesModule
import dev.ohoussein.reportoandroid.module.ReportoModule
import dev.ohoussein.reportoandroid.resulthandler.ResultHandler
import dev.ohoussein.reportoandroid.resulthandler.ZipFileHandler
import java.io.File
import java.text.DateFormat
import java.util.*


class Reporto private constructor(
    context: Context,
    private val modules: List<ReportoModule>,
    private val resultHandler: ResultHandler,
    showNotification: Boolean
) {

    companion object {
        lateinit var INSTANCE: Reporto
    }
    //TODO delete reports
    //TODO cleanup

    val appName: String

    init {
        if (showNotification)
            createReportNotification(context)

        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        appName =
            if (stringId == 0) applicationInfo.nonLocalizedLabel?.toString() ?: "" else context.getString(stringId)
    }

    fun report(fromActivity: Activity, title: String? = null, message: String? = null, then: (() -> Unit)? = null) {
        val dir = File(fromActivity.cacheDir, System.currentTimeMillis().toString())
        dir.mkdirs()
        modules.forEach {
            it.collect(fromActivity, dir)
        }
        val messageTitle = title ?: fromActivity.getString(
            R.string.report_message_title,
            appName,
            DateFormat.getDateTimeInstance().format(Date())
        )

        File(dir, "message.txt").apply {
            writeText("$messageTitle\n$message")
        }

        resultHandler.handleResultFiles(fromActivity, dir, messageTitle, message)
        then?.invoke()
    }

    private fun createReportNotification(context: Context) {
        val intent = Intent(context, ReportActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, 0)

        val builder = NotificationCompat.Builder(context, "Report")
            .setContentTitle(appName)
            .setContentText(context.getString(R.string.create_report))
            .setSmallIcon(R.drawable.ic_report)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setContentIntent(pendingIntent)
            .setOngoing(true)

        val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel("Report", "Reporto", NotificationManager.IMPORTANCE_LOW)
                .apply {
                    notifManager.createNotificationChannel(this)
                }
        }
        notifManager.notify(1991, builder.build())
    }

    class Factory {

        private val modules = mutableListOf<ReportoModule>()
        var showNotification = true
        var resultHandler: ResultHandler = ZipFileHandler()

        fun addLogcatModule(params: LogcatModule.LogParams = LogcatModule.LogParams()) =
            modules.add(LogcatModule(params))

        fun addDatabaseModule() = modules.add(DatabaseModule())

        fun addPreferencesModule() = modules.add(PreferencesModule())

        fun create(context: Context): Reporto {
            INSTANCE = Reporto(context.applicationContext, modules, resultHandler, showNotification)
            return INSTANCE
        }
    }
}