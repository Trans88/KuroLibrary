package com.trs88.kurolibrary.log;

import android.util.Log;

import androidx.annotation.NonNull;

import static com.trs88.kurolibrary.log.KuroLogConfig.MAX_LEN;

/**
 * 控制台打印器
 */
public class KuroConsolePrinter implements KuroLogPrinter {
    @Override
    public void print(@NonNull KuroLogConfig config, int level, String tag, @NonNull String printString) {
        int len =printString.length();
        int countOfSub =len/MAX_LEN;

        if (countOfSub>0){
            int index =0;
            for (int i =0;i<countOfSub;i++){
                Log.println(level,tag,printString.substring(index,index+MAX_LEN));
                index +=MAX_LEN;
            }
            //在没有整除的情况下把剩余的打印出来
            if (index !=len){
                Log.println(level,tag,printString.substring(index,len));
            }
        }else {
            //不足一行的时候将所有信息打印出来
            Log.println(level,tag,printString);
        }
    }
}
