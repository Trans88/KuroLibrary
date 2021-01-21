package com.trs88.kuro_ui.banner.core;

/**
 * KuroBanner的数据绑定接口，基于该接口可以实现数据的绑定和框架层的解耦
 */
public interface IBindAdapter {
    void onBind(KuroBannerAdapter.KuroBannerViewHolder viewHolder,KuroBannerMo mo,int position);
}
