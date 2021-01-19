package com.trs88.kuro_ui.refresh;

public enum KuroRefreshState {
    /**
     * 初始态
     */
    STATE_INIT,
    /**
     * Header展示的状态
     */
    STATE_VISIBLE,
    /**
     * 超出可刷新距离的状态
     */
    STATE_OVER,
    /**
     * 刷新中的状态
     */
    STATE_REFRESH,
    /**
     * 超出刷新位置松开手后的状态
     */
    STATE_OVER_RELEASE

}
