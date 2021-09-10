package com.trs88.kuro_ui.slider

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.trs88.kuro_ui.R
import com.trs88.kuro_ui.kuroItem.KuroViewHolder
import com.trs88.kurolibrary.log.KuroLog
import kotlinx.android.synthetic.main.kuro_slider_menu_item.view.*

class KuroSliderView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {
    private var menuItemAttr: MenuItemAttr
    private val MENU_WIDTH = applyUnit(TypedValue.COMPLEX_UNIT_DIP, 100f)//表示100是个dp，想转化成px
    private val MENU_HEIGHT = applyUnit(TypedValue.COMPLEX_UNIT_DIP, 45f)
    private val MENU_TEXT_SIZE = applyUnit(TypedValue.COMPLEX_UNIT_SP, 14f)
    private val MENU_COLOR_NORMAL = Color.parseColor("#666666")
    private val MENU_COLOR_SELECT = Color.parseColor("#DD3127")
    private val TEXT_COLOR_NORMAL = Color.parseColor("#666666")
    private val TEXT_COLOR_SELECT = Color.parseColor("#DD3127")
    private val BG_COLOR_NORMAL = Color.parseColor("#F7F8F9")
    private val BG_COLOR_SELECT = Color.parseColor("#FFFFFF")

    val MENU_ITEM_LAYOUT_RES_ID = R.layout.kuro_slider_menu_item
    val CONTENT_ITEM_LAYOUT_RES_ID = R.layout.kuro_slider_content_item

    val menuView = RecyclerView(context)
    val contentView = RecyclerView(context)

    init {
        menuItemAttr = parseMenuItemAttr(attrs)
        orientation = HORIZONTAL

        menuView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        menuView.overScrollMode = View.OVER_SCROLL_NEVER
        menuView.itemAnimator = null

        contentView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        contentView.overScrollMode = View.OVER_SCROLL_NEVER
        contentView.itemAnimator = null

        addView(menuView)
        addView(contentView)
    }

    fun bindMenuView(
        layoutRes: Int = MENU_ITEM_LAYOUT_RES_ID,
        itemCount: Int,
        onBindView: (KuroViewHolder, Int) -> Unit,
        onItemClick: (KuroViewHolder, Int) -> Unit
    ) {
        menuView.layoutManager =LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        menuView.adapter = MenuAdapter(layoutRes,itemCount,onBindView,onItemClick)
    }

    fun bindContentView(
        layoutRes: Int = CONTENT_ITEM_LAYOUT_RES_ID,
        itemCount: Int,
        itemDecoration: RecyclerView.ItemDecoration?,
        layoutManager:RecyclerView.LayoutManager,
        onBindView: (KuroViewHolder, Int) -> Unit,
        onItemClick: (KuroViewHolder, Int) -> Unit
    ){
        if (contentView.layoutManager ==null){
            contentView.layoutManager =layoutManager
            contentView.adapter=ContentAdapter(layoutRes)
            itemDecoration?.let {
                contentView.addItemDecoration(itemDecoration)
            }
        }
    }

    inner class ContentAdapter(val layoutRes: Int):RecyclerView.Adapter<KuroViewHolder>(){
        private lateinit var onItemClick:(KuroViewHolder,Int)->Unit
        private lateinit var onBindView: (KuroViewHolder, Int) -> Unit
        private var count:Int =0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KuroViewHolder {
            val itemView=LayoutInflater.from(context).inflate(layoutRes,parent,false)
            val remainSpace =width-paddingLeft-paddingRight-menuItemAttr.width
            val layoutManager =(parent as RecyclerView).layoutManager
            var spanCount =0
            if (layoutManager is GridLayoutManager){
                spanCount =layoutManager.spanCount
            }else if (layoutManager is StaggeredGridLayoutManager){
                spanCount =layoutManager.spanCount
            }

            if (spanCount >0){
                val itemWidth =remainSpace/spanCount
                itemView.layoutParams=RecyclerView.LayoutParams(itemWidth,itemWidth)
            }
            return KuroViewHolder(itemView)
        }

        override fun getItemCount(): Int {
           return count
        }

        override fun onBindViewHolder(holder: KuroViewHolder, position: Int) {
            onBindView(holder,position)
            holder.itemView.setOnClickListener{
                onItemClick(holder,position)
            }
        }

    }

    inner class MenuAdapter(
        val layoutRes: Int,
        val count: Int,
        val onBindView: (KuroViewHolder, Int) -> Unit,
        val onItemClick: (KuroViewHolder, Int) -> Unit
    ): RecyclerView.Adapter<KuroViewHolder>() {
        //当前选择的item
        private var currentSelectIndex =0
        //上一次选中item的位置
        private var lastSelectIndex =0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KuroViewHolder {
            val itemView = LayoutInflater.from(context).inflate(layoutRes, parent, false)
            val params =RecyclerView.LayoutParams(menuItemAttr.width,menuItemAttr.height)
            itemView.setBackgroundColor(menuItemAttr.normalBackgroundColor)
            itemView.findViewById<TextView>(R.id.menu_item_title)?.setTextColor(menuItemAttr.textColor)
            itemView.findViewById<ImageView>(R.id.menu_item_indicator)?.setImageDrawable(menuItemAttr.indicator)
            return KuroViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return count
        }

        override fun onBindViewHolder(holder: KuroViewHolder, position: Int) {
            holder.itemView.setOnClickListener {
                currentSelectIndex =position
                notifyItemChanged(position)
                notifyItemChanged(lastSelectIndex)
            }
            if (currentSelectIndex ==position){
                onItemClick(holder,position)
                lastSelectIndex =currentSelectIndex
            }

            applyItemAttr(position,holder)

            onBindView(holder,position)
        }

        private fun applyItemAttr(position: Int, holder: KuroViewHolder) {
            val selected =position ==currentSelectIndex
            val titleView:TextView? =holder.itemView.menu_item_title
            val indicatorView = holder.itemView.menu_item_indicator

            indicatorView?.visibility =if (selected) View.VISIBLE else View.GONE
            titleView?.setTextSize(TypedValue.COMPLEX_UNIT_PX,if (selected)menuItemAttr.selectTextSize.toFloat() else menuItemAttr.textSize.toFloat())
            holder.itemView.setBackgroundColor(if (selected)menuItemAttr.selectBackgroundColor else menuItemAttr.normalBackgroundColor)
            titleView?.isSelected=selected
        }

    }

    private fun parseMenuItemAttr(attrs: AttributeSet?): MenuItemAttr {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KuroSliderView)
        val menuItemWidth = typedArray.getDimensionPixelOffset(R.styleable.KuroSliderView_menuItemWidth, MENU_WIDTH)
        val menuItemHeight = typedArray.getDimensionPixelOffset(R.styleable.KuroSliderView_menuItemHeight, MENU_HEIGHT)
        val menuItemTextSize = typedArray.getDimensionPixelOffset(R.styleable.KuroSliderView_menuItemTextSize, MENU_TEXT_SIZE)
        val menuItemSelectTextSize = typedArray.getDimensionPixelOffset(R.styleable.KuroSliderView_menuItemSelectTextSize, MENU_TEXT_SIZE)
        val menuItemTextColor = typedArray.getColorStateList(R.styleable.KuroSliderView_menuItemTextColor) ?: generateColorStateList()
        val menuItemIndicator = typedArray.getDrawable(R.styleable.KuroSliderView_menuItemIndicator) ?: ContextCompat.getDrawable(
            context,
            R.drawable.shape_kuro_slider_indicator
        )
        val menuItemBackgroundColor = typedArray.getColor(R.styleable.KuroSliderView_menuItemBackgroundColor, BG_COLOR_NORMAL)
        val menuItemBackgroundSelectColor = typedArray.getColor(R.styleable.KuroSliderView_menuItemSelectBackgroundColor, BG_COLOR_SELECT)
        typedArray.recycle()

        return MenuItemAttr(
            menuItemWidth,
            menuItemHeight,
            menuItemTextColor,
            menuItemBackgroundSelectColor,
            menuItemBackgroundColor,
            menuItemTextSize,
            menuItemSelectTextSize,
            menuItemIndicator
        )
    }


    private fun generateColorStateList(): ColorStateList {
        val states = Array(2) { IntArray(2) }
        val colors = IntArray(2)

        colors[0] = TEXT_COLOR_SELECT
        colors[1] = TEXT_COLOR_NORMAL

        states[0] = IntArray(1) { android.R.attr.state_selected }
        states[1] = IntArray(1)

        return ColorStateList(states, colors)
    }

    private fun applyUnit(unit: Int, value: Float): Int {
        return TypedValue.applyDimension(unit, value, context.resources.displayMetrics).toInt()
    }

    data class MenuItemAttr(
        val width: Int,
        val height: Int,
        val textColor: ColorStateList,
        val selectBackgroundColor: Int,
        val normalBackgroundColor: Int,
        val textSize: Int,
        val selectTextSize: Int,
        val indicator: Drawable?
    )
}