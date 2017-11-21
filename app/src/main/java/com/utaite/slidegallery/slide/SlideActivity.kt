package com.utaite.slidegallery.slide

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.utaite.slidegallery.R
import com.utaite.slidegallery.util.setView
import kotlinx.android.synthetic.main.activity_slide.*


class SlideActivity : AppCompatActivity() {

    companion object {

        val IMAGE = "IMAGE"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(this, R.layout.activity_slide)

        val dataSet = intent.getStringArrayExtra(SlideActivity.IMAGE)
        slideView.adapter = SlideAdapter(this, dataSet.toList().map { Uri.parse(it) })
    }

}
