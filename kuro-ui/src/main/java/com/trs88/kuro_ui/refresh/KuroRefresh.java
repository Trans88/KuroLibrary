package com.trs88.kuro_ui.refresh;

public interface KuroRefresh {
    /**
     * 刷新时是否禁止滚动
     * @param disableRefreshScroll 是否禁止滚动
     */
    void setDisableRefreshScroll(boolean disableRefreshScroll);

    /**
     * 刷新完成
     */
    void refreshFinished();

    /**
     * 设置刷新的监听器
     * @param kuroRefreshListener 刷新的监听器
     */
    void setRefreshListener(KuroRefreshListener kuroRefreshListener);

    /**
     * 设置下拉刷新的视图
     * @param kuroOverView 下拉刷新的视图
     */
    void setRefreshOverView(KuroOverView kuroOverView);

    interface KuroRefreshListener{
        void onRefresh();
        boolean enableRefresh();
    }
}
