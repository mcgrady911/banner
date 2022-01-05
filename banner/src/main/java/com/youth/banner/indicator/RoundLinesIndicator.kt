package com.youth.banner.indicator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

class RoundLinesIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseIndicator(context, attrs, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = indicatorConfig.indicatorSize
        if (count <= 1) return
        setMeasuredDimension((indicatorConfig.selectedWidth * count), indicatorConfig.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = indicatorConfig.indicatorSize
        if (count <= 1) return
        mPaint.color = indicatorConfig.normalColor
        val oval = RectF(0f, 0f, canvas.width.toFloat(), indicatorConfig.height.toFloat())
        canvas.drawRoundRect(oval, indicatorConfig.radius.toFloat(), indicatorConfig.radius.toFloat(), mPaint)
        mPaint.color = indicatorConfig.selectedColor
        val left = indicatorConfig.currentPosition * indicatorConfig.selectedWidth
        val rectF = RectF(left.toFloat(), 0f, left.toFloat() + indicatorConfig.selectedWidth.toFloat(), indicatorConfig.height.toFloat())
        canvas.drawRoundRect(rectF, indicatorConfig.radius.toFloat(), indicatorConfig.radius.toFloat(), mPaint)
    }

    init {
        mPaint.style = Paint.Style.FILL
    }
}