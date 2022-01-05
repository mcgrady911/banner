package com.youth.banner.holder

import android.view.ViewGroup

interface IViewHolder<T, VH> {
    /**
     * 创建ViewHolder
     *
     * @return XViewHolder
     */
    fun onCreateHolder(parent: ViewGroup, viewType: Int): VH

    /**
     * 绑定布局数据
     *
     * @param holder   XViewHolder
     * @param data     数据实体
     * @param position 当前位置
     * @param size     总数
     */
    fun onBindView(holder: VH, data: T, position: Int, size: Int)
}