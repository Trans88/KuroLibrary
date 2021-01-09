package com.trs88.kurolibrary.log;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.trs88.kurolibrary.util.KuroDisplayUtil;

public class KuroViewPrinterProvider {
    private FrameLayout rootView;
    private View floatingView;
    private boolean isOpen;
    private FrameLayout logView;
    private RecyclerView recyclerView;

    private static final String TAG_FLOATING_VIEW ="TAG_FLOATING_VIEW";
    private static final String TAG_LOG_VIEW ="TAG_LOG_VIEW";

    public KuroViewPrinterProvider(FrameLayout rootView, RecyclerView recyclerView) {
        this.rootView = rootView;
        this.recyclerView = recyclerView;
    }

    /**
     * 展示Log 悬浮按钮
     */
    public void showFloatingView(){
        if (rootView.findViewWithTag(TAG_FLOATING_VIEW)!=null){
            return;
        }

        FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM|Gravity.END;
        View floatingView =genFloatingView();
        floatingView.setTag(TAG_FLOATING_VIEW);
        floatingView.setBackgroundColor(Color.BLACK);
        floatingView.setAlpha(0.8f);
        params.bottomMargin =KuroDisplayUtil.dp2px(100,recyclerView.getResources());
        rootView.addView(genFloatingView(),params);
    }
    /**
     * 关闭Log 悬浮按钮
     */
    public void closeFloatingView(){
        rootView.removeView(genFloatingView());
    }

    private View genFloatingView() {
        if (floatingView !=null){
            return floatingView;
        }

        TextView textView =new TextView(rootView.getContext());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isOpen){
                    showLogView();
                }
            }
        });
        textView.setText("KuroLog");
        return floatingView =textView;
    }

    private void showLogView() {
        if (rootView.findViewWithTag(TAG_LOG_VIEW)!=null){
            return;
        }

        FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, KuroDisplayUtil.dp2px(160,rootView.getResources()));
        params.gravity = Gravity.BOTTOM;

        View logView =genLogView();
        logView.setTag(TAG_LOG_VIEW);
        rootView.addView(genLogView(),params);
        isOpen =true;
    }

    private View genLogView() {
        if (logView!=null){
            return logView;
        }

        FrameLayout logView =new FrameLayout(rootView.getContext());
        logView.setBackgroundColor(Color.BLACK);
        logView.addView(recyclerView);
        FrameLayout.LayoutParams params =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity=Gravity.END;
        TextView closeView =new TextView(rootView.getContext());
        closeView.setText("close");
        closeView.setTextColor(Color.WHITE);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeLogView();
            }
        });
        logView.addView(closeView,params);

        FrameLayout.LayoutParams clearParams =new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        clearParams.gravity=Gravity.START;
        TextView clearView =new TextView(rootView.getContext());
        clearView.setText("clear");
        clearView.setTextColor(Color.WHITE);
        clearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLog();
            }
        });
        logView.addView(clearView,clearParams);

        return this.logView =logView;
    }

    private void clearLog() {
//        View logView = rootView.findViewWithTag(TAG_LOG_VIEW);
    }

    private void closeLogView() {
        isOpen =false;
        rootView.removeView(genLogView());
    }
}
