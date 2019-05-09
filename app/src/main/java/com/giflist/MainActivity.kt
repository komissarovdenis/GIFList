package com.giflist

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.giflist.view.FragmentTrending
import com.giflist.view.FragmentViewer

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_view, FragmentTrending())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                super.onBackPressed()
            }
        }
        return true
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.getBundleExtra(FragmentViewer.DATA_KEY)?.let { data ->
            val fragment = FragmentViewer()
            fragment.arguments = data
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.content_view, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
