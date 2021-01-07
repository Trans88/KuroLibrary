package com.trs88.kurolibrary.log;

import android.util.Log;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class KuroLogType {
    @IntDef({V,D,I,W,E,A})
    //注解的保留时期在源码级别
    @Retention(RetentionPolicy.SOURCE)
    public @interface  TYPE{}
    public final static int V = Log.VERBOSE;
    public final static int D = Log.DEBUG;
    public final static int I = Log.INFO;
    public final static int W = Log.WARN;
    public final static int E = Log.ERROR;
    public final static int A = Log.ASSERT;
}
