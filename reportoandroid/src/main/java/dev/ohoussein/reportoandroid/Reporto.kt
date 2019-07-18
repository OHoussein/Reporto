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
        @Volatile
        lateinit var instance: Reporto
            private set

        private const val NOTIF_ID = 1991
    }

    val appName: String

    init {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        appName =
            if (stringId == 0) applicationInfo.nonLocalizedLabel?.toString() ?: "" else context.getString(stringId)
        if (showNotification)
            createReportNotification(context)
    }

    /**
     * Create a new report
     * @param fromActivity the caller activity
     * @param reportTitle an optional report title
     * @param message an optional message
     * @param then executed when the report is done
     */
    fun report(
        fromActivity: Activity,
        reportTitle: String? = null,
        message: String? = null,
        then: (() -> Unit)? = null
    ) {
        val dir = File(fromActivity.cacheDir, System.currentTimeMillis().toString())
        dir.mkdirs()
        modules.forEach {
            it.collect(fromActivity, dir)
        }
        val messageTitle = reportTitle ?: fromActivity.getString(
            R.string.report_message_title,
            appName,
            DateFormat.getDateTimeInstance().format(Date())
        )

        File(dir, "message.txt").apply {
            writeText("$messageTitle\n\n$message")
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
        notifManager.notify(NOTIF_ID, builder.build())
    }

    class Builder {

        private val modules = mutableListOf<ReportoModule>()
        private var showNotification = true
        private var resultHandler: ResultHandler = ZipFileHandler()

        /**
         * Add add the last device's log
         * @see LogcatModule.LogParams
         */
        fun addLogcatModule(params: LogcatModule.LogParams = LogcatModule.LogParams()) =
            apply { modules.add(LogcatModule(params)) }

        /**
         * Add all the app databases files
         */
        fun addDatabaseModule() = apply { modules.add(DatabaseModule()) }

        /**
         * Add the preferences in the report in their xml format
         */
        fun addPreferencesModule() = apply { modules.add(PreferencesModule()) }

        /**
         * Customize the resultHandler which handle the result report data.
         * By default, Reporto use the [ZipFileHandler]
         */
        fun resultHandler(handler: ResultHandler) = apply { resultHandler = handler }

        /**
         * when set to true true, a notification bar is added that create a report when click on
         */
        fun showNotification(show: Boolean) = apply { showNotification = show }

        fun create(context: Context) = Reporto(context.applicationContext, modules, resultHandler, showNotification)
            .apply {
                instance = this
            }
    }
}