package com.trs88.kurolibrary.tab.bottom;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.trs88.kurolibrary.R;
import com.trs88.kurolibrary.tab.common.IKuroTab;

/**
 * 底部导航栏单个Item
 */
public class KuroTabBottom extends RelativeLayout implements IKuroTab<KuroTabBottomInfo<?>> {
    private KuroTabBottomInfo<?> tabInfo;
    private ImageView tabImageView;
    private TextView tabIconView;
    private TextView tabNameView;

    public KuroTabBottom(Context context) {
        this(context, null);
    }

    public KuroTabBottom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KuroTabBottom(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化item
     */
    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.kuro_tab_bottom, this);
        tabImageView = findViewById(R.id.iv_image);
        tabIconView = findViewById(R.id.tv_icon);
        tabNameView = findViewById(R.id.tv_name);
    }

    @Override
    public void setKuroTabInfo(@NonNull KuroTabBottomInfo<?> data) {
        this.tabInfo = data;
        inflateInfo(false, true);
    }

    public KuroTabBottomInfo<?> getKuroTabInfo() {
        return tabInfo;
    }

    public ImageView getTabImageView() {
        return tabImageView;
    }

    public TextView getTabIconView() {
        return tabIconView;
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
        if (tabInfo.tabType == KuroTabBottomInfo.TabType.ICON) {
            if (init) {
                tabImageView.setVisibility(GONE);
                tabIconView.setVisibility(VISIBLE);
                Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), tabInfo.iconFont);//设置字体
                tabIconView.setTypeface(typeface);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }

            if (selected) {
                tabIconView.setText(TextUtils.isEmpty(tabInfo.selectedIconName) ? tabInfo.defaultIconName : tabInfo.selectedIconName);
                tabNameView.setTextColor(getTextColor(tabInfo.tintColor));
                tabIconView.setTextColor(getTextColor(tabInfo.tintColor));
            } else {
                tabIconView.setText(tabInfo.defaultIconName);
                tabNameView.setTextColor(getTextColor(tabInfo.defaultColor));
                tabIconView.setTextColor(getTextColor(tabInfo.defaultColor));
            }
        } else if (tabInfo.tabType == KuroTabBottomInfo.TabType.BITMAP) {
            if (init) {
                tabImageView.setVisibility(VISIBLE);
                tabIconView.setVisibility(GONE);
                if (!TextUtils.isEmpty(tabInfo.name)) {
                    tabNameView.setText(tabInfo.name);
                }
            }

            if (selected) {
                tabImageView.setImageBitmap(tabInfo.selectedBitmap);
            } else {
                tabImageView.setImageBitmap(tabInfo.defaultBitmap);
            }
        }
    }

    @Override
    public void OnTabSelectedChange(int index, @Nullable KuroTabBottomInfo<?> prevInfo, @NonNull KuroTabBottomInfo<?> nextInfo) {
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
