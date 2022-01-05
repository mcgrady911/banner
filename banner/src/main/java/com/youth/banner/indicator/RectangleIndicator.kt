package com.youth.banner.indicator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

/**
 * 矩形（条形）指示器
 * 1、可以设置选中和默认的宽度、指示器的圆角
 * 2、如果需要正方形将圆角设置为0，可将宽度和高度设置为一样
 * 3、如果不想选中时变长，可将选中的宽度和默认宽度设置为一样
 */
class RectangleIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseIndicator(context, attrs, defStyleAttr) {
    var rectF: RectF = RectF()
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = indicatorConfig.indicatorSize
        if (count <= 1) {
            return
        }
        //间距*（总数-1）+默认宽度*（总数-1）+选中宽度
        val space = indicatorConfig.indicatorSpace * (count - 1)
        val normal = indicatorConfig.normalWidth * (count - 1)
        setMeasuredDimension(space + normal + indicatorConfig.selectedWidth, indicatorConfig.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = indicatorConfig.indicatorSize
        if (count <= 1) {
            return
        }
        var left = 0f
        for (i in 0 until count) {
            mPaint.color =
                if (indicatorConfig.currentPosition == i) indicatorConfig.selectedColor else indicatorConfig.normalColor
            val indicatorWidth =
                if (indicatorConfig.currentPosition == i) indicatorConfig.selectedWidth else indicatorConfig.normalWidth
            rectF[left, 0f, left + indicatorWidth] = indicatorConfig.height.toFloat()
            left += (indicatorWidth + indicatorConfig.indicatorSpace).toFloat()
            canvas.drawRoundRect(rectF, indicatorConfig.radius.toFloat(), indicatorConfig.radius.toFloat(), mPaint)
        }
    }

}