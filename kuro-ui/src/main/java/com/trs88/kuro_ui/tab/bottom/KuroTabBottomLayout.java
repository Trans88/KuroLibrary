package com.trs88.kuro_ui.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.trs88.kuro_ui.R;
import com.trs88.kuro_ui.tab.common.IKuroTabLayout;
import com.trs88.kurolibrary.log.KuroLog;
import com.trs88.kurolibrary.util.KuroDisplayUtil;
import com.trs88.kurolibrary.util.KuroViewUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 存放bottom和上部布局的layout
 */
public class KuroTabBottomLayout extends FrameLayout implements IKuroTabLayout<KuroTabBottom,KuroTabBottomInfo<?>> {
    private static final String TAG_TAB_BOTTOM= "TAG_TAB_BOTTOM";
    private List<OnTabSelectedListener<KuroTabBottomInfo<?>>> tabSelectedListeners =new ArrayList<>();
    private KuroTabBottomInfo<?> selectedInfo;
    //底部透明度
    private float bottomAlpha =1f;
    //TabBottom高度
    private float tabBottomHeight =50;
    //TabBottom的头部线条高度
    private float bottomLineHeight =0.5f;
    //TabBottom的头部线条颜色
    private String bottomLineColor ="#dfe0e1";
    private List<KuroTabBottomInfo<?>> infoList;

    public KuroTabBottomLayout(@NonNull Context context) {
        this(context,null);
    }

    public KuroTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public KuroTabBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    public KuroTabBottom findTab(@NonNull KuroTabBottomInfo<?> info) {
        ViewGroup ll =findViewWithTag(TAG_TAB_BOTTOM);
        for (int i=0;i<ll.getChildCount();i++){
            View child =ll.getChildAt(i);
            if (child instanceof  KuroTabBottom){
                KuroTabBottom tab = (KuroTabBottom) child;
                if (tab.getKuroTabInfo() ==info){
                    return tab;
                }
            }
        }

        return null;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<KuroTabBottomInfo<?>> listener) {
        tabSelectedListeners.add(listener);
    }

    @Override
    public void defaultSelected(@NonNull KuroTabBottomInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    @Override
    public void inflateInfo(@NonNull List<KuroTabBottomInfo<?>> infoList) {
        if (infoList.isEmpty()){
            return;
        }
        this.infoList =infoList;
        //移除之前已经添加的View,第0个元素是底部导航上面的区域，不能移除，所以>0
        for (int i =getChildCount()-1;i>0;i--){
            removeViewAt(i);
        }
        selectedInfo =null;
        addBackground();

        // 清除之前添加的KuroTabBottom listener,Tips: Java foreach remove问题，边增删边查询
        Iterator<OnTabSelectedListener<KuroTabBottomInfo<?>>> iterable =tabSelectedListeners.iterator();
        while (iterable.hasNext()){
            if (iterable.next() instanceof KuroTabBottom){
                iterable.remove();
            }
        }

        int height =KuroDisplayUtil.dp2px(tabBottomHeight,getResources());
        //为什么不用LinearLayout:当动态改变child大小后Gravity.BOTTOM会失效
        FrameLayout ll =new FrameLayout(getContext());
        ll.setTag(TAG_TAB_BOTTOM);
        int width =KuroDisplayUtil.getDisplayWidthInPx(getContext())/infoList.size();
        for (int i =0;i<infoList.size();i++){
            final KuroTabBottomInfo<?> info =infoList.get(i);
            LayoutParams params =new LayoutParams(width,height);
            params.gravity =Gravity.BOTTOM;
            params.leftMargin =i*width; //依次向右排列

            KuroTabBottom tabBottom =new KuroTabBottom(getContext());

            tabSelectedListeners.add(tabBottom);
            tabBottom.setKuroTabInfo(info);

            ll.addView(tabBottom,params);

            tabBottom.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelected(info);
                }
            });
        }

        LayoutParams flParams =new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        flParams.gravity =Gravity.BOTTOM;
        addBottomLine();
        addView(ll,flParams);
        fixContentView();
    }



    public void setTabAlpha(float alpha) {
        this.bottomAlpha = alpha;
    }

    public void setTabHeight(float tabHeight) {
        this.tabBottomHeight = tabHeight;
    }

    public void setBottomLineHeight(float bottomLineHeight) {
        this.bottomLineHeight = bottomLineHeight;
    }

    public void setBottomLineColor(String bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
    }

    /**
     * 添加BottomLine
     */
    private void addBottomLine(){
        View bottomLine =new View(getContext());
        bottomLine.setBackgroundColor(Color.parseColor(bottomLineColor));
        LayoutParams bottomLineParams =new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,KuroDisplayUtil.dp2px(bottomLineHeight,getResources()));
        bottomLineParams.gravity =Gravity.BOTTOM;
        bottomLineParams.bottomMargin =KuroDisplayUtil.dp2px(tabBottomHeight-bottomLineHeight,getResources());
        addView(bottomLine,bottomLineParams);
        bottomLine.setAlpha(bottomAlpha);
    }

    private void onSelected(@NonNull KuroTabBottomInfo<?> nextInfo){
        for (OnTabSelectedListener<KuroTabBottomInfo<?>>listener:tabSelectedListeners){
            listener.OnTabSelectedChange(infoList.indexOf(nextInfo),selectedInfo,nextInfo);
        }
        this.selectedInfo =nextInfo;
    }

    /**
     * 添加白色背景
     */
    private void addBackground(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.kuro_bottom_layout_bg,null);
        LayoutParams params =new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, KuroDisplayUtil.dp2px(tabBottomHeight,getResources()));
        params.gravity = Gravity.BOTTOM;
        addView(view,params);
        view.setAlpha(bottomAlpha);
    }

    /**
     * 修复内容区域的底部Padding
     */
    private void fixContentView(){
        if (!(getChildAt(0) instanceof ViewGroup)){
            return;
        }

        ViewGroup rootView= (ViewGroup) getChildAt(0);
        ViewGroup targetView = KuroViewUtil.findTypeView(rootView, RecyclerView.class);
        if (targetView==null){
            targetView =KuroViewUtil.findTypeView(rootView, ScrollView.class);
        }

        if (targetView==null){
            targetView =KuroViewUtil.findTypeView(rootView, AbsListView.class);
        }

        if (targetView!=null){
            targetView.setPadding(0,0,0,KuroDisplayUtil.dp2px(tabBottomHeight,getResources()));
            //让padding可以回执
            targetView.setClipToPadding(false);
        }
    }
}
