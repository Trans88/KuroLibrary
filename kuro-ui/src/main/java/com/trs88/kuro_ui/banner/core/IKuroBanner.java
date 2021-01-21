package com.trs88.kuro_ui.banner.core;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.trs88.kuro_ui.banner.indicator.KuroIndicator;

import java.util.List;

public interface IKuroBanner {
    void setBannerData(@LayoutRes int layoutResId, @NonNull List<? extends KuroBannerMo> models);

    void setBannerData(@NonNull List<? extends KuroBannerMo> models);

    void setKuroIndicator(KuroIndicator<?> kuroIndicator);

    void setAutoPlay(boolean autoPlay);

    void setLoop(boolean loop);

    void setIntervalTime(int intervalTime);

    void setBindAdapter(IBindAdapter bindAdapter);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener);

    void setOnBannerClickListener(OnBannerClickListener onBannerClickListener);

    void setScrollDuration(int duration);

    interface OnBannerClickListener {
        void onBannerClick(@NonNull KuroBannerAdapter.KuroBannerViewHolder viewHolder, @NonNull KuroBannerMo bannerMo,int position);
    }
}
