package dev.ohoussein.reportoandroid.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import dev.ohoussein.reportoandroid.R
import dev.ohoussein.reportoandroid.Reporto
import kotlinx.android.synthetic.main.activity_report.*


class ReportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
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
