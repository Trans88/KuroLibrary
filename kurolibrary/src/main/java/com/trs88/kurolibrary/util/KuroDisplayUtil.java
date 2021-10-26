package com.trs88.kurolibrary.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * 显示内容的工具类 包括dp2px 获取屏幕的宽高
 */
public class KuroDisplayUtil {
    public static int dp2px(float dp, Resources resources){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,resources.getDisplayMetrics());
    }

    public static int getDisplayWidthInPx(@NonNull Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm!=null){
            Display display =wm.getDefaultDisplay();
            Point size =new Point();
            display.getSize(size);
            return size.x;
        }
        return 0;
    }

    public static int getDisplayHeightInPx(@NonNull Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm!=null){
            Display display =wm.getDefaultDisplay();
            Point size =new Point();
            display.getSize(size);
            return size.y;
        }
        return 0;
    }
}
