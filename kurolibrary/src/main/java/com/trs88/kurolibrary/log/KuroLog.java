package com.trs88.kurolibrary.log;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

/**
 * 1、打印堆栈信息
 * 2、File输出
 * 3、模拟控制台
 */
public class KuroLog {
    private static final String KURO_LOG_PACKAGE;

    static {
        String className =KuroLog.class.getName();
        KURO_LOG_PACKAGE =className.substring(0,className.lastIndexOf('.')+1);
    }

    public static void v(Object...contents){
        log(KuroLogType.V,contents);
    }

    public static void vt(String tag,Object...contents){
        log(KuroLogType.V,tag,contents);
    }

    public static void d(Object...contents){
        log(KuroLogType.D,contents);
    }

    public static void dt(String tag,Object...contents){
        log(KuroLogType.D,tag,contents);
    }

    public static void i(Object...contents){
        log(KuroLogType.I,contents);
    }

    public static void it(String tag,Object...contents){
        log(KuroLogType.I,tag,contents);
    }

    public static void w(Object...contents){
        log(KuroLogType.W,contents);
    }

    public static void wt(String tag,Object...contents){
        log(KuroLogType.W,tag,contents);
    }

    public static void e(Object...contents){
        log(KuroLogType.E,contents);
    }

    public static void et(String tag,Object...contents){
        log(KuroLogType.E,tag,contents);
    }

    public static void a(Object...contents){
        log(KuroLogType.A,contents);
    }

    public static void at(String tag,Object...contents){
        log(KuroLogType.A,tag,contents);
    }

    public static void log(@KuroLogType.TYPE int type,Object...contents ){
        log(type,KuroLogManager.getInstance().getConfig().getGlobalTag(),contents);
    }

    public static void log(@KuroLogType.TYPE int type,@Nullable String tag, Object...contents ){
        log(KuroLogManager.getInstance().getConfig(),type,tag,contents);
    }

    public static void log(@NonNull KuroLogConfig config, @KuroLogType.TYPE int type, @Nullable String tag, Object...contents ){
        if (!config.enable()){
            return;
        }

        //如果配置输出的log等级大于当前的log等级 就不打印
        if (config.printLogLevel()>type){
            return;
        }

        StringBuilder sb =new StringBuilder();
        if (config.includeTread()){
            String threadInfo = KuroLogConfig.KURO_THREAD_FORMATTER.format(Thread.currentThread()) ;
            sb.append(threadInfo).append("\n");
        }

        if (config.stackTraceDepth()>0){
            String stackTrace =KuroLogConfig.KURO_STACKTRACE_FORMATTER.format(KuroStackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(),KURO_LOG_PACKAGE,config.stackTraceDepth()));
            sb.append(stackTrace).append("\n");
        }

        String body = parseBody(contents,config);
        sb.append(body);
        //如果config有打印器直接获取config的打印器，如果没有从manager里面获取
        List<KuroLogPrinter> printers =config.printers()!=null? Arrays.asList(config.printers()):KuroLogManager.getInstance().getPrinters();

        if (printers==null){
            return;
        }
        //打印log
        for (KuroLogPrinter printer : printers) {
            printer.print(config,type,tag,sb.toString());
        }
    }

    private static String parseBody(@NonNull Object[] contents,@NonNull KuroLogConfig config){
        if (config.injectJsonParser()!=null){
            return config.injectJsonParser().toJson(contents);
        }
        StringBuilder sb =new StringBuilder();
        for (Object o:contents){
            sb.append(o.toString()).append(";");
        }
        if (sb.length()>0){
            sb.deleteCharAt(sb.length()-1);
        }

        return sb.toString();
    }
}
