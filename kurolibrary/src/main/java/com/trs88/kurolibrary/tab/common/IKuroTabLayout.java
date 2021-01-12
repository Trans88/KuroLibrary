package com.trs88.kurolibrary.tab.common;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 *
 * @param <Tab> 顶部和底部tab
 * @param <D> 对应的数据
 */
public interface IKuroTabLayout<Tab extends ViewGroup, D> {
    //根据数据查找对应的Tab
    Tab findTab(@NonNull D info);

    //添加监听器
    void addTabSelectedChangeListener(OnTabSelectedListener<D> listener);

    //默认选择
    void defaultSelected(@NonNull D defaultInfo);

    //对数据进行初始化
    void inflateInfo(@NonNull List<D> infoList);

    /**
     * 通知对应索引的选中
     * @param  <D>prevInfo 上一个索引的数据
     * @param <D>nextInfo 下一个索引的数据
     */
    interface OnTabSelectedListener<D> {
        void OnTabSelectedChange(int index, @Nullable D prevInfo, @NonNull D nextInfo);
    }
}
