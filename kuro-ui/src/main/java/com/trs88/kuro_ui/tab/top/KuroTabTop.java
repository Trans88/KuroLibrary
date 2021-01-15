package com.trs88.kuro_ui.tab.top;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trs88.kuro_ui.R;
import com.trs88.kuro_ui.tab.common.IKuroTab;

/**
 * 顶部导航栏单个Item
 */
public class KuroTabTop extends RelativeLayout implements IKuroTab<KuroTabTopInfo<?>> {
    private KuroTabTopInfo<?> tabInfo;
    private ImageView tabImageView;
    private TextView tabNameView;

    private View indicator;

    public KuroTabTop(Context context) {
        this(context, null);
    }

    public KuroTabTop(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KuroTabTop(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化item
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.kuro_tab_top, this);
        tabImageView = findViewById(R.id.iv_image);
        tabNameView = findViewById(R.id.tv_name);
        indicator =findViewById(R.id.tab_top_indicator);
    }

    @Override
    public void setKuroTabInfo(@NonNull KuroTabTopInfo<?> data) {
        this.tabInfo = data;
        inflateInfo(false, true);
    }

    public KuroTabTopInfo<?> getKuroTabInfo() {
        return tabInfo;
    }

    public ImageView getTabImageView() {
        return tabImageView;
    }

    public TextView getTabNameView() {
        return tabNameView;
    }

    /**
     * 改变某个tab的高度
     *
     * @param height
     */
    @Override
    public void resetHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height;
        setLayoutParams(layoutParams);
        getTabNameView().setVisibility(GONE);
    }

    private void inflateInfo(boolean selected, boolean init) {
        if (tabInfo.tabType == KuroTabTopInfo.TabType.TEXT) {
            if (init) {
                tabImageView.setVisibility(GONE);
                tabNameView.setVisibility(VISIBLE);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }

            if (selected) {
                indicator.setVisibility(VISIBLE);
                tabNameView.setTextColor(getTextColor(tabInfo.tintColor));
            } else {
                indicator.setVisibility(GONE);
                tabNameView.setTextColor(getTextColor(tabInfo.defaultColor));
            }
        } else if (tabInfo.tabType == KuroTabTopInfo.TabType.BITMAP) {
            if (init) {
                tabImageView.setVisibility(VISIBLE);
                tabNameView.setVisibility(GONE);
            }

            if (selected) {
                tabImageView.setImageBitmap(tabInfo.selectedBitmap);
            } else {
                tabImageView.setImageBitmap(tabInfo.defaultBitmap);
            }
        }
    }

    @Override
    public void OnTabSelectedChange(int index, @Nullable KuroTabTopInfo<?> prevInfo, @NonNull KuroTabTopInfo<?> nextInfo) {
        if (prevInfo != tabInfo && nextInfo != tabInfo || prevInfo == nextInfo) {
            return;
        }

        if (prevInfo == tabInfo) {
            inflateInfo(false, false);
        } else {
            inflateInfo(true, false);
        }
    }

    @ColorInt
    private int getTextColor(Object color) {
        if (color instanceof String) {
            return Color.parseColor((String) color);
        } else {
            return (int) color;
        }
    }



}
