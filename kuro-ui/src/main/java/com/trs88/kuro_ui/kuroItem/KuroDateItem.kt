package com.trs88.kuro_ui.kuroItem

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class KuroDateItem <DATA,VH:RecyclerView.ViewHolder>(data:DATA){
    private lateinit var adapter: KuroAdapter
    var mData:DATA?=null
    init {
        this.mData=data
    }

    abstract fun onBindData(holder: RecyclerView.ViewHolder,position:Int)


    /**
     * 获取资源文件的ID
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
        adapter.refreshItem(this)
    }

    /**
     * 从列表移除
     */
    fun removeItem(){
        adapter.removeItem(this)
    }

    /**
     * 该Item在列表上占据几列
     */
    fun getSpanSize():Int{
        return 0
    }
}