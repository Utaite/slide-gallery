package com.utaite.slidegallery.slide

import android.content.Context
import android.net.Uri
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.utaite.slidegallery.R
import kotlinx.android.synthetic.main.activity_slide_view.view.*


class SlideAdapter(private val context: Context,
                   private val dataSet: List<Uri>) : PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any =
            LayoutInflater.from(context).inflate(R.layout.activity_slide_view, container, false).also {
                val model = dataSet[position]
                it.slideViewImage.setImageURI(model)
                container.addView(it)
            }

    override fun getCount() =
            dataSet.size

    override fun getItemPosition(obj: Any?) =
            PagerAdapter.POSITION_NONE

    override fun isViewFromObject(view: View, obj: Any) =
            view == obj

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) =
            container.removeView(obj as View)

}
