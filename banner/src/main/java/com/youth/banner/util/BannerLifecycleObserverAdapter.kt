package com.youth.banner.util

import com.youth.banner.util.LogUtils
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import android.view.LayoutInflater
import android.util.TypedValue
import androidx.annotation.RequiresApi
import android.os.Build
import android.view.ViewOutlineProvider
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
import android.view.Gravity
import com.youth.banner.indicator.BaseIndicator
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import android.graphics.BitmapFactory
import android.content.res.TypedArray
import android.graphics.drawable.BitmapDrawable
import android.graphics.RectF
import com.youth.banner.Banner.AutoLoopTask
import com.youth.banner.Banner.BannerOnPageChangeCallback
import android.view.ViewConfiguration
import android.graphics.PorterDuffXfermode
import android.graphics.PorterDuff
import android.view.MotionEvent
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
import android.view.ViewParent
import androidx.lifecycle.Lifecycle
import com.youth.banner.transformer.ZoomOutPageTransformer
import com.youth.banner.transformer.RotateUpPageTransformer
import com.youth.banner.transformer.RotateDownPageTransformer

class BannerLifecycleObserverAdapter(
    private val mLifecycleOwner: LifecycleOwner,
    private val mObserver: BannerLifecycleObserver
) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        LogUtils.i("onStart")
        mObserver.onStart(mLifecycleOwner)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        LogUtils.i("onStop")
        mObserver.onStop(mLifecycleOwner)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        LogUtils.i("onDestroy")
        mObserver.onDestroy(mLifecycleOwner)
    }
}