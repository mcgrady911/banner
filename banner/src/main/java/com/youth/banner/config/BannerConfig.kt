package com.youth.banner.config

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
import com.youth.banner.transformer.ZoomOutPageTransformer
import com.youth.banner.transformer.RotateUpPageTransformer
import com.youth.banner.transformer.RotateDownPageTransformer

/**
 * 不忘初心
 *
 * ┌───┐   ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┬───┐ ┌───┬───┬───┐  ┌┐    ┌┐    ┌┐
 * │Esc│   │ F1│ F2│ F3│ F4│ │ F5│ F6│ F7│ F8│ │ F9│F10│F11│F12│ │P/S│S L│P/B│  └┘    └┘    └┘
 * └───┘   └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┴───┘ └───┴───┴───┘
 * ┌───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───┬───────┐ ┌───┬───┬───┐ ┌───┬───┬───┬───┐
 * │~ `│! 1│@ 2│# 3│$ 4│% 5│^ 6│& 7│* 8│( 9│) 0│_ -│+ =│ BacSp │ │Ins│Hom│PUp│ │N L│ / │ * │ - │
 * ├───┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─────┤ ├───┼───┼───┤ ├───┼───┼───┼───┤
 * │ Tab │ Q │ W │ E │ R │ T │ Y │ U │ I │ O │ P │{ [│} ]│ | \ │ │Del│End│PDn│ │ 7 │ 8 │ 9 │   │
 * ├─────┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴┬──┴─────┤ └───┴───┴───┘ ├───┼───┼───┤ + │
 * │ Caps │ A │ S │ D │ F │ G │ H │ J │ K │ L │: ;│" '│ Enter  │               │ 4 │ 5 │ 6 │   │
 * ├──────┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴─┬─┴────────┤     ┌───┐     ├───┼───┼───┼───┤
 * │ Shift  │ Z │ X │ C │ V │ B │ N │ M │< ,│> .│? /│  Shift   │     │ ↑ │     │ 1 │ 2 │ 3 │   │
 * ├─────┬──┴─┬─┴──┬┴───┴───┴───┴───┴───┴──┬┴───┼───┴┬────┬────┤ ┌───┼───┼───┐ ├───┴───┼───┤ E││
 * │ Ctrl│    │Alt │         Space         │ Alt│    │    │Ctrl│ │ ← │ ↓ │ → │ │   0   │ . │←─┘│
 * └─────┴────┴────┴───────────────────────┴────┴────┴────┴────┘ └───┴───┴───┘ └───────┴───┴───┘
 *
 * @author youth5201314/spring
 * @date 2020/1/24
 * banner 配置参数
 */
object BannerConfig {
    const val IS_AUTO_LOOP = true
    const val IS_INFINITE_LOOP = true
    const val LOOP_TIME = 3000
    const val SCROLL_TIME = 600
    const val INCREASE_COUNT = 2
    const val INDICATOR_NORMAL_COLOR = -0x77000001
    const val INDICATOR_SELECTED_COLOR = -0x78000000
    val INDICATOR_NORMAL_WIDTH = BannerUtils.dp2px(5f)
    val INDICATOR_SELECTED_WIDTH = BannerUtils.dp2px(7f)
    val INDICATOR_SPACE = BannerUtils.dp2px(5f)
    @kotlin.jvm.JvmField
    val INDICATOR_MARGIN = BannerUtils.dp2px(5f)
    val INDICATOR_HEIGHT = BannerUtils.dp2px(3f)
    val INDICATOR_RADIUS = BannerUtils.dp2px(3f)
}