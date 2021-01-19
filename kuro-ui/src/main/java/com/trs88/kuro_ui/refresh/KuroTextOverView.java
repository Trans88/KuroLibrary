package com.trs88.kuro_ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trs88.kuro_ui.R;
import com.trs88.kurolibrary.util.KuroDisplayUtil;

public class KuroTextOverView extends KuroOverView{
    private TextView mText;
    private View mRotateView;

    public KuroTextOverView(@NonNull Context context) {
        super(context);
    }

    public KuroTextOverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public KuroTextOverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.kuro_refresh_overview,this,true);
        mText=findViewById(R.id.text);
        mRotateView =findViewById(R.id.iv_rotate);
        mPullRefreshHeight = KuroDisplayUtil.dp2px(90,getResources());
    }

    @Override
    protected void onScroll(int scrollY, int pullRefreshHeight) {

    }

    @Override
    protected void onVisible() {
        mText.setText("下拉刷新");
    }

    @Override
    public void onOver() {
        mText.setText("松开刷新");
    }

    @Override
    public void onRefresh() {
        mText.setText("正在刷新...");
        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(),R.anim.rotate_anim);
        LinearInterpolator lin =new LinearInterpolator();
        operatingAnim .setInterpolator(lin);
        mRotateView.startAnimation(operatingAnim);
    }

    @Override
    public void onFinish() {
        mRotateView.clearAnimation();
    }
}
