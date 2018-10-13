package com.wander.floatview

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.wander.floatview.slideback.SlideBackHelper
import com.wander.floatview.slideback.SlideConfig
import kotlinx.android.synthetic.main.activity_seconde.*

class SecondeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seconde)
        val slideConfig = SlideConfig.Builder().rotateScreen(true).create()
        SlideBackHelper.attach(this, MainActivity.activityHelper, slideConfig, null)

        val animator = floatView.animate().rotation(180f)
        animator.duration = 3000
        animator.start()

        floatView.setOnClickListener {
            startActivity(Intent(this, SecondeActivity::class.java))
        }
    }
}
