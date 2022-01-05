package com.youth.banner.util

import com.youth.banner.Banner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.viewpager2.widget.ViewPager2
import java.lang.Exception

/**
 * 改变LinearLayoutManager的切换速度
 */
class ScrollSpeedManger(
    private val banner: Banner<*, *>,
    linearLayoutManager: LinearLayoutManager?
) : LinearLayoutManager(
    banner.context, linearLayoutManager?.orientation ?: HORIZONTAL, false
) {
    override fun smoothScrollToPosition(
        recyclerView: RecyclerView,
        state: RecyclerView.State,
        position: Int
    ) {
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateTimeForDeceleration(dx: Int): Int {
                    return banner.scrollTime
                }
            }
        linearSmoothScroller.targetPosition = position
        startSmoothScroll(linearSmoothScroller)
    }

    companion object {
        fun reflectLayoutManager(banner: Banner<*, *>) {
            if (banner.scrollTime < 100) return
            try {
                val viewPager2 = banner.viewPager2
                val recyclerView = viewPager2?.getChildAt(0) as RecyclerView
                recyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                val speedManger =
                    ScrollSpeedManger(banner, recyclerView.layoutManager as LinearLayoutManager?)
                recyclerView.layoutManager = speedManger
                val LayoutMangerField = ViewPager2::class.java.getDeclaredField("mLayoutManager")
                LayoutMangerField.isAccessible = true
                LayoutMangerField[viewPager2] = speedManger
                val pageTransformerAdapterField =
                    ViewPager2::class.java.getDeclaredField("mPageTransformerAdapter")
                pageTransformerAdapterField.isAccessible = true
                val mPageTransformerAdapter = pageTransformerAdapterField[viewPager2]
                if (mPageTransformerAdapter != null) {
                    val aClass: Class<*> = mPageTransformerAdapter.javaClass
                    val layoutManager = aClass.getDeclaredField("mLayoutManager")
                    layoutManager.isAccessible = true
                    layoutManager[mPageTransformerAdapter] = speedManger
                }
                val scrollEventAdapterField =
                    ViewPager2::class.java.getDeclaredField("mScrollEventAdapter")
                scrollEventAdapterField.isAccessible = true
                val mScrollEventAdapter = scrollEventAdapterField[viewPager2]
                if (mScrollEventAdapter != null) {
                    val aClass: Class<*> = mScrollEventAdapter.javaClass
                    val layoutManager = aClass.getDeclaredField("mLayoutManager")
                    layoutManager.isAccessible = true
                    layoutManager[mScrollEventAdapter] = speedManger
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}