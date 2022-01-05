package com.youth.banner.listener

interface OnBannerListener<T> {
    /**
     * 点击事件
     *
     * @param data     数据实体
     * @param position 当前位置
     */
    fun OnBannerClick(data: T, position: Int)
}