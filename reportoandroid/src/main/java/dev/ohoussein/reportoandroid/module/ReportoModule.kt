package dev.ohoussein.reportoandroid.module

import android.content.Context
import java.io.File

interface ReportoModule {
    /**
     * Collect the data and return the destination file or folder
     */
    fun collect(context: Context, parentDir: File): File
}