package com.trs88.kuro_ui.kuroItem

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.ParameterizedType

class KuroAdapter(context:Context):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mContext: Context
    private var mInflater:LayoutInflater?=null
    private var dataSets = ArrayList<KuroDateItem<*,RecyclerView.ViewHolder>>()
    //存放item的类型
    private var typeArrays =SparseArray<KuroDateItem<*,RecyclerView.ViewHolder>>()
    init {
        this.mContext =context
        this.mInflater = LayoutInflater.from(context)
    }

    fun addItem(index:Int,item:KuroDateItem<*,RecyclerView.ViewHolder>,notify:Boolean){
        if (index>0){
            //添加到指定位置
            dataSets.add(index,item)
        }else{
            //添加到末尾
            dataSets.add(item)
        }

        //刷新位置
        val notifyPos =if (index>0)index else dataSets.size-1
        if (notify){
            notifyItemInserted(notifyPos)
        }
    }

    fun addItems(items:List<KuroDateItem<*,RecyclerView.ViewHolder>>,notify: Boolean){
        //当前列表的起始位置
        val start=dataSets.size
        for (item in items){
            dataSets.add(item)
        }

        if (notify){
            notifyItemRangeInserted(start,items.size)
        }
    }

    fun removeItem(index: Int):KuroDateItem<*,RecyclerView.ViewHolder>?{
        if (index>0&& index<dataSets.size){
            val remove =dataSets.removeAt(index)
            notifyItemRemoved(index)
            return remove
        }else{
            return null
        }
    }

    fun removeItem(item:KuroDateItem<*,*>){
        if (item!=null){
            val index= dataSets.indexOf(item)
            removeItem(index)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val dateItem = dataSets[position]
        //相同类型的返回值应该是相同的所有可以使用hashcode作为返回值
        val type = dateItem.javaClass.hashCode()

        //如果还没有包含这种类型的item,则添加进来
        if (typeArrays.indexOfKey(type)<0){
            typeArrays.put(type,dateItem)
        }

        return type
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val dataItem = typeArrays.get(viewType)
        var view: View?=dataItem.getItemView(parent)
        if (view ==null){
            val layoutRes =dataItem.getItemLayoutRes()
            if (layoutRes<0){
                throw RuntimeException("dataItem:${dataItem.javaClass.name} must override getItemView or getItemLayout")
            }
            view =mInflater!!.inflate(layoutRes,parent,false)
        }

        return createViewHolderInternal(dataItem.javaClass,view)
    }

    private fun createViewHolderInternal(javaClass: Class<KuroDateItem<*, RecyclerView.ViewHolder>>, view: View?): RecyclerView.ViewHolder {
        val superClass = javaClass.genericSuperclass
        if (superClass is ParameterizedType){
            val arguments = superClass.actualTypeArguments
            for (argument in arguments) {
                if (argument is Class<*> && RecyclerView.ViewHolder::class.java.isAssignableFrom(argument)){
                    return argument.getConstructor(View::class.java).newInstance(view) as RecyclerView.ViewHolder
                }
            }
        }
        return object :RecyclerView.ViewHolder(view!!){}

    }

    override fun getItemCount(): Int {
        return dataSets.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val kuroDateItem = dataSets[position]
        kuroDateItem.onBindData(holder,position)
    }

    /**
     * 和RecyclerView关联时回调
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager){
            val spanCount =layoutManager.spanCount
            layoutManager.spanSizeLookup =object :GridLayoutManager.SpanSizeLookup(){
                override fun getSpanSize(position: Int): Int {
                    if (position<dataSets.size){
                        val kuroDateItem = dataSets[position]
                        val spanSize = kuroDateItem.getSpanSize()
                        return if (spanSize<=0) spanCount else spanSize
                    }
                    return spanCount
                }
            }
        }
    }

    fun refreshItem(kuroDateItem: KuroDateItem<*, *>) {
        val indexOf = dataSets.indexOf(kuroDateItem)
        notifyItemChanged(indexOf)
    }


}