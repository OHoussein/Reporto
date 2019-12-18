package dev.ohoussein.reportoandroid

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
    notifParam: NotifParam?
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
        notifParam?.let {
            createReportNotification(context, it)
        }
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
        //TODO Should be async
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

    private fun createReportNotification(
        context: Context,
        notifParam: NotifParam
    ) {
        val intent = Intent(context, ReportActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 1, intent, 0)

        val builder = NotificationCompat.Builder(context, "Report")
            .setContentTitle(appName)
            .setContentText(context.getString(notifParam.contentStrRes))
            .setSmallIcon(notifParam.icon)
            .setPriority(notifParam.priority)
            .setContentIntent(pendingIntent)

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
        private var notifParam : NotifParam? = null
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
         * Add a custom module
         */
        fun addModule(module: ReportoModule) = apply { modules.add(module) }

        /**
         * Customize the resultHandler which handle the result report data.
         * By default, Reporto use the [ZipFileHandler]
         */
        fun resultHandler(handler: ResultHandler) = apply { resultHandler = handler }

        /**
         * when set to true true, a notification bar is added that create a report when click on
         */
        fun showNotification(param : NotifParam = NotifParam()) = apply { notifParam = param }

        fun create(context: Context) = Reporto(context.applicationContext, modules, resultHandler, notifParam)
            .apply {
                instance = this
            }
    }

    data class NotifParam (
        @StringRes val contentStrRes: Int = R.string.create_report,
        @DrawableRes val icon: Int = R.drawable.ic_report,
        val priority: Int = NotificationCompat.PRIORITY_MIN,
        val id : Long = 1991
    )
}