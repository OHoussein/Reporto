package dev.ohoussein.reportoandroid.resulthandler

import android.app.Activity
import android.content.Intent
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import dev.ohoussein.reportoandroid.R
import dev.ohoussein.reportoandroid.Reporto
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Create a zip file that contains all the report files and share it via an intent
 */
open class ZipFileHandler : ResultHandler {

    companion object {
        const val FILE_PROVIDER_AUTHORITY = "dev.ohoussein.reportoandroid.provider"
    }

    override fun handleResultFiles(activity: Activity, sourceDirFile: File, messageTitle: String?, message: String?) {

        val zipName = "${Reporto.INSTANCE.appName}_report_${DateFormat.getDateTimeInstance(
            DateFormat.SHORT,
            DateFormat.SHORT
        ).format(Date())}.zip"
            .replace(Regex("""[/\s+:]"""), "_")
        val destDir = File(activity.cacheDir.path + "/zip_report/zip_${System.currentTimeMillis()}")
        val destFile = File(destDir, zipName)
        destDir.mkdirs()
        zip(sourceDirFile, destFile)
        sourceDirFile.deleteRecursively()
        shareZipFile(activity, destFile, messageTitle, message)
    }

    protected fun shareZipFile(activity: Activity, zipFile: File, messageTitle: String?, message: String?) {
        val uri = FileProvider.getUriForFile(activity, FILE_PROVIDER_AUTHORITY, zipFile)

        ShareCompat.IntentBuilder.from(activity)
            .setType("application/zip")
            .setStream(uri)
            .setSubject(messageTitle)
            .setText(message)
            .setChooserTitle(R.string.share_report_file_title)
            .createChooserIntent()
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            .apply { activity.startActivity(this) }
    }

    protected fun zip(sourceDirFile: File, destFile: File) {
        ZipOutputStream(BufferedOutputStream(FileOutputStream(destFile))).use { stream ->

            sourceDirFile.walk()
                .filter { !it.isDirectory }
                .forEach { file ->
                    val zipEntry = ZipEntry(file.path.substring(sourceDirFile.path.toString().length + 1))

                    stream.putNextEntry(zipEntry)
                    stream.write(file.readBytes())
                    stream.closeEntry()
                }
        }
    }
}