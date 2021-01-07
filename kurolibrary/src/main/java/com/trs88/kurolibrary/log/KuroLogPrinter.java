package com.trs88.kurolibrary.log;

import androidx.annotation.NonNull;

public interface KuroLogPrinter {
    void print(@NonNull KuroLogConfig config,int level,String tag,@NonNull String printString);
}
