package com.youth.banner.config

import androidx.annotation.ColorInt
import androidx.annotation.IntDef


class IndicatorConfig {
    var indicatorSize = 0
    var currentPosition = 0
    var gravity = Direction.CENTER
    var indicatorSpace = BannerConfig.INDICATOR_SPACE
    var normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH
    var selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH

    @ColorInt
    var normalColor = BannerConfig.INDICATOR_NORMAL_COLOR

    @ColorInt
    var selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR
    var radius = BannerConfig.INDICATOR_RADIUS
    var height = BannerConfig.INDICATOR_HEIGHT
    var margins: Margins? = null
//        get() {
//            if (field == null) {
//                field = Margins()
//            }
//            return field
//        }

    //是将指示器添加到banner上
    var isAttachToBanner = true

    @IntDef(Direction.LEFT, Direction.CENTER, Direction.RIGHT)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation
    class Direction {
        companion object {
            const val LEFT = 0
            const val CENTER = 1
            const val RIGHT = 2
        }
    }

    class Margins(
        var leftMargin: Int,
        var topMargin: Int,
        var rightMargin: Int,
        var bottomMargin: Int
    ) {
        @JvmOverloads
        constructor(marginSize: Int = BannerConfig.INDICATOR_MARGIN) : this(
            marginSize,
            marginSize,
            marginSize,
            marginSize
        ) {
        }
    }

//    fun getMargins(): Margins? {
//        if (margins == null) {
//            setMargins(Margins())
//        }
//        return margins
//    }
//
//    fun setMargins(margins: Margins?): IndicatorConfig {
//        this.margins = margins
//        return this
//    }

    fun setIndicatorSize(indicatorSize: Int): IndicatorConfig {
        this.indicatorSize = indicatorSize
        return this
    }

    fun setNormalColor(normalColor: Int): IndicatorConfig {
        this.normalColor = normalColor
        return this
    }

    fun setSelectedColor(selectedColor: Int): IndicatorConfig {
        this.selectedColor = selectedColor
        return this
    }

    fun setIndicatorSpace(indicatorSpace: Int): IndicatorConfig {
        this.indicatorSpace = indicatorSpace
        return this
    }

    fun setCurrentPosition(currentPosition: Int): IndicatorConfig {
        this.currentPosition = currentPosition
        return this
    }

    fun setNormalWidth(normalWidth: Int): IndicatorConfig {
        this.normalWidth = normalWidth
        return this
    }

    fun setSelectedWidth(selectedWidth: Int): IndicatorConfig {
        this.selectedWidth = selectedWidth
        return this
    }

    fun setGravity(@Direction gravity: Int): IndicatorConfig {
        this.gravity = gravity
        return this
    }

    fun setAttachToBanner(attachToBanner: Boolean): IndicatorConfig {
        isAttachToBanner = attachToBanner
        return this
    }

    fun setRadius(radius: Int): IndicatorConfig {
        this.radius = radius
        return this
    }

    fun setHeight(height: Int): IndicatorConfig {
        this.height = height
        return this
    }
}