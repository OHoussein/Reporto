package dev.ohoussein.reportoandroid.module

import android.content.Context
import java.io.File

class PreferencesModule : ReportoModule {
    override fun collect(context: Context, parentDir: File): File {
        val srcDir = File(context.applicationInfo.dataDir, "shared_prefs")
        val dstDir = File(parentDir, "shared_prefs")
        srcDir.copyRecursively(dstDir, true)
        return srcDir
    }
}