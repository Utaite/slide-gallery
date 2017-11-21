package com.utaite.slidegallery.full

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.utaite.slidegallery.R
import com.utaite.slidegallery.util.setView
import kotlinx.android.synthetic.main.activity_full.*


class FullActivity : AppCompatActivity() {

    companion object {

        val IMAGE = "IMAGE"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setView(this, R.layout.activity_full)

        val image = Uri.parse(intent.getStringExtra(IMAGE))
        supportActionBar?.title = image.lastPathSegment
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fullImage.setImageURI(image)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}
