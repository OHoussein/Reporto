package dev.ohoussein.reportoandroid.resulthandler

import android.app.Activity
import java.io.File

interface ResultHandler {

    fun handleResultFiles(activity: Activity, sourceDirFile: File, messageTitle: String? = null, message: String? = null)
}