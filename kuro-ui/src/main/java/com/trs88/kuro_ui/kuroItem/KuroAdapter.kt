package com.trs88.kuro_ui.kuroItem

import android.content.Context
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.trs88.kurolibrary.activity.AppGlobals
import com.trs88.kurolibrary.log.KuroLog
import okhttp3.internal.notifyAll
import java.lang.reflect.ParameterizedType

class KuroAdapter(context:Context):Adapter<ViewHolder>() {
    private var mContext: Context
    private var mInflater:LayoutInflater?=null
    private var dataSets = java.util.ArrayList<KuroDateItem<*,out ViewHolder>>()
    //存放item的类型
    private var typeArrays =SparseArray<KuroDateItem<*,out ViewHolder>>()
    init {
        this.mContext =context
        this.mInflater = LayoutInflater.from(context)
    }

    /**
     * 注册一条数据
     */
    fun addItem(index:Int,item:KuroDateItem<*,out ViewHolder>,notify:Boolean){
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
//            notifyDataSetChanged()
        }
    }

    fun addItems(items:List<KuroDateItem<*,out ViewHolder>>, notify: Boolean){
        //当前列表的起始位置
        val start=dataSets.size

        for (item in items){
            dataSets.add(item)
        }

        if (notify){
            notifyItemRangeInserted(start,items.size)
        }
    }

    /**
     * 移除对应下标的数据
     */
    fun removeItem(index: Int):KuroDateItem<*,out ViewHolder>?{
        if (index>0&& index<dataSets.size){
            val remove =dataSets.removeAt(index)
            notifyItemRemoved(index)
            return remove
        }else{
            return null
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val dataItem = typeArrays.get(viewType)
        var view: View?=dataItem.getItemView(parent)

        //如果直接获取视图获取不到，尝试从getItemLayoutRes里获取视图
        if (view ==null){
            val layoutRes =dataItem.getItemLayoutRes()
//            KuroLog.e("layoutRes:$layoutRes")
            if (layoutRes<0){
                throw RuntimeException("dataItem:${dataItem.javaClass.name} must override getItemView or getItemLayout")
            }
            view =mInflater!!.inflate(layoutRes,parent,false)
        }

        return createViewHolderInternal(dataItem.javaClass,view!!)
    }

    /**
     * 取出ViewHolder里标注的泛型
     */
    private fun createViewHolderInternal(javaClass: Class<KuroDateItem<*, out ViewHolder>>, view: View):ViewHolder {
        //得到KuroDateItem对象
        val superClass = javaClass.genericSuperclass

        //如果是参数泛型类型的
        if (superClass is ParameterizedType){
            //返回KuroDateItem泛型参数的集合，也就是DATA、VH
            val arguments = superClass.actualTypeArguments
            for (argument in arguments) {
                if (argument is Class<*> && ViewHolder::class.java.isAssignableFrom(argument)){
                    try {
                        //通过构造方法构造出反射的ViewHolder
                        return argument.getConstructor(View::class.java).newInstance(view) as ViewHolder
                    }catch (e:Throwable){
                        e.printStackTrace()
                    }
                }
            }
        }

        KuroLog.i("创建ViewHolder失败")

        //如果失败返回默认的ViewHolder
        return object :ViewHolder(view){}

    }

    override fun getItemCount(): Int {
        return dataSets.size
    }

    override fun onBindViewHolder(holder:ViewHolder, position: Int) {
        val kuroDateItem = dataSets[position]
        kuroDateItem.onBindData(holder,position)
    }

    /**
     * 和RecyclerView关联时回调
     */
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        //如果是GridLayoutManager，为他设置spanSizeLookup 来控制一行几列
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


    fun removeItem(item:KuroDateItem<*,*>){
        if (item!=null){
            val index= dataSets.indexOf(item)
            removeItem(index)
        }
    }

    fun getPosition(kuroDateItem: KuroDateItem<*, *>):Int{
        val indexOf = dataSets.indexOf(kuroDateItem)
        return indexOf
    }




}