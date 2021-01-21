package com.trs88.kuro_ui.banner.core;

import android.content.Context;
import android.widget.Scroller;

public class KuroBannerScroller extends Scroller {

    /**
     * 滚动时间，值越大滚动越慢
     */
    private int mDuration =1000;

    public KuroBannerScroller(Context context, int duration) {
        super(context);
        mDuration =duration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy,mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mDuration);
    }
}
