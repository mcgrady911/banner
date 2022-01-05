package com.youth.banner.indicator

import android.content.Context
import com.youth.banner.config.IndicatorConfig
import android.widget.FrameLayout
import android.graphics.*
import android.util.AttributeSet
import android.view.*

open class BaseIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Indicator {
    override lateinit var indicatorConfig: IndicatorConfig
        protected set
    @kotlin.jvm.JvmField
    protected var mPaint: Paint
    protected var offset = 0f
    override val indicatorView: View
        get() {
            if (indicatorConfig.isAttachToBanner) {
                val layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                )
                when (indicatorConfig.gravity) {
                    IndicatorConfig.Direction.LEFT -> layoutParams.gravity =
                        Gravity.BOTTOM or Gravity.START
                    IndicatorConfig.Direction.CENTER -> layoutParams.gravity =
                        Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    IndicatorConfig.Direction.RIGHT -> layoutParams.gravity =
                        Gravity.BOTTOM or Gravity.END
                }

                indicatorConfig.margins?.run {
                    layoutParams.leftMargin = leftMargin
                    layoutParams.rightMargin = rightMargin
                    layoutParams.topMargin = topMargin
                    layoutParams.bottomMargin = bottomMargin
                }
                setLayoutParams(layoutParams)
            }
            return this
        }

    override fun onPageChanged(count: Int, currentPosition: Int) {
        indicatorConfig.indicatorSize = count
        indicatorConfig.currentPosition = currentPosition
        requestLayout()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        offset = positionOffset
        invalidate()
    }

    override fun onPageSelected(position: Int) {
        indicatorConfig.currentPosition = position
        invalidate()
    }

    override fun onPageScrollStateChanged(state: Int) {}

    init {
        indicatorConfig = IndicatorConfig()
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.TRANSPARENT
        mPaint.color = indicatorConfig.normalColor
    }
}