package com.trs88.kuro_ui.banner.core;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.trs88.kuro_ui.R;
import com.trs88.kuro_ui.banner.indicator.KuroIndicator;

import java.util.List;

public class KuroBanner extends FrameLayout implements IKuroBanner {
    private KuroBannerDelegate  delegate;

    public KuroBanner(@NonNull Context context) {
        this(context,null);
    }

    public KuroBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public KuroBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        delegate =new KuroBannerDelegate(context,this);
        initCustomAttrs(context,attrs);
    }

    private void initCustomAttrs(Context context,AttributeSet attrs){
        TypedArray typedArray =context.obtainStyledAttributes(attrs, R.styleable.KuroBanner);
        boolean autoPlay =typedArray.getBoolean(R.styleable.KuroBanner_autoPlay,true);
        boolean loop =typedArray.getBoolean(R.styleable.KuroBanner_loop,true);
        int intervalTime =typedArray.getInteger(R.styleable.KuroBanner_intervalTime,-1);
        setAutoPlay(autoPlay);
        setLoop(loop);
        setIntervalTime(intervalTime);
        typedArray.recycle();
    }

    @Override
    public void setBannerData(int layoutResId, @NonNull List<? extends KuroBannerMo> models) {
        delegate.setBannerData(layoutResId,models);
    }

    @Override
    public void setBannerData(@NonNull List<? extends KuroBannerMo> models) {
        delegate.setBannerData(models);
    }

    @Override
    public void setKuroIndicator(KuroIndicator<?> kuroIndicator) {
        delegate.setKuroIndicator(kuroIndicator);
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        delegate.setAutoPlay(autoPlay);
    }

    @Override
    public void setLoop(boolean loop) {
        delegate.setLoop(loop);
    }

    @Override
    public void setIntervalTime(int intervalTime) {
        delegate.setIntervalTime(intervalTime);
    }

    @Override
    public void setBindAdapter(IBindAdapter bindAdapter) {
        delegate.setBindAdapter(bindAdapter);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        delegate.setOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        delegate.setOnBannerClickListener(onBannerClickListener);
    }

    @Override
    public void setScrollDuration(int duration) {
        delegate.setScrollDuration(duration);
    }
}
