package dev.ohoussein.reporto

import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }

    private val prefs1  by lazy { getSharedPreferences("dev.ohoussein.reporto.prefs1", MODE_PRIVATE) }
    private val prefs2  by lazy { getSharedPreferences("dev.ohoussein.reporto.prefs2", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dbHelper = DatabaseHelper(this)

        findViewById<View>(R.id.btnFillDb).setOnClickListener {
            insertFakeDataToDb(dbHelper.writableDatabase)
        }

        findViewById<View>(R.id.btnFillPrefs).setOnClickListener {
            insertFakeDataToPrefs()
        }
    }

    private fun insertFakeDataToDb(database: SQLiteDatabase) {
        database.use { db ->
            val user = DataGenerator.generateUsers(1).first()
            val userRowId = db.insert(DatabaseHelper.TABLE_USER, null, user)
            Log.d(TAG, "User User($user) inserted with id $userRowId")
            DataGenerator.generateTasks(userRowId, 20).forEach { task ->
                db.insert(DatabaseHelper.TABLE_TASK, null, task)
                Log.d(TAG, "New task inserted: Tasks($task)")
            }
        }
    }

    private fun insertFakeDataToPrefs() {
        var value = System.currentTimeMillis()
        prefs1.edit().apply {
            (0..20).map { i ->
                putLong("Key $i", value)
                value++
            }
            apply()
        }
        prefs2.edit().apply {
            (0..10).map { i ->
                putString("key $i", value.toString())
                value++
            }
            apply()
        }
    }
}
