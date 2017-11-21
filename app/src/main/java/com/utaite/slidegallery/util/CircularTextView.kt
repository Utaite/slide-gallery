package com.utaite.slidegallery.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.TextView


class CircularTextView : TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var strokeWidth: Float = 0.toFloat()
    private var strokeCol: Int = 0
    private var solidCol: Int = 0

    override fun draw(canvas: Canvas) {
        val circlePaint = Paint()
        circlePaint.color = solidCol
        circlePaint.flags = Paint.ANTI_ALIAS_FLAG

        val strokePaint = Paint()
        strokePaint.color = strokeCol
        strokePaint.flags = Paint.ANTI_ALIAS_FLAG

        val diameter = when (this.height > this.width) {
            false -> this.width
            true -> this.height
        }
        val radius = diameter / 2

        this.height = diameter
        this.width = diameter

        canvas.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), radius.toFloat(), strokePaint)
        canvas.drawCircle((diameter / 2).toFloat(), (diameter / 2).toFloat(), (radius - strokeWidth), circlePaint)

        super.draw(canvas)
    }

    fun setStrokeWidth(dp: Int) {
        val scale = context.resources.displayMetrics.density
        strokeWidth = (dp * scale).toInt().toFloat()

    }

    fun setStrokeCol(strokeCol: Int) {
        this.strokeCol = strokeCol
    }

    fun setSolidCol(solidCol: Int) {
        this.solidCol = solidCol
    }

}
