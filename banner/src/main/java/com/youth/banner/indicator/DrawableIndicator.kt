package com.youth.banner.indicator

import android.content.Context
import com.youth.banner.R
import androidx.annotation.DrawableRes
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet

/**
 * Drawable指示器
 */
class DrawableIndicator : BaseIndicator {
    private var normalBitmap: Bitmap? = null
    private var selectedBitmap: Bitmap? = null

    /**
     * 实例化Drawable指示器 ，也可以通过自定义属性设置
     * @param context
     * @param normalResId
     * @param selectedResId
     */
    constructor(
        context: Context?,
        @DrawableRes normalResId: Int,
        @DrawableRes selectedResId: Int
    ) : super(context) {
        normalBitmap = BitmapFactory.decodeResource(resources, normalResId)
        selectedBitmap = BitmapFactory.decodeResource(resources, selectedResId)
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.DrawableIndicator)
        a?.let {
            val normal =
                it.getDrawable(R.styleable.DrawableIndicator_normal_drawable) as BitmapDrawable?
            val selected =
                it.getDrawable(R.styleable.DrawableIndicator_selected_drawable) as BitmapDrawable?
            normalBitmap = normal?.bitmap
            selectedBitmap = selected?.bitmap
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = indicatorConfig.indicatorSize
        if (count <= 1) {
            return
        }
        setMeasuredDimension(
            (selectedBitmap?.width ?: 0) * (count - 1)
                    + (selectedBitmap?.width ?: 0)
                    + indicatorConfig.indicatorSpace * (count - 1),
            (normalBitmap?.height ?: 0).coerceAtLeast((selectedBitmap?.height ?: 0))
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = indicatorConfig.indicatorSize
        if (count <= 1 || normalBitmap == null || selectedBitmap == null) {
            return
        }
        var left = 0f
        for (i in 0 until count) {
            (if (indicatorConfig.currentPosition == i) selectedBitmap else normalBitmap)?.let {
                canvas.drawBitmap(
                    it,
                    left,
                    0f,
                    mPaint
                )

                left += ((normalBitmap?.width ?: 0) + indicatorConfig.indicatorSpace).toFloat()
            }
        }
    }
}