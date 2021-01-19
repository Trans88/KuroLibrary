package com.trs88.kuro_ui.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trs88.kurolibrary.log.KuroLog;
import com.trs88.kurolibrary.util.KuroDisplayUtil;

/**
 * 下拉刷新的Overlay视图，可以重载这个类来定义自己的Overlay
 */
public abstract class KuroOverView extends FrameLayout {
    private static final String TAG = KuroOverView.class.getSimpleName();

    protected  KuroRefreshState mState=KuroRefreshState.STATE_INIT;

    /**
     * 触发下拉刷新需要的最小高度
     */
    public  int mPullRefreshHeight;
    /**
     * 最小阻尼
     */
    public float minDamp =1.6f;
    /**
     * 最大阻尼
     */
    public float maxDamp =2.2f;
    public KuroOverView(@NonNull Context context) {
        this(context,null);
    }

    public KuroOverView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public KuroOverView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preInit();
    }

    protected  void preInit(){
        mPullRefreshHeight = KuroDisplayUtil.dp2px(66,getResources());
        init();
    }

    /**
     * 初始化
     */
    public abstract void init();

    protected abstract void onScroll(int scrollY,int pullRefreshHeight);

    /**
     * 显示Overlay
     */
    protected abstract void onVisible();

    /**
     * 超过Overlay,释放就会加载
     */
    public abstract void onOver();

    /**
     * 开始刷新
     */
    public abstract void onRefresh();

    /**
     * 刷新完成
     */
    public abstract void onFinish();


    /**
     * 设置下拉刷新状态
     * @param state 状态
     */
    public void setState(KuroRefreshState state){
        this.mState =state;
    }

    /**
     * 获取状态
     * @return 状态
     */
    public KuroRefreshState getState() {
        return mState;
    }
}
