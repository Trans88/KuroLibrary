package com.trs88.kuro_ui.tab.top;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.trs88.kuro_ui.tab.bottom.KuroTabBottom;
import com.trs88.kuro_ui.tab.bottom.KuroTabBottomInfo;
import com.trs88.kuro_ui.tab.common.IKuroTabLayout;
import com.trs88.kurolibrary.util.KuroDisplayUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KuroTabTopLayout extends HorizontalScrollView implements IKuroTabLayout<KuroTabTop, KuroTabTopInfo<?>> {
    private List<OnTabSelectedListener<KuroTabTopInfo<?>>> tabSelectedChangeListeners = new ArrayList<>();
    private KuroTabTopInfo<?> selectedInfo;
    private List<KuroTabTopInfo<?>> infoList;

    private int tabWith;

    public KuroTabTopLayout(Context context) {
        super(context);
    }

    public KuroTabTopLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KuroTabTopLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public KuroTabTop findTab(@NonNull KuroTabTopInfo<?> info) {
        ViewGroup ll = getRootLayout(false);
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (child instanceof KuroTabTop) {
                KuroTabTop tab = (KuroTabTop) child;
                if (tab.getKuroTabInfo() == info) {
                    return tab;
                }
            }
        }

        return null;
    }

    @Override
    public void addTabSelectedChangeListener(OnTabSelectedListener<KuroTabTopInfo<?>> listener) {
        tabSelectedChangeListeners.add(listener);
    }

    @Override
    public void defaultSelected(@NonNull KuroTabTopInfo<?> defaultInfo) {
        onSelected(defaultInfo);
    }

    @Override
    public void inflateInfo(@NonNull List<KuroTabTopInfo<?>> infoList) {
        if (infoList.isEmpty()) {
            return;
        }
        this.infoList = infoList;
        LinearLayout linearLayout = getRootLayout(true);
        selectedInfo = null;

        // 清除之前添加的KuroTabBottom listener,Tips: Java foreach remove问题，边增删边查询
        Iterator<OnTabSelectedListener<KuroTabTopInfo<?>>> iterable = tabSelectedChangeListeners.iterator();
        while (iterable.hasNext()) {
            if (iterable.next() instanceof KuroTabTop) {
                iterable.remove();
            }
        }

        for (int i = 0; i < infoList.size(); i++) {
            final KuroTabTopInfo<?> info = infoList.get(i);
            KuroTabTop tab = new KuroTabTop(getContext());
            tabSelectedChangeListeners.add(tab);

            tab.setKuroTabInfo(info);
            linearLayout.addView(tab);
            tab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSelected(info);
                }
            });
        }

    }

    private void onSelected(@NonNull KuroTabTopInfo<?> nextInfo) {
        for (OnTabSelectedListener<KuroTabTopInfo<?>> listener : tabSelectedChangeListeners) {
            listener.OnTabSelectedChange(infoList.indexOf(nextInfo), selectedInfo, nextInfo);
        }
        this.selectedInfo = nextInfo;

        autoScroll(nextInfo);
    }

    /**
     * 自动滚动，实现点击的位置能够自动滚动展示前后2个
     *
     * @param nextInfo 点击tab的info
     */
    private void autoScroll(KuroTabTopInfo<?> nextInfo) {
        KuroTabTop tabTop = findTab(nextInfo);
        if (tabTop == null) {
            return;
        }

        int index = infoList.indexOf(nextInfo);
        int[] loc = new int[2];
        //获取点击的控件在屏幕的位置
        tabTop.getLocationInWindow(loc);
        int scrollWidth;
        if (tabWith == 0) {
            tabWith = tabTop.getWidth();
        }
        //判断点击了屏幕左侧还是右侧
        if ((loc[0] + tabWith / 2) > KuroDisplayUtil.getDisplayWidthInPx(getContext()) / 2) {
            scrollWidth = rangeScrollWidth(index, 2);
        } else {
            scrollWidth = rangeScrollWidth(index, -2);
        }

        scrollTo(getScrollX()+scrollWidth,0);
    }

    /**
     * 获取可滚动的范围
     *
     * @param index 从第几个开始
     * @param range 向前向后的范围
     * @return 可滚动的范围
     */
    private int rangeScrollWidth(int index, int range) {
        int scrollWidth = 0;
        for (int i = 0; i < Math.abs(range); i++) {
            int next;
            if (range < 0) {
                next = range + i + index;
            } else {
                next = range - i + index;
            }

            if (next > 0 && next < infoList.size()) {
                if (range < 0) {
                    scrollWidth -= scrollWidth(next, false);
                } else {
                    scrollWidth += scrollWidth(next, true);
                }
            }
        }
        return scrollWidth;
    }

    /**
     * 指定位置的控件可滚动的距离
     *
     * @param index   指定位置的控件
     * @param toRight 是否点击了屏幕右侧
     * @return 可滚动的距离
     */
    private int scrollWidth(int index, boolean toRight) {
        KuroTabTop target = findTab(infoList.get(index));
        if (target == null) {
            return 0;
        }

        Rect rect = new Rect();
        target.getLocalVisibleRect(rect);
        if (toRight) {
            //点击屏幕右侧
            if (rect.right > tabWith) {
                //right坐标大于控件的宽度时，说明完全没有显示
                return tabWith;
            } else {
                //显示部分，减去已显示的宽度
                return tabWith - rect.right;
            }
        } else {
            //点击屏幕左侧
            if (rect.left <= -tabWith) {
                //left坐标小于-控件的宽度时，说明完全没有显示
                return tabWith;
            } else if (rect.left > 0) {
                //显示部分
                return rect.left;
            }

            return 0;
        }
    }

    private LinearLayout getRootLayout(boolean clear) {
        LinearLayout rootView = (LinearLayout) getChildAt(0);
        if (rootView == null) {
            rootView = new LinearLayout(getContext());
            rootView.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            addView(rootView, params);
        } else if (clear) {
            rootView.removeAllViews();
        }
        return rootView;
    }
}
