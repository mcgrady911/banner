package com.youth.banner.itemdecoration

import com.youth.banner.util.LogUtils
import androidx.annotation.LayoutRes
import android.util.TypedValue
import androidx.annotation.RequiresApi
import android.os.Build
import com.youth.banner.Banner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSmoothScroller
import com.youth.banner.util.ScrollSpeedManger
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.youth.banner.util.BannerLifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.youth.banner.util.BannerUtils
import com.youth.banner.config.BannerConfig
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.holder.IViewHolder
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.R
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.holder.BannerImageHolder
import androidx.annotation.Px
import com.youth.banner.indicator.Indicator
import android.widget.FrameLayout
import com.youth.banner.indicator.BaseIndicator
import androidx.annotation.DrawableRes
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.view.*
import com.youth.banner.Banner.AutoLoopTask
import com.youth.banner.Banner.BannerOnPageChangeCallback
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.youth.banner.transformer.ScaleInTransformer
import com.youth.banner.transformer.MZScaleInTransformer
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.youth.banner.util.BannerLifecycleObserverAdapter
import com.youth.banner.transformer.BasePageTransformer
import com.youth.banner.transformer.RotateYTransformer
import com.youth.banner.transformer.AlphaPageTransformer
import com.youth.banner.transformer.DepthPageTransformer
import com.youth.banner.transformer.ZoomOutPageTransformer
import com.youth.banner.transformer.RotateUpPageTransformer
import com.youth.banner.transformer.RotateDownPageTransformer
import java.lang.IllegalStateException

class MarginDecoration(@param:Px private val mMarginPx: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val linearLayoutManager = requireLinearLayoutManager(parent)
        if (linearLayoutManager.orientation == LinearLayoutManager.VERTICAL) {
            outRect.top = mMarginPx
            outRect.bottom = mMarginPx
        } else {
            outRect.left = mMarginPx
            outRect.right = mMarginPx
        }
    }

    private fun requireLinearLayoutManager(parent: RecyclerView): LinearLayoutManager {
        val layoutManager = parent.layoutManager
        if (layoutManager is LinearLayoutManager) {
            return layoutManager
        }
        throw IllegalStateException("The layoutManager must be LinearLayoutManager")
    }
}