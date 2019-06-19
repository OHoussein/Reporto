package dev.ohoussein.reportoandroid.module

import android.content.Context
import java.io.File

class DatabaseModule : ReportoModule {
    override fun collect(context: Context, parentDir: File): File {
        val dbDir = File(parentDir, "databases")
        dbDir.mkdirs()
        context.databaseList()
            .map { dbName ->
                 context.getDatabasePath(dbName)
            }
            .map {
                val dest = File(dbDir, it.name)
                it.copyTo(dest)
                dest
            }
        return dbDir
    }
}