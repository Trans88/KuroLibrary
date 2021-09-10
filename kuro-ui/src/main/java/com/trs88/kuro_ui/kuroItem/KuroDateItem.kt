package com.trs88.kuro_ui.kuroItem

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * DATA javabean对象
 */
abstract class KuroDateItem <DATA,VH:RecyclerView.ViewHolder>(data:DATA?){
    private lateinit var adapter: KuroAdapter
    var mData:DATA?=null //该item的数据对象

    init {
        this.mData=data
    }

    /**
     * 绑定数据
     */
    abstract fun <VH:RecyclerView.ViewHolder> onBindData(holder:VH, position:Int)


    /**
     * 获取该item资源文件的ID
     */
    open fun getItemLayoutRes():Int{
        return -1
    }

    /**
     * 返回该Item的视图View
     */
    open fun getItemView(parent:ViewGroup): View?{
        return null
    }

    fun setAdapter(adapter: KuroAdapter){
        this.adapter =adapter;
    }

    /**
     * 刷新列表
     */
    fun refreshItem(){
        if(adapter==null){
            throw IllegalArgumentException("KuroAdapter is null pls setAdapter first")
        }
        adapter.refreshItem(this)
    }

    /**
     * 从列表移除
     */
    fun removeItem(){
        if(adapter==null){
            throw IllegalArgumentException("KuroAdapter is null pls setAdapter first")
        }

        adapter.removeItem(this)
    }

    fun getPosition():Int{
        return adapter.getPosition(this)
    }

    /**
     * 该Item在列表上占据几列
     */
    fun getSpanSize():Int{
        return 0
    }

    /**
     * 该item被滑进屏幕
     */
    open fun onViewAttachedToWindow(holder: VH){
    }

    /**
     * 该item被移除出屏幕
     */
    open fun onViewDetachedFromWindow(holder: VH){

    }
}