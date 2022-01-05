package com.youth.banner.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.youth.banner.util.BannerUtils
import com.youth.banner.config.BannerConfig
import com.youth.banner.holder.IViewHolder
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.R
import android.view.*
import com.youth.banner.util.LogUtils

abstract class BannerAdapter<T, VH : RecyclerView.ViewHolder?>(datas: MutableList<T>? = null) :
    RecyclerView.Adapter<VH>(), IViewHolder<T, VH> {

//    var datas: List<T>? = mutableListOf()
//        @SuppressLint("NotifyDataSetChanged")
//        set(value) {
//            Log.d("BannerAdapter", "set datas")
//            if (value == null) {
//                Log.d("BannerAdapter", "value is null set datas = mutableListOf")
//                field = mutableListOf()
//            }
//
//            Log.d("BannerAdapter", "set datas finish and notifyDataSetChanged")
//            notifyDataSetChanged()
//        }

    /**
     * data, Only allowed to get.
     * 数据, 只允许 get。
     */
    var datas: MutableList<T> = datas ?: arrayListOf()
        internal set

    /**
     * 使用新的数据集合，改变原有数据集合内容。
     * 注意：不会替换原有的内存引用，只是替换内容
     *
     * @param list Collection<T>?
     */
    @SuppressLint("NotifyDataSetChanged")
    open fun setList(list: Collection<T>?) {
        if (list !== this.datas) {
            this.datas.clear()
            if (!list.isNullOrEmpty()) {
                this.datas.addAll(list)
            }
        } else {
            if (!list.isNullOrEmpty()) {
                val newList = ArrayList(list)
                this.datas.clear()
                this.datas.addAll(newList)
            } else {
                this.datas.clear()
            }
        }

        notifyDataSetChanged()
    }

    /**
     * setting up a new instance to data;
     * 设置新的数据实例，替换原有内存引用。
     * 通常情况下，如非必要，请使用[setList]修改内容
     *
     * @param list
     */
    @SuppressLint("NotifyDataSetChanged")
    open fun setNewInstance(list: MutableList<T>?) {
        if (list === this.datas) {
            return
        }

        this.datas = list ?: arrayListOf()
        notifyDataSetChanged()
    }

    private var onBannerListener: OnBannerListener<T>? = null

    var viewHolder: VH? = null
        private set

    private var increaseCount = BannerConfig.INCREASE_COUNT

    /**
     * 获取指定的实体（可以在自己的adapter自定义，不一定非要使用）
     *
     * @param position 真实的position
     * @return
     */
    fun getData(position: Int): T? {
        return datas[position]
    }

    /**
     * 获取指定的实体（可以在自己的adapter自定义，不一定非要使用）
     *
     * @param position 这里传的position不是真实的，获取时转换了一次
     * @return
     */
    fun getRealData(position: Int): T? {
        return datas[getRealPosition(position)]
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        viewHolder = holder

        holder?.itemView?.run {
            LogUtils.d("position $position")
            val real = getRealPosition(position)
            LogUtils.d("real position $real")
            datas[real]?.let {
                setTag(R.id.banner_data_key, it)
                setTag(R.id.banner_pos_key, real)
                onBindView(holder, it, real, realCount)
                setOnClickListener { view: View? ->
                    onBannerListener?.OnBannerClick(
                        it,
                        real
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val vh = onCreateHolder(parent, viewType)
        vh?.itemView?.setOnClickListener { v: View? ->
            val data = vh.itemView.getTag(R.id.banner_data_key) as T
            val real = vh.itemView.getTag(R.id.banner_pos_key) as Int
            onBannerListener?.OnBannerClick(data, real)
        }
        return vh
    }

    override fun getItemCount(): Int {
        return if (realCount > 1) realCount + increaseCount else realCount
    }

    val realCount: Int
        get() = datas.size

    fun getRealPosition(position: Int): Int {
        return BannerUtils.getRealPosition(
            increaseCount == BannerConfig.INCREASE_COUNT,
            position,
            realCount
        )
    }

    fun setOnBannerListener(listener: OnBannerListener<T>?) {
        onBannerListener = listener
    }

    fun setIncreaseCount(increaseCount: Int) {
        this.increaseCount = increaseCount
    }
}