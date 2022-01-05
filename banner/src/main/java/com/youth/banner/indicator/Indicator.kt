package com.youth.banner.indicator

import android.view.View
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.listener.OnPageChangeListener

interface Indicator : OnPageChangeListener {
    val indicatorView: View
    val indicatorConfig: IndicatorConfig
    fun onPageChanged(count: Int, currentPosition: Int)
}