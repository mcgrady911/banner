package com.youth.banner.util

import android.content.res.Resources
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
import com.youth.banner.Banner.AutoLoopTask
import com.youth.banner.Banner.BannerOnPageChangeCallback
import android.view.*
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


internal fun ViewGroup.inflate(@LayoutRes layoutId: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(this.context).inflate(layoutId, this, attachToRoot).apply {
        val params = layoutParams
        if (params.height != -1 || params.width != -1) {
            params.height = -1
            params.width = -1
            layoutParams = params
        }
    }

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
internal fun View.setOutlineRound(radius: Float) {
    outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(0, 0, view.width, view.height, radius)
        }
    }
    clipToOutline = true
}

object BannerUtils {
    /**
     * 获取真正的位置
     *
     * @param isIncrease 首尾是否有增加
     * @param position  当前位置
     * @param realCount 真实数量
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun getRealPosition(isIncrease: Boolean, position: Int, realCount: Int): Int = when {
        !isIncrease -> position
        position == 0 -> realCount - 1
        position == realCount + 1 -> 0
        else -> position - 1
    }

    /**
     * 将布局文件转成view，这里为了适配viewpager2中高宽必须为match_parent
     *
     * @param parent
     * @param layoutId
     * @return
     */
    @kotlin.jvm.JvmStatic
    fun getView(parent: ViewGroup, @LayoutRes layoutId: Int): View {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        val params = view.layoutParams
        //这里判断高度和宽带是否都是match_parent
        if (params.height != -1 || params.width != -1) {
            params.height = -1
            params.width = -1
            view.layoutParams = params
        }
        return view
    }

    @kotlin.jvm.JvmStatic
    fun dp2px(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            Resources.getSystem().displayMetrics
        ).toInt()
    }

    /**
     * 设置view圆角
     *
     * @param radius
     * @return
     */
    @kotlin.jvm.JvmStatic
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setBannerRound(view: View, radius: Float) {
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, radius)
            }
        }
        view.clipToOutline = true
    }
}