package dev.ohoussein.reportoandroid.module

import android.content.Context
import java.io.File
import java.io.IOException


class LogcatModule(private val params: LogParams) : ReportoModule {

    /**
     * @param onlyAppLog to filter the log by the app's pid
     * @param bufferName the log buffer [https://developer.android.com/studio/command-line/logcat#alternativeBuffers]
     * @param maxLines the max lines on the log file
     */
    data class LogParams(val onlyAppLog: Boolean = false, val bufferName: String? = null, val maxLines : Int = 500)

    companion object {
        const val BUFFER_EVENTSLOG = "events"
        const val BUFFER_RADIOLOG = "radio"
    }

    private val logcatArguments = listOf("-d", "-t${params.maxLines}", "-v", "time");

    @Throws(IOException::class)
    override fun collect(context: Context, parentDir: File): File {
        val logs = collectLogCat(
            bufferName = params.bufferName,
            filter = if (params.onlyAppLog) android.os.Process.myPid().toString() else null
        )
        val fileName = "reporto_log${params.bufferName ?: "cat"}.txt"
        return File(parentDir, fileName)
            .also {
                it.delete()
                it.createNewFile()
                it.writeText(logs)
            }
    }

    @Throws(IOException::class)
    private fun collectLogCat(bufferName: String? = null, filter: String? = null): String {
        val commandLine = mutableListOf("logcat")
        bufferName?.let {
            commandLine.add("-b")
            commandLine.add(bufferName)
        }

        commandLine.addAll(logcatArguments)
        val process = ProcessBuilder().command(commandLine).redirectErrorStream(true).start()
        try {
            return process.inputStream.bufferedReader()
                .useLines { seq ->
                    seq
                        .filter { filter == null || it.contains(filter) }
                        .joinToString("\n")
                }
        } finally {
            process.destroy()
        }
    }
}