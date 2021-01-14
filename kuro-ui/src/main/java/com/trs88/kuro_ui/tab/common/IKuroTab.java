package com.trs88.kuro_ui.tab.common;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

/**
 * KuroTab对外的接口
 * @param <D>
 */
public interface IKuroTab<D> extends IKuroTabLayout.OnTabSelectedListener<D> {

    void setKuroTabInfo(@NonNull D data);

    /**
     *动态修改某个Item的大小
     * @param height
     */
    void resetHeight(@Px int height);
}
