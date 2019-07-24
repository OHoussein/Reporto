package dev.ohoussein.reporto

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val TAG = DatabaseHelper::class.java.simpleName

        const val DB_NAME = "reporto_test.db"
        const val DB_VERSION = 1

        const val TABLE_TASK = "task"
        const val TABLE_USER = "user"

        const val COL_TASK_TITLE = "title"
        const val COL_TASK_CREATED_AT = "created_at"
        const val COL_TASK_TODO_ID = "_id"
        const val COL_TASK_USER_ID = "user_id"

        const val COL_USER_ID = "_id"
        const val COL_USER_NAME = "name"

    }

    override fun onCreate(db: SQLiteDatabase) {
        Log.d(TAG, "Creating database tables")
        createTableTask(db)
        createTableUser(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "on upgrade from $oldVersion to $newVersion")
    }

    private fun createTableTask(db: SQLiteDatabase) {
        val sql = """CREATE TABLE $TABLE_TASK ($COL_TASK_TODO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TASK_USER_ID INTEGER,
                $COL_TASK_TITLE TEXT,
                $COL_TASK_CREATED_AT INTEGER)"""
        db.execSQL(sql)
    }

    private fun createTableUser(db: SQLiteDatabase) {
        val sql = """CREATE TABLE $TABLE_USER ($COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_USER_NAME TEXT)"""
        db.execSQL(sql)
    }

}