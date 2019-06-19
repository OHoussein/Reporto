package dev.ohoussein.reporto

import android.content.ContentValues

object DataGenerator {

    fun generateUsers(count: Int = 5) =
        (0..count).map { i ->
            ContentValues().apply {
                put(DatabaseHelper.COL_USER_NAME, "User $i")
            }
        }

    fun generateTasks(userId: Long, count: Int = 5) =
        (0..count).map { i ->
            ContentValues().apply {
                put(DatabaseHelper.COL_TASK_TITLE, "Task $i for user $userId")
                put(DatabaseHelper.COL_TASK_USER_ID, userId)
                put(DatabaseHelper.COL_TASK_CREATED_AT, System.currentTimeMillis())
            }
        }
}