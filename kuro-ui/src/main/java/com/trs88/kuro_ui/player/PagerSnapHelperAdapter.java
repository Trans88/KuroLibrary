package com.trs88.kuro_ui.player;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trs88.kuro_ui.R;

import java.util.List;

public class PagerSnapHelperAdapter extends RecyclerView.Adapter<PagerSnapHelperAdapter.ViewHolder> {
    //数据集
    private List<PlayMod> mDataList;

    private int mWidth;
    private int mHeight;
    private Context mContext;

    public PagerSnapHelperAdapter(List<PlayMod> dataList, int width, int height) {
        super();
        this.mDataList = dataList;
        this.mWidth = width;
        this.mHeight = height;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_pager_item, viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
//        viewHolder.itemView.setTag(position);
        int realPosition = getRealPosition(position);
        viewHolder.mTextView.setText(realPosition+" ");
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }



    static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mTextView;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView =itemView.findViewById(R.id.tv_test);
        }
    }

    private int getRealPosition(int position){
        return position % mDataList.size();
    }
}
