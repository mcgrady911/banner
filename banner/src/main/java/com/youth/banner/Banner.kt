package com.youth.banner

import android.content.Context
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.LifecycleOwner
import com.youth.banner.config.BannerConfig
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.adapter.BannerAdapter
import com.youth.banner.indicator.Indicator
import android.widget.FrameLayout
import android.graphics.*
import android.view.ViewConfiguration
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.youth.banner.transformer.ScaleInTransformer
import com.youth.banner.transformer.MZScaleInTransformer
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.youth.banner.listener.OnPageChangeListener
import com.youth.banner.util.*
import java.lang.NullPointerException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.ref.WeakReference

class Banner<T, BA : BannerAdapter<T, out RecyclerView.ViewHolder?>?> @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), BannerLifecycleObserver {
    var viewPager2: ViewPager2? = null
        private set
    private var loopTask: AutoLoopTask? = null
    private var onPageChangeListener: OnPageChangeListener? = null
    var adapter: BA? = null
        private set

    var indicator: Indicator? = null
        private set
    private var compositePageTransformer: CompositePageTransformer? = null
    private var pageChangeCallback: BannerOnPageChangeCallback? = null

    // 是否允许无限轮播（即首尾直接切换）
    var isInfiniteLoop = BannerConfig.IS_INFINITE_LOOP
        private set

    // 是否自动轮播
    private var mIsAutoLoop = BannerConfig.IS_AUTO_LOOP

    // 轮播切换间隔时间
    private var mLoopTime = BannerConfig.LOOP_TIME.toLong()

    // 轮播切换时间
    var scrollTime = BannerConfig.SCROLL_TIME
        private set

    // 轮播开始位置
    var startPosition = 1
        private set

    // banner圆角半径，默认没有圆角
    private var mBannerRadius = 0f

    // banner圆角方向，如果一个都不设置，默认四个角全部圆角
    private var mRoundTopLeft = false
    private var mRoundTopRight = false
    private var mRoundBottomLeft = false
    private var mRoundBottomRight = false

    // 指示器相关配置
    private var normalWidth = BannerConfig.INDICATOR_NORMAL_WIDTH
    private var selectedWidth = BannerConfig.INDICATOR_SELECTED_WIDTH
    private var normalColor = BannerConfig.INDICATOR_NORMAL_COLOR
    private var selectedColor = BannerConfig.INDICATOR_SELECTED_COLOR
    private var indicatorGravity: Int = IndicatorConfig.Direction.CENTER
    private var indicatorSpace = 0
    private var indicatorMargin = 0
    private var indicatorMarginLeft = 0
    private var indicatorMarginTop = 0
    private var indicatorMarginRight = 0
    private var indicatorMarginBottom = 0
    private var indicatorHeight = BannerConfig.INDICATOR_HEIGHT
    private var indicatorRadius = BannerConfig.INDICATOR_RADIUS
    private var mOrientation = HORIZONTAL

    // 滑动距离范围
    private var mTouchSlop = 0

    // 记录触摸的位置（主要用于解决事件冲突问题）
    private var mStartX = 0f
    private var mStartY = 0f

    // 记录viewpager2是否被拖动
    private var mIsViewPager2Drag = false

    // 是否要拦截事件
    private var isIntercept = true

    //绘制圆角视图
    private lateinit var mRoundPaint: Paint
    private lateinit var mImagePaint: Paint

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(HORIZONTAL, VERTICAL)
    annotation class Orientation

    private fun initialize(context: Context) {
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop / 2
        compositePageTransformer = CompositePageTransformer()
        pageChangeCallback = BannerOnPageChangeCallback()
        loopTask = AutoLoopTask(this)
        viewPager2 = ViewPager2(context).apply {
            layoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            offscreenPageLimit = 2
            pageChangeCallback?.let { registerOnPageChangeCallback(it) }
            setPageTransformer(compositePageTransformer)
        }
        ScrollSpeedManger.reflectLayoutManager(this)
        addView(viewPager2)
        mRoundPaint = Paint().apply {
            color = Color.WHITE
            isAntiAlias = true
            style = Paint.Style.FILL
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }
        mImagePaint = Paint().apply {
            xfermode = null
        }
    }

    private fun initTypedArray(attrs: AttributeSet, defStyle: Int = 0) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.Banner)
        mBannerRadius = a.getDimensionPixelSize(R.styleable.Banner_banner_radius, 0).toFloat()
        mLoopTime =
            a.getInt(R.styleable.Banner_banner_loop_time, BannerConfig.LOOP_TIME).toLong()
        mIsAutoLoop =
            a.getBoolean(R.styleable.Banner_banner_auto_loop, BannerConfig.IS_AUTO_LOOP)
        isInfiniteLoop =
            a.getBoolean(R.styleable.Banner_banner_infinite_loop, BannerConfig.IS_INFINITE_LOOP)
        normalWidth = a.getDimensionPixelSize(
            R.styleable.Banner_banner_indicator_normal_width,
            BannerConfig.INDICATOR_NORMAL_WIDTH
        )
        selectedWidth = a.getDimensionPixelSize(
            R.styleable.Banner_banner_indicator_selected_width,
            BannerConfig.INDICATOR_SELECTED_WIDTH
        )
        normalColor = a.getColor(
            R.styleable.Banner_banner_indicator_normal_color,
            BannerConfig.INDICATOR_NORMAL_COLOR
        )
        selectedColor = a.getColor(
            R.styleable.Banner_banner_indicator_selected_color,
            BannerConfig.INDICATOR_SELECTED_COLOR
        )
        indicatorGravity = a.getInt(
            R.styleable.Banner_banner_indicator_gravity,
            IndicatorConfig.Direction.CENTER
        )
        indicatorSpace = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_space, 0)
        indicatorMargin = a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_margin, 0)
        indicatorMarginLeft =
            a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginLeft, 0)
        indicatorMarginTop =
            a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginTop, 0)
        indicatorMarginRight =
            a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginRight, 0)
        indicatorMarginBottom =
            a.getDimensionPixelSize(R.styleable.Banner_banner_indicator_marginBottom, 0)
        indicatorHeight = a.getDimensionPixelSize(
            R.styleable.Banner_banner_indicator_height,
            BannerConfig.INDICATOR_HEIGHT
        )
        indicatorRadius = a.getDimensionPixelSize(
            R.styleable.Banner_banner_indicator_radius,
            BannerConfig.INDICATOR_RADIUS
        )
        mOrientation = a.getInt(R.styleable.Banner_banner_orientation, HORIZONTAL)
        mRoundTopLeft = a.getBoolean(R.styleable.Banner_banner_round_top_left, false)
        mRoundTopRight = a.getBoolean(R.styleable.Banner_banner_round_top_right, false)
        mRoundBottomLeft = a.getBoolean(R.styleable.Banner_banner_round_bottom_left, false)
        mRoundBottomRight = a.getBoolean(R.styleable.Banner_banner_round_bottom_right, false)
        a.recycle()

        setOrientation(mOrientation)
        setInfiniteLoop()
    }

    private fun initIndicatorAttr() {
        if (indicatorMargin != 0) {
            setIndicatorMargins(IndicatorConfig.Margins(indicatorMargin))
        } else if (indicatorMarginLeft != 0 || indicatorMarginTop != 0 || indicatorMarginRight != 0 || indicatorMarginBottom != 0) {
            setIndicatorMargins(
                IndicatorConfig.Margins(
                    indicatorMarginLeft,
                    indicatorMarginTop,
                    indicatorMarginRight,
                    indicatorMarginBottom
                )
            )
        }
        if (indicatorSpace > 0) {
            setIndicatorSpace(indicatorSpace)
        }
        if (indicatorGravity != IndicatorConfig.Direction.CENTER) {
            setIndicatorGravity(indicatorGravity)
        }
        if (normalWidth > 0) {
            setIndicatorNormalWidth(normalWidth)
        }
        if (selectedWidth > 0) {
            setIndicatorSelectedWidth(selectedWidth)
        }
        if (indicatorHeight > 0) {
            setIndicatorHeight(indicatorHeight)
        }
        if (indicatorRadius > 0) {
            setIndicatorRadius(indicatorRadius)
        }
        setIndicatorNormalColor(normalColor)
        setIndicatorSelectedColor(selectedColor)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (viewPager2?.isUserInputEnabled == false) {
            return super.dispatchTouchEvent(ev)
        }
        val action = ev.actionMasked
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            start()
        } else if (action == MotionEvent.ACTION_DOWN) {
            stop()
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (viewPager2?.isUserInputEnabled == false || !isIntercept) {
            return super.onInterceptTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = event.x
                mStartY = event.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = event.x
                val endY = event.y
                val distanceX = Math.abs(endX - mStartX)
                val distanceY = Math.abs(endY - mStartY)
                mIsViewPager2Drag = if (viewPager2?.orientation == HORIZONTAL) {
                    distanceX > mTouchSlop && distanceX > distanceY
                } else {
                    distanceY > mTouchSlop && distanceY > distanceX
                }
                parent.requestDisallowInterceptTouchEvent(mIsViewPager2Drag)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> parent.requestDisallowInterceptTouchEvent(
                false
            )
        }
        return super.onInterceptTouchEvent(event)
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (mBannerRadius > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas.saveLayer(
                    RectF(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat()),
                    mImagePaint
                )
            } else {
                canvas.saveLayer(
                    RectF(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat()),
                    mImagePaint,
                    Canvas.ALL_SAVE_FLAG
                )
            }
            super.dispatchDraw(canvas)
            //绘制外圆环边框圆环
            //默认四个角都设置
            if (!mRoundTopRight && !mRoundTopLeft && !mRoundBottomRight && !mRoundBottomLeft) {
                drawTopLeft(canvas)
                drawTopRight(canvas)
                drawBottomLeft(canvas)
                drawBottomRight(canvas)
                canvas.restore()
                return
            }
            if (mRoundTopLeft) {
                drawTopLeft(canvas)
            }
            if (mRoundTopRight) {
                drawTopRight(canvas)
            }
            if (mRoundBottomLeft) {
                drawBottomLeft(canvas)
            }
            if (mRoundBottomRight) {
                drawBottomRight(canvas)
            }
            canvas.restore()
        } else {
            super.dispatchDraw(canvas)
        }
    }

    private fun drawTopLeft(canvas: Canvas) {
        val path = Path()
        path.moveTo(0f, mBannerRadius)
        path.lineTo(0f, 0f)
        path.lineTo(mBannerRadius, 0f)
        path.arcTo(RectF(0f, 0f, mBannerRadius * 2, mBannerRadius * 2), -90f, -90f)
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    private fun drawTopRight(canvas: Canvas) {
        val width = width
        val path = Path()
        path.moveTo(width - mBannerRadius, 0f)
        path.lineTo(width.toFloat(), 0f)
        path.lineTo(width.toFloat(), mBannerRadius)
        path.arcTo(
            RectF(width - 2 * mBannerRadius, 0f, width.toFloat(), mBannerRadius * 2),
            0f,
            -90f
        )
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    private fun drawBottomLeft(canvas: Canvas) {
        val height = height
        val path = Path()
        path.moveTo(0f, height - mBannerRadius)
        path.lineTo(0f, height.toFloat())
        path.lineTo(mBannerRadius, height.toFloat())
        path.arcTo(
            RectF(0f, height - 2 * mBannerRadius, mBannerRadius * 2, height.toFloat()),
            90f,
            90f
        )
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    private fun drawBottomRight(canvas: Canvas) {
        val height = height
        val width = width
        val path = Path()
        path.moveTo(width - mBannerRadius, height.toFloat())
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(width.toFloat(), height - mBannerRadius)
        path.arcTo(
            RectF(
                width - 2 * mBannerRadius,
                height - 2 * mBannerRadius,
                width.toFloat(),
                height.toFloat()
            ), 0f, 90f
        )
        path.close()
        canvas.drawPath(path, mRoundPaint)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stop()
    }

    internal inner class BannerOnPageChangeCallback : ViewPager2.OnPageChangeCallback() {
        private var mTempPosition = INVALID_VALUE
        private var isScrolled = false
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
            val realPosition = BannerUtils.getRealPosition(isInfiniteLoop, position, realCount)
            if (realPosition == currentItem - 1) {
                onPageChangeListener?.onPageScrolled(
                    realPosition,
                    positionOffset,
                    positionOffsetPixels
                )
            }
            if (realPosition == currentItem - 1) {
                indicator?.onPageScrolled(realPosition, positionOffset, positionOffsetPixels)
            }
        }

        override fun onPageSelected(position: Int) {
            if (isScrolled) {
                mTempPosition = position
                val realPosition = BannerUtils.getRealPosition(isInfiniteLoop, position, realCount)
                onPageChangeListener?.onPageSelected(realPosition)
                indicator?.onPageSelected(realPosition)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {
            //手势滑动中,代码执行滑动中
            if (state == ViewPager2.SCROLL_STATE_DRAGGING || state == ViewPager2.SCROLL_STATE_SETTLING) {
                isScrolled = true
            } else if (state == ViewPager2.SCROLL_STATE_IDLE) {
                //滑动闲置或滑动结束
                isScrolled = false
                if (mTempPosition != INVALID_VALUE && isInfiniteLoop) {
                    if (mTempPosition == 0) {
                        setCurrentItem(realCount, false)
                    } else if (mTempPosition == itemCount - 1) {
                        setCurrentItem(1, false)
                    }
                }
            }
            onPageChangeListener?.onPageScrollStateChanged(state)
            indicator?.onPageScrollStateChanged(state)
        }
    }

    internal class AutoLoopTask(banner: Banner<*, *>) : Runnable {
        private val reference: WeakReference<Banner<*, *>>
        override fun run() {
            val banner = reference.get()
            if (banner != null && banner.mIsAutoLoop) {
                val count = banner.itemCount
                if (count == 0) {
                    return
                }
                val next = (banner.currentItem + 1) % count
                banner.setCurrentItem(next)
                banner.postDelayed(banner.loopTask, banner.mLoopTime)
            }
        }

        init {
            reference = WeakReference(banner)
        }
    }

    private val adapterDataObserver: AdapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            if (itemCount <= 1) {
                stop()
            } else {
                start()
            }
            setIndicatorPageChange()
        }
    }

    private fun initIndicator() {
        if (indicator == null || adapter == null) {
            return
        }

        indicator?.let {
            if (it.indicatorConfig.isAttachToBanner) {
                removeIndicator()
                addView(it.indicatorView)
            }
        }

        initIndicatorAttr()
        setIndicatorPageChange()
    }

    private fun setInfiniteLoop() {
        // 当不支持无限循环时，要关闭自动轮播
        if (!isInfiniteLoop) {
            isAutoLoop(false)
        }
        setStartPosition(if (isInfiniteLoop) startPosition else 0)
    }

    private fun setRecyclerViewPadding(itemPadding: Int) {
        setRecyclerViewPadding(itemPadding, itemPadding)
    }

    private fun setRecyclerViewPadding(leftItemPadding: Int, rightItemPadding: Int) {
        viewPager2?.run {
            val recyclerView = getChildAt(0) as RecyclerView

            recyclerView.setPadding(
                if (orientation == ViewPager2.ORIENTATION_VERTICAL) paddingLeft else leftItemPadding,
                if (orientation == ViewPager2.ORIENTATION_VERTICAL) leftItemPadding else paddingTop,
                if (orientation == ViewPager2.ORIENTATION_VERTICAL) paddingRight else rightItemPadding,
                if (orientation == ViewPager2.ORIENTATION_VERTICAL) rightItemPadding else paddingBottom
            )

            recyclerView.clipToPadding = false
        }
    }

    /**
     * **********************************************************************
     * ------------------------ 对外公开API ---------------------------------*
     * **********************************************************************
     */
    val currentItem: Int
        get() = viewPager2?.currentItem ?: 0
    val itemCount: Int
        get() = adapter?.itemCount ?: 0
    val indicatorConfig: IndicatorConfig?
        get() = indicator?.indicatorConfig

    /**
     * 返回banner真实总数
     */
    val realCount: Int
        get() = adapter?.realCount ?: 0
    //-----------------------------------------------------------------------------------------
    /**
     * 是否要拦截事件
     * @param intercept
     * @return
     */
    fun setIntercept(intercept: Boolean): Banner<*, *> {
        isIntercept = intercept
        return this
    }

    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @return
     */
    fun setCurrentItem(position: Int): Banner<*, *> {
        return setCurrentItem(position, true)
    }

    /**
     * 跳转到指定位置（最好在设置了数据后在调用，不然没有意义）
     * @param position
     * @param smoothScroll
     * @return
     */
    fun setCurrentItem(position: Int, smoothScroll: Boolean): Banner<*, *> {
        viewPager2?.setCurrentItem(position, smoothScroll)
        return this
    }

    fun setIndicatorPageChange(): Banner<*, *> {
        indicator?.let {
            val realPosition = BannerUtils.getRealPosition(isInfiniteLoop, currentItem, realCount)
            it.onPageChanged(realCount, realPosition)
        }
        return this
    }

    fun removeIndicator(): Banner<*, *> {
        removeView(indicator?.indicatorView)
        return this
    }

    /**
     * 设置开始的位置 (需要在setAdapter或者setDatas之前调用才有效哦)
     */
    fun setStartPosition(mStartPosition: Int): Banner<*, *> {
        startPosition = mStartPosition
        return this
    }

    /**
     * 禁止手动滑动
     *
     * @param enabled true 允许，false 禁止
     */
    fun setUserInputEnabled(enabled: Boolean): Banner<*, *> {
        viewPager2?.isUserInputEnabled = enabled
        return this
    }

    /**
     * 添加PageTransformer，可以组合效果
     * [ViewPager2.PageTransformer]
     * 如果找不到请导入implementation "androidx.viewpager2:viewpager2:1.0.0"
     */
    fun addPageTransformer(transformer: ViewPager2.PageTransformer?): Banner<*, *> {
        transformer?.let {
            compositePageTransformer?.addTransformer(it)
        }
        return this
    }

    /**
     * 设置PageTransformer，和addPageTransformer不同，这个只支持一种transformer
     */
    fun setPageTransformer(transformer: ViewPager2.PageTransformer?): Banner<*, *> {
        viewPager2?.setPageTransformer(transformer)
        return this
    }

    fun removeTransformer(transformer: ViewPager2.PageTransformer): Banner<*, *> {
        compositePageTransformer?.removeTransformer(transformer)
        return this
    }

    /**
     * 添加 ItemDecoration
     */
    fun addItemDecoration(decor: ItemDecoration): Banner<*, *> {
        viewPager2?.addItemDecoration(decor)
        return this
    }

    fun addItemDecoration(decor: ItemDecoration, index: Int): Banner<*, *> {
        viewPager2?.addItemDecoration(decor, index)
        return this
    }

    /**
     * 是否允许自动轮播
     *
     * @param isAutoLoop ture 允许，false 不允许
     */
    fun isAutoLoop(isAutoLoop: Boolean): Banner<*, *> {
        mIsAutoLoop = isAutoLoop
        return this
    }

    /**
     * 设置轮播间隔时间
     *
     * @param loopTime 时间（毫秒）
     */
    fun setLoopTime(loopTime: Long): Banner<*, *> {
        mLoopTime = loopTime
        return this
    }

    /**
     * 设置轮播滑动过程的时间
     */
    fun setScrollTime(scrollTime: Int): Banner<*, *> {
        this.scrollTime = scrollTime
        return this
    }

    /**
     * 开始轮播
     */
    fun start(): Banner<*, *> {
        if (mIsAutoLoop) {
            stop()
            postDelayed(loopTask, mLoopTime)
        }
        return this
    }

    /**
     * 停止轮播
     */
    fun stop(): Banner<*, *> {
        if (mIsAutoLoop) {
            removeCallbacks(loopTask)
        }
        return this
    }

    /**
     * 移除一些引用
     */
    fun destroy() {
        pageChangeCallback?.let {
            viewPager2?.unregisterOnPageChangeCallback(it)
        }
        pageChangeCallback = null

        stop()
    }

    /**
     * 设置banner的适配器
     */
    fun setAdapter(adapter: BA?): Banner<*, *> {
        if (adapter == null) {
            throw NullPointerException(context.getString(R.string.banner_adapter_null_error))
        }

        this.adapter = adapter
        if (!isInfiniteLoop) {
            adapter.setIncreaseCount(0)
        }
        adapter.registerAdapterDataObserver(adapterDataObserver)

        viewPager2?.adapter = adapter
        setCurrentItem(startPosition, false)
        initIndicator()
        return this
    }

    /**
     * 设置banner的适配器
     * @param adapter
     * @param isInfiniteLoop 是否支持无限循环
     * @return
     */
    fun setAdapter(adapter: BA, isInfiniteLoop: Boolean): Banner<*, *> {
        this.isInfiniteLoop = isInfiniteLoop
        setInfiniteLoop()
        setAdapter(adapter)
        return this
    }

    /**
     * 重新设置banner数据，当然你也可以在你adapter中自己操作数据,不要过于局限在这个方法，举一反三哈
     *
     * @param datas 数据集合，当传null或者datas没有数据时，banner会变成空白的，请做好占位UI处理
     */
    fun setDatas(datas: MutableList<T>): Banner<*, *> {
        adapter?.let {
            it.setList(datas)
            setCurrentItem(startPosition, false)
            setIndicatorPageChange()
            start()
        }
        return this
    }

    /**
     * 设置banner轮播方向
     *
     * @param orientation [Orientation]
     */
    fun setOrientation(@Orientation orientation: Int): Banner<*, *> {
        viewPager2?.orientation = orientation
        return this
    }

    /**
     * 改变最小滑动距离
     */
    fun setTouchSlop(mTouchSlop: Int): Banner<*, *> {
        this.mTouchSlop = mTouchSlop
        return this
    }

    /**
     * 设置点击事件
     */
    fun setOnBannerListener(listener: OnBannerListener<T>?): Banner<*, *> {
        adapter?.setOnBannerListener(listener)
        return this
    }

    /**
     * 添加viewpager切换事件
     *
     *
     * 在viewpager2中切换事件[ViewPager2.OnPageChangeCallback]是一个抽象类，
     * 为了方便使用习惯这里用的是和viewpager一样的[ViewPager.OnPageChangeListener]接口
     *
     */
    fun addOnPageChangeListener(pageListener: OnPageChangeListener?): Banner<*, *> {
        onPageChangeListener = pageListener
        return this
    }

    /**
     * 设置banner圆角
     *
     *
     * 默认没有圆角，需要取消圆角把半径设置为0即可
     *
     * @param radius 圆角半径
     */
    fun setBannerRound(radius: Float): Banner<*, *> {
        mBannerRadius = radius
        return this
    }

    /**
     * 设置banner圆角(第二种方式，和上面的方法不要同时使用)，只支持5.0以上
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setBannerRound2(radius: Float): Banner<*, *> {
//        BannerUtils.setBannerRound(this, radius)
        setOutlineRound(radius)
        return this
    }

    /**
     * 为banner添加画廊效果
     *
     * @param itemWidth  item左右展示的宽度,单位dp
     * @param pageMargin 页面间距,单位dp
     */
    fun setBannerGalleryEffect(itemWidth: Int, pageMargin: Int): Banner<*, *> {
        return setBannerGalleryEffect(itemWidth, pageMargin, .85f)
    }

    /**
     * 为banner添加画廊效果
     *
     * @param leftItemWidth  item左展示的宽度,单位dp
     * @param rightItemWidth item右展示的宽度,单位dp
     * @param pageMargin     页面间距,单位dp
     */
    fun setBannerGalleryEffect(
        leftItemWidth: Int,
        rightItemWidth: Int,
        pageMargin: Int
    ): Banner<*, *> {
        return setBannerGalleryEffect(leftItemWidth, rightItemWidth, pageMargin, .85f)
    }

    /**
     * 为banner添加画廊效果
     *
     * @param itemWidth  item左右展示的宽度,单位dp
     * @param pageMargin 页面间距,单位dp
     * @param scale      缩放[0-1],1代表不缩放
     */
    fun setBannerGalleryEffect(itemWidth: Int, pageMargin: Int, scale: Float): Banner<*, *> {
        return setBannerGalleryEffect(itemWidth, itemWidth, pageMargin, scale)
    }

    /**
     * 为banner添加画廊效果
     *
     * @param leftItemWidth  item左展示的宽度,单位dp
     * @param rightItemWidth item右展示的宽度,单位dp
     * @param pageMargin     页面间距,单位dp
     * @param scale          缩放[0-1],1代表不缩放
     */
    fun setBannerGalleryEffect(
        leftItemWidth: Int,
        rightItemWidth: Int,
        pageMargin: Int,
        scale: Float
    ): Banner<*, *> {
        if (pageMargin > 0) {
            addPageTransformer(MarginPageTransformer(BannerUtils.dp2px(pageMargin.toFloat())))
        }
        if (scale < 1 && scale > 0) {
            addPageTransformer(ScaleInTransformer(scale))
        }
        setRecyclerViewPadding(
            if (leftItemWidth > 0) BannerUtils.dp2px((leftItemWidth + pageMargin).toFloat()) else 0,
            if (rightItemWidth > 0) BannerUtils.dp2px((rightItemWidth + pageMargin).toFloat()) else 0
        )
        return this
    }

    /**
     * 为banner添加魅族效果
     *
     * @param itemWidth item左右展示的宽度,单位dp
     */
    fun setBannerGalleryMZ(itemWidth: Int): Banner<*, *> {
        return setBannerGalleryMZ(itemWidth, .88f)
    }

    /**
     * 为banner添加魅族效果
     *
     * @param itemWidth item左右展示的宽度,单位dp
     * @param scale     缩放[0-1],1代表不缩放
     */
    fun setBannerGalleryMZ(itemWidth: Int, scale: Float): Banner<*, *> {
        if (scale < 1 && scale > 0) {
            addPageTransformer(MZScaleInTransformer(scale))
        }
        setRecyclerViewPadding(BannerUtils.dp2px(itemWidth.toFloat()))
        return this
    }
    /**
     * **********************************************************************
     * ------------------------ 指示器相关设置 --------------------------------*
     * **********************************************************************
     */
    /**
     * 设置轮播指示器(显示在banner上)
     */
    fun setIndicator(indicator: Indicator): Banner<*, *> {
        return setIndicator(indicator, true)
    }

    /**
     * 设置轮播指示器(如果你的指示器写在布局文件中，attachToBanner传false)
     *
     * @param attachToBanner 是否将指示器添加到banner中，false 代表你可以将指示器通过布局放在任何位置
     * 注意：设置为false后，内置的 setIndicatorGravity()和setIndicatorMargins() 方法将失效。
     * 想改变可以自己调用系统提供的属性在布局文件中进行设置。具体可以参照demo
     */
    fun setIndicator(indicator: Indicator, attachToBanner: Boolean): Banner<*, *> {
        removeIndicator()
        indicator.indicatorConfig.isAttachToBanner = attachToBanner
        this.indicator = indicator
        initIndicator()
        return this
    }

    fun setIndicatorSelectedColor(@ColorInt color: Int): Banner<*, *> {
        indicatorConfig?.selectedColor = color
        return this
    }

    fun setIndicatorSelectedColorRes(@ColorRes color: Int): Banner<*, *> {
        setIndicatorSelectedColor(ContextCompat.getColor(context, color))
        return this
    }

    fun setIndicatorNormalColor(@ColorInt color: Int): Banner<*, *> {
        indicatorConfig?.normalColor = color
        return this
    }

    fun setIndicatorNormalColorRes(@ColorRes color: Int): Banner<*, *> {
        setIndicatorNormalColor(ContextCompat.getColor(context, color))
        return this
    }

    fun setIndicatorGravity(@IndicatorConfig.Direction gravity: Int): Banner<*, *> {
        if (indicatorConfig?.isAttachToBanner == true) {
            indicatorConfig?.gravity = gravity
            indicator?.indicatorView?.postInvalidate()
        }
        return this
    }

    fun setIndicatorSpace(indicatorSpace: Int): Banner<*, *> {
        indicatorConfig?.indicatorSpace = indicatorSpace
        return this
    }

    fun setIndicatorMargins(margins: IndicatorConfig.Margins?): Banner<*, *> {
        if (indicatorConfig?.isAttachToBanner == true) {
            indicatorConfig?.margins = margins
            indicator?.indicatorView?.requestLayout()
        }
        return this
    }

    fun setIndicatorWidth(normalWidth: Int, selectedWidth: Int): Banner<*, *> {
        indicatorConfig?.normalWidth = normalWidth
        indicatorConfig?.selectedWidth = selectedWidth
        return this
    }

    fun setIndicatorNormalWidth(normalWidth: Int): Banner<*, *> {
        indicatorConfig?.normalWidth = normalWidth
        return this
    }

    fun setIndicatorSelectedWidth(selectedWidth: Int): Banner<*, *> {
        indicatorConfig?.selectedWidth = selectedWidth
        return this
    }

    fun setIndicatorRadius(indicatorRadius: Int): Banner<*, *> {
        indicatorConfig?.radius = indicatorRadius
        return this
    }

    fun setIndicatorHeight(indicatorHeight: Int): Banner<*, *> {
        indicatorConfig?.height = indicatorHeight
        return this
    }

    /**
     * **********************************************************************
     * ------------------------ 生命周期控制 --------------------------------*
     * **********************************************************************
     */
    fun addBannerLifecycleObserver(owner: LifecycleOwner?): Banner<*, *> {
        owner?.lifecycle?.addObserver(BannerLifecycleObserverAdapter(owner, this))
        return this
    }

    override fun onStart(owner: LifecycleOwner?) {
        start()
    }

    override fun onStop(owner: LifecycleOwner?) {
        stop()
    }

    override fun onDestroy(owner: LifecycleOwner?) {
        destroy()
    }

    companion object {
        const val INVALID_VALUE = -1
        const val HORIZONTAL = 0
        const val VERTICAL = 1
    }

    init {
        initialize(context)
        attrs?.let {
            initTypedArray(it)
        }
    }
}