package com.trs88.kuro_ui.banner.core;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.logging.LogRecord;

/**
 * 实现自动翻页的ViewPager
 */
public class KuroViewPager extends ViewPager {
    private int mIntervalTime;
    /**
     * 是否自动播放
     */
    private boolean mAutoPlay = true;
    private boolean isLayout;

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            //切换到下一个
            next();
            mHandler.postDelayed(this, mIntervalTime);
        }
    };


    public KuroViewPager(@NonNull Context context) {
        super(context);
    }

    public KuroViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if (!mAutoPlay) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                start();
                break;
            default:
                stop();
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 通过反射来设置ViewPager的滚动速度
     * @param duration
     */
    public void setScrollDuration(int duration){
        try {
            Field scrollerField =ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this,new KuroBannerScroller(getContext(),duration));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setIntervalTime(int intervalTime) {
        this.mIntervalTime = intervalTime;
    }

    public void start() {
        mHandler.removeCallbacksAndMessages(null);
        if (mAutoPlay){
            mHandler.postDelayed(mRunnable,mIntervalTime);
        }
    }

    public void stop() {
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        isLayout =true;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决ViewPager在RecycleView滚动时切换卡住bug
        if (isLayout&&getAdapter()!=null&&getAdapter().getCount()>0){
            try {

                Field mScroller =ViewPager.class.getDeclaredField("mFirstLayout");
                mScroller.setAccessible(true);
                mScroller.set(this,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        //解决ViewPager在RecycleView滚动时切换卡住bug
        if (((Activity)getContext()).isFinishing()){
            super.onDetachedFromWindow();
        }
        stop();
    }

    /**
     * 设置下一个要显示的item,并返回item的pos
     *
     * @reture 下一个要显示item的pos
     */
    private int next() {
        int nextPosition = -1;
        if (getAdapter() == null || getAdapter().getCount() <= 1) {
            stop();
            return nextPosition;
        }
        nextPosition = getCurrentItem() + 1;
        //下一个索引大于adapter的view的最大数量时重新开始
        if (nextPosition >= getAdapter().getCount()) {
            //获取第一个item的索引
            nextPosition =((KuroBannerAdapter)getAdapter()).getFirstItem();
        }
        setCurrentItem(nextPosition, true);
        return nextPosition;
    }


}
