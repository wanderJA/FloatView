package com.wander.floatview

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.wander.floatview.slideback.ActivityHelper

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val activityHelper = ActivityHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            startActivity(Intent(this, SecondeActivity::class.java))
        }
        val animate = fab.animate().rotation(360f)
        animate.duration = 10000
        animate.start()
        mainLayout.background.alpha = 255

        application.registerActivityLifecycleCallbacks(activityHelper)
        mainDrag.dragStateListener = object : DragStateListener {
            override fun onDrag(percent: Float) {
                mainLayout.background.alpha = ((1 - percent) * 255).toInt()
            }

            override fun onClose() {
                mainDrag.visibility = View.GONE
                mainLayout.background.alpha = 0
            }

            override fun onOpen() {
            }
        }
    }

    override fun onBackPressed() {
        if (mainDrag.visibility == View.VISIBLE) {
            mainDrag.dismiss()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                mainDrag.visibility = View.VISIBLE
                mainLayout.background.alpha = 255
                showSlide()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showSlide() {
    }
}
