package com.trs88.kuro_ui.player;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class KuroPlayer extends RecyclerView {
    private Context mContext;
    public KuroPlayer(@NonNull Context context) {
        this(context,null);
    }

    public KuroPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public KuroPlayer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new LinearLayoutManager(mContext));
        PagerSnapHelper snapHelper =new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);
    }

    public void setPageAdapter(PagerSnapHelperAdapter adapter){
        setAdapter(adapter);
    }

    public void next(){

//        scrollToPosition();
    }

}
