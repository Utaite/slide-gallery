package com.utaite.slidegallery.list

import android.graphics.Bitmap
import android.net.Uri


data class ListModel(
        val image: Uri,
        val thumbnail: Bitmap,
        var isChoice: Boolean = false,
        var count: Int = 0
)
