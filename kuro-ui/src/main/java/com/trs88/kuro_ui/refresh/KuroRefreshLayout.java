package com.trs88.kuro_ui.refresh;

import android.content.Context;
import android.content.IntentFilter;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trs88.kurolibrary.log.KuroLog;
import com.trs88.kurolibrary.util.KuroScrollUtil;

public class KuroRefreshLayout extends FrameLayout implements KuroRefresh {
    private static final String TAG = KuroRefreshLayout.class.getSimpleName();
    private KuroRefreshState mState;
    private GestureDetector mGestureDetector;
    private KuroRefreshListener mKuroRefreshListener;
    protected KuroOverView mKuroOverView;
    private AutoScroller mAutoScroller;

    private int mLastY;

    //刷新时是否禁止滚动
    private boolean disableRefreshScroll;

    public KuroRefreshLayout(@NonNull Context context) {
        this(context, null);
    }

    public KuroRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KuroRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
//        KuroLog.i(TAG,"init KuroRefreshLayout");
        mGestureDetector = new GestureDetector(getContext(), kuroGestureDetector);
        mAutoScroller = new AutoScroller();
        //默认初始化KuroTextOverView
        setRefreshOverView(new KuroTextOverView(context));
    }

    @Override
    public void setDisableRefreshScroll(boolean disableRefreshScroll) {
        this.disableRefreshScroll = disableRefreshScroll;
    }

    @Override
    public void refreshFinished() {
        View head = getChildAt(0);
//        KuroLog.i(TAG, "refreshFinished head-bottom:" + head.getBottom());
        mKuroOverView.onFinish();
        mKuroOverView.setState(KuroRefreshState.STATE_INIT);
        int bottom = head.getBottom();
        if (bottom > 0) {
            recover(bottom);
        }
        mState = KuroRefreshState.STATE_INIT;
    }

    @Override
    public void setRefreshListener(KuroRefreshListener kuroRefreshListener) {
        this.mKuroRefreshListener = kuroRefreshListener;
    }

    /**
     * 设置下拉刷新的视图
     * @param kuroOverView 下拉刷新的视图
     */
    @Override
    public void setRefreshOverView(KuroOverView kuroOverView) {
        if (this.mKuroOverView != null) {
            removeView(mKuroOverView);
        }
        this.mKuroOverView = kuroOverView;

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mKuroOverView, 0, params);
    }


    KuroGestureDetector kuroGestureDetector = new KuroGestureDetector() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY) || mKuroRefreshListener != null && !mKuroRefreshListener.enableRefresh()) {
                //横向滑动，或刷新禁止则不处理
                return false;
            }

            if (disableRefreshScroll && mState == KuroRefreshState.STATE_REFRESH) {
                //刷新时是否禁止滚动
                return true;
            }

            View head = getChildAt(0);
            View child = KuroScrollUtil.findScrollableChild(KuroRefreshLayout.this);
            if (KuroScrollUtil.childScrolled(child)) {
                //如果列表发生了滚动则不处理
                return false;
            }
            //没有刷新或者没有达到可以刷新的距离，且头部已经划出或者下拉
            if ((mState != KuroRefreshState.STATE_REFRESH || head.getBottom() <= mKuroOverView.mPullRefreshHeight) && (head.getBottom() > 0|| distanceY <= 0.0F)) {
                //还在滑动中
                if (mState != KuroRefreshState.STATE_OVER_RELEASE) {
                    int seed;
                    //根据阻尼计算速度
                    if (child.getTop() < mKuroOverView.mPullRefreshHeight) {
                        seed = (int) (mLastY / mKuroOverView.minDamp);
                    } else {
                        seed = (int) (mLastY / mKuroOverView.maxDamp);
                    }
                    //如果是正在刷新状态，则不允许在滑动中改变状态
                    boolean bool = moveDown(seed, true);
                    mLastY = (int) -distanceY;
                    return bool;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //事件分发处理
        if (!mAutoScroller.isIsFinished()){
            return false;
        }

        View head = getChildAt(0);
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_POINTER_INDEX_MASK) {
            //松开手
            if (head.getBottom() > 0) {
                if (mState != KuroRefreshState.STATE_REFRESH) {
                    //还没开始刷新
                    recover(head.getBottom());
                    return false;
                }
            }

            mLastY = 0;
        }

        boolean consumed = mGestureDetector.onTouchEvent(ev);

        if ((consumed || (mState != KuroRefreshState.STATE_INIT && mState != KuroRefreshState.STATE_REFRESH)) && head.getBottom() != 0) {
            ev.setAction(MotionEvent.ACTION_CANCEL);//让父类接受不到真实的事件
            return super.dispatchTouchEvent(ev);
        }

        if (consumed) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //定义head与child的排列位置
        View head = getChildAt(0);
        View child = getChildAt(1);
        if (head != null && child != null) {
            int childTop = child.getTop();
//            KuroLog.i(TAG,"onLayout head-height:" + head.getMeasuredHeight());
            if (mState == KuroRefreshState.STATE_REFRESH) {
                head.layout(0, mKuroOverView.mPullRefreshHeight - head.getMeasuredHeight(), right, mKuroOverView.mPullRefreshHeight);
                child.layout(0, mKuroOverView.mPullRefreshHeight, right, mKuroOverView.mPullRefreshHeight + child.getMeasuredHeight());
            } else {
                head.layout(0, childTop - head.getMeasuredHeight(), right, childTop);
                child.layout(0, childTop, right, childTop + child.getMeasuredHeight());
            }

            //其他视图保持原有排列
            View other;
            for (int i = 2; i < getChildCount(); i++) {
                other = getChildAt(i);
                other.layout(0, top, right, bottom);
            }
//            KuroLog.i(TAG, "onLayout head-bottom:" + head.getBottom());
        }
    }

    private void recover(int dis) {
        if (mKuroRefreshListener != null && dis > mKuroOverView.mPullRefreshHeight) {
            //滚动到指定位置
            mAutoScroller.recover(dis - mKuroOverView.mPullRefreshHeight);
            mState = KuroRefreshState.STATE_OVER_RELEASE;
        } else {
            mAutoScroller.recover(dis);
        }
    }

    /**
     * 根据偏移量移动header与child
     *
     * @param offsetY 偏移量
     * @param nonAuto 是否非自动滚动触发
     * @return
     */
    private boolean moveDown(int offsetY, boolean nonAuto) {
//        KuroLog.i(TAG, "changeState:" + nonAuto);
        View head = getChildAt(0);
        View child = getChildAt(1);
        int childTop = child.getTop() + offsetY;
//        KuroLog.i(TAG,"-----", "moveDown head-bottom:" + head.getBottom() + ",child.getTop():" + child.getTop() + ",offsetY:" + offsetY);
        if (childTop <= 0) {
//            KuroLog.i(TAG, "childTop<=0,mState" + mState);
            //异常情况的补充
            offsetY = -child.getTop();
            //移动head与child的位置到原始位置
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (mState != KuroRefreshState.STATE_REFRESH) {
                mState = KuroRefreshState.STATE_INIT;
            }
        } else if (mState == KuroRefreshState.STATE_REFRESH && childTop > mKuroOverView.mPullRefreshHeight) {
            //如果正在下拉刷新中，禁止继续下拉
            return false;
        } else if (childTop <= mKuroOverView.mPullRefreshHeight) {
            //还没有超过设定的刷新距离
            if (mKuroOverView.getState() != KuroRefreshState.STATE_VISIBLE && nonAuto) {
                //头部开始显示
                mKuroOverView.onVisible();
                mKuroOverView.setState(KuroRefreshState.STATE_VISIBLE);
                mState = KuroRefreshState.STATE_VISIBLE;
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
            if (childTop == mKuroOverView.mPullRefreshHeight && mState == KuroRefreshState.STATE_OVER_RELEASE) {
//                KuroLog.i(TAG, "refresh，childTop：" + childTop);
                // 下拉刷新完成
                refresh();
            }
        } else {
            if (mKuroOverView.getState() != KuroRefreshState.STATE_OVER && nonAuto) {
                //超出刷新位置
                mKuroOverView.onOver();
                mKuroOverView.setState(KuroRefreshState.STATE_OVER);
            }
            head.offsetTopAndBottom(offsetY);
            child.offsetTopAndBottom(offsetY);
        }

        if (mKuroOverView != null) {
            mKuroOverView.onScroll(head.getBottom(), mKuroOverView.mPullRefreshHeight);
        }
        return true;
    }

    /**
     * 开始刷新
     */
    private void refresh() {
        if (mKuroRefreshListener != null) {
            mState = KuroRefreshState.STATE_REFRESH;
            mKuroOverView.onRefresh();
            mKuroOverView.setState(KuroRefreshState.STATE_REFRESH);
            mKuroRefreshListener.onRefresh();
        }
    }


    /**
     * 借助Scroller实现视图的自动滚动
     * https://juejin.im/post/5c7f4f0351882562ed516ab6
     */
    private class AutoScroller implements Runnable {
        private Scroller mScroller;
        private int mLastY;
        private boolean mIsFinished;

        public AutoScroller() {
            mScroller = new Scroller(getContext(), new LinearInterpolator());
            mIsFinished = true;
        }

        @Override
        public void run() {
            if (mScroller.computeScrollOffset()) {
                //还未滚动完成
                moveDown(mLastY - mScroller.getCurrY(), false);
                mLastY = mScroller.getCurrY();
                post(this);
            } else {
                removeCallbacks(this);
                mIsFinished = true;
            }
        }

        void recover(int dis) {
            if (dis <= 0) {
                return;
            }
            removeCallbacks(this);
            mLastY = 0;
            mIsFinished = false;
            mScroller.startScroll(0, 0, 0, dis, 300);
            post(this);
        }

        boolean isIsFinished() {
            return mIsFinished;
        }
    }
}
