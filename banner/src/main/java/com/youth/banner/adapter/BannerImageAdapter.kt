package com.youth.banner.adapter

import android.view.ViewGroup
import com.youth.banner.holder.BannerImageHolder
import android.widget.ImageView

/**
 * 默认实现的图片适配器，图片加载需要自己实现
 */
abstract class BannerImageAdapter<T>(mData: MutableList<T>?) :
    BannerAdapter<T, BannerImageHolder?>(mData) {
    override fun onCreateHolder(parent: ViewGroup, viewType: Int): BannerImageHolder {
        val imageView = ImageView(parent.context)
        //注意，必须设置为match_parent，这个是viewpager2强制要求的
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        imageView.layoutParams = params
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        return BannerImageHolder(imageView)
    }
}