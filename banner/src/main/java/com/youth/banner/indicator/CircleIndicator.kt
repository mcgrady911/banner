package com.youth.banner.indicator

import android.content.Context
import android.graphics.*
import android.util.AttributeSet

/**
 * 圆形指示器
 * 如果想要大小一样，可以将选中和默认设置成同样大小
 */
class CircleIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseIndicator(context, attrs, defStyleAttr) {
    private var mNormalRadius: Int
    private var mSelectedRadius: Int
    private var maxRadius = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = indicatorConfig.indicatorSize
        if (count <= 1) {
            return
        }
        mNormalRadius = indicatorConfig.normalWidth / 2
        mSelectedRadius = indicatorConfig.selectedWidth / 2
        //考虑当 选中和默认 的大小不一样的情况
        maxRadius = Math.max(mSelectedRadius, mNormalRadius)
        //间距*（总数-1）+选中宽度+默认宽度*（总数-1）
        val width =
            (count - 1) * indicatorConfig.indicatorSpace + indicatorConfig.selectedWidth + indicatorConfig.normalWidth * (count - 1)
        setMeasuredDimension(width, Math.max(indicatorConfig.normalWidth, indicatorConfig.selectedWidth))
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
            val radius = if (indicatorConfig.currentPosition == i) mSelectedRadius else mNormalRadius
            canvas.drawCircle(left + radius, maxRadius.toFloat(), radius.toFloat(), mPaint)
            left += (indicatorWidth + indicatorConfig.indicatorSpace).toFloat()
        }
        //        mPaint.setColor(config.getNormalColor());
//        for (int i = 0; i < count; i++) {
//            canvas.drawCircle(left + maxRadius, maxRadius, mNormalRadius, mPaint);
//            left += config.getNormalWidth() + config.getIndicatorSpace();
//        }
//        mPaint.setColor(config.getSelectedColor());
//        left = maxRadius + (config.getNormalWidth() + config.getIndicatorSpace()) * config.getCurrentPosition();
//        canvas.drawCircle(left, maxRadius, mSelectedRadius, mPaint);
    }

    init {
        mNormalRadius = indicatorConfig.normalWidth / 2
        mSelectedRadius = indicatorConfig.selectedWidth / 2
    }
}