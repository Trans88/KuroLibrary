package com.trs88.kurolibrary.log;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trs88.kurolibrary.R;

import java.util.ArrayList;
import java.util.List;

public class KuroViewPrinter implements KuroLogPrinter {
    private RecyclerView recyclerView;
    private LogAdapter adapter;
    private KuroViewPrinterProvider viewProvider;

    public KuroViewPrinter(Activity activity) {
        FrameLayout rootView = activity.findViewById(android.R.id.content);
        recyclerView =new RecyclerView(activity);
        adapter =new LogAdapter(LayoutInflater.from(recyclerView.getContext()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        viewProvider =new KuroViewPrinterProvider(rootView,recyclerView);
    }

    /**
     * 获取ViewProvider,通过ViewProvider可以控制log视图的展示和隐藏
     * @return viewProvider
     */
    @NonNull
    public KuroViewPrinterProvider getViewProvider(){
        return viewProvider;
    }

    @Override
    public void print(@NonNull KuroLogConfig config, int level, String tag, @NonNull String printString) {
        //将log展示添加到recycleView
        adapter.addItem(new KuroLogModel(System.currentTimeMillis(),level,tag,printString));
        //滚动到对应位置
        recyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
    }

    private static class LogAdapter extends RecyclerView.Adapter<LogViewHolder>{
        private LayoutInflater inflater;
        private List<KuroLogModel> logs =new ArrayList<>();
        public LogAdapter(LayoutInflater inflater){
            this.inflater =inflater;
        }

        void  addItem(KuroLogModel kuroLogModel){
            logs.add(kuroLogModel);
            notifyItemInserted(logs.size()-1);
        }

        @NonNull
        @Override
        public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.kurolog_item, parent, false);
            return new LogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
            KuroLogModel logItem =logs.get(position);
            int color =getHighLightColor(logItem.level);
            holder.tagView.setTextColor(color);
            holder.messageView.setTextColor(color);

            holder.tagView.setText(logItem.getFlattened());
            holder.messageView.setText(logItem.log);
        }

        @Override
        public int getItemCount() {
            return logs.size();
        }

        /**
         * 根据log级别获取不同的高亮颜色
         * @param logLevel log 级别
         * @return 颜色
         */
        private int getHighLightColor(int logLevel){
            int highLight ;
            switch (logLevel){
                case KuroLogType.V:
                    highLight =0xffbbbbbb;
                    break;
                case KuroLogType.D:
                    highLight =0xffffffff;
                    break;
                case KuroLogType.I:
                    highLight =0xff6a8759;
                    break;
                case KuroLogType.W:
                    highLight =0xffbbb529;
                    break;
                case KuroLogType.E:
                    highLight =0xffff6b68;
                    break;
                default:
                    highLight =0xffffff00;
                    break;

            }
            return highLight;
        }
    }

    private static class LogViewHolder extends RecyclerView.ViewHolder{
        TextView tagView;
        TextView messageView;
        public LogViewHolder(@NonNull View itemView){
            super(itemView);
            tagView =itemView.findViewById(R.id.tag);
            messageView =itemView.findViewById(R.id.message);
        }
    }
}
