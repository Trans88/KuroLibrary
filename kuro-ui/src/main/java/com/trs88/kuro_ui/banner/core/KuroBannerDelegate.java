package com.trs88.kuro_ui.banner.core;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.trs88.kuro_ui.R;
import com.trs88.kuro_ui.banner.indicator.KuroCircleIndicator;
import com.trs88.kuro_ui.banner.indicator.KuroIndicator;

import java.util.List;

/**
 * KuroBanner的控制器
 * 辅助KuroBanner完成各种功能控制
 * 将KuroBanner的逻辑内聚在这，保证暴露给使用者的KuroBanner干净整洁
 */

public class KuroBannerDelegate implements IKuroBanner, ViewPager.OnPageChangeListener {
    private Context mContext;
    private KuroBanner mKuroBanner;

    private KuroBannerAdapter mAdapter;
    private KuroIndicator<?> mKuroIndicator;
    private boolean mAutoPlay;
    private boolean mLoop;
    private List<? extends KuroBannerMo> mKuroBannerMos;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private int mIntervalTime = 5000;
    private KuroBanner.OnBannerClickListener mOnBannerClickListener;
    private KuroViewPager mKuroViewPager;
    private int mScrollDuration = -1;

    public KuroBannerDelegate(Context context, KuroBanner kuroBanner) {
        mContext = context;
        mKuroBanner = kuroBanner;
    }

    @Override
    public void setBannerData(int layoutResId, @NonNull List<? extends KuroBannerMo> models) {
        mKuroBannerMos = models;
        init(layoutResId);
    }


    @Override
    public void setBannerData(@NonNull List<? extends KuroBannerMo> models) {
//        setBannerData(R.layout.kuro_banner_item_image, models);
        setBannerData(R.layout.kuro_banner_item, models);
    }

    @Override
    public void setKuroIndicator(KuroIndicator<?> kuroIndicator) {
        this.mKuroIndicator = kuroIndicator;
    }

    @Override
    public void setAutoPlay(boolean autoPlay) {
        this.mAutoPlay = autoPlay;
        if (mAdapter != null) {
            mAdapter.setAutoPlay(autoPlay);
        }
        if (mKuroViewPager != null) {
            mKuroViewPager.setAutoPlay(autoPlay);
        }

    }

    @Override
    public void setLoop(boolean loop) {
        this.mLoop = loop;
    }

    @Override
    public void setIntervalTime(int intervalTime) {
        if (intervalTime > 0) {
            this.mIntervalTime = intervalTime;
        }
    }

    @Override
    public void setBindAdapter(IBindAdapter bindAdapter) {
        mAdapter.setBindAdapter(bindAdapter);
    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.mOnPageChangeListener = onPageChangeListener;
    }

    @Override
    public void setOnBannerClickListener(OnBannerClickListener onBannerClickListener) {
        this.mOnBannerClickListener = onBannerClickListener;
    }

    @Override
    public void setScrollDuration(int duration) {
        this.mScrollDuration = duration;
        if (mKuroViewPager != null && duration > 0) {
            mKuroViewPager.setScrollDuration(duration);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mOnPageChangeListener != null && mAdapter.getRealCount() != 0) {
            mOnPageChangeListener.onPageScrolled(position % mAdapter.getRealCount(), positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        if (mAdapter.getRealCount() == 0) {
            return;
        }
        position = position % mAdapter.getRealCount();
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }

        if (mKuroIndicator != null) {
            mKuroIndicator.onPointChange(position, mAdapter.getRealCount());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
    }

    private void init(int layoutResId) {
        if (mAdapter == null) {
            mAdapter = new KuroBannerAdapter(mContext);
        }

        if (mKuroIndicator == null) {
            mKuroIndicator = new KuroCircleIndicator(mContext);
        }

        mKuroIndicator.onInflate(mKuroBannerMos.size());
        mAdapter.setLayoutResId(layoutResId);
        mAdapter.setBannerData(mKuroBannerMos);
        mAdapter.setAutoPlay(mAutoPlay);
        mAdapter.setLoop(mLoop);
        mAdapter.setOnBannerClickListener(mOnBannerClickListener);

        mKuroViewPager = new KuroViewPager(mContext);
        mKuroViewPager.setIntervalTime(mIntervalTime);
        mKuroViewPager.addOnPageChangeListener(this);
        mKuroViewPager.setAutoPlay(mAutoPlay);
        mKuroViewPager.setAdapter(mAdapter);

        if (mScrollDuration > 0) {
            mKuroViewPager.setScrollDuration(mScrollDuration);
        }
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        if ((mLoop || mAutoPlay) && mAdapter.getRealCount() != 0) {
            //设置无限轮播，使第一张能反向滑动到最后一张，已达到无限滚动的效果
            int firstItem = mAdapter.getFirstItem();
            mKuroViewPager.setCurrentItem(firstItem, false);

        }

        //清楚缓存的View
        mKuroBanner.removeAllViews();
        mKuroBanner.addView(mKuroViewPager, layoutParams);
        mKuroBanner.addView(mKuroIndicator.get(), layoutParams);
    }


}
