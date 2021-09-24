package dev.ohoussein.reportoandroid.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dev.ohoussein.reportoandroid.R
import dev.ohoussein.reportoandroid.Reporto

class ReportActivity : AppCompatActivity() {

    private val messageInput: TextView by lazy { findViewById<TextView>(R.id.messageInput) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        val appToolbar = findViewById<Toolbar>(R.id.appToolbar)
        setSupportActionBar(appToolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.report_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send -> Reporto.instance.report(
                fromActivity = this,
                message = messageInput.text.toString()) { finish() }
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
