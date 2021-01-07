package com.trs88.kurolibrary.log;

/**
 * 对KuroLog进行配置
 */
public abstract class KuroLogConfig {
    static int MAX_LEN =512;//日志格式化时每一行最大的长度

    static KuroStackTraceFormatter KURO_STACKTRACE_FORMATTER =new KuroStackTraceFormatter();
    static KuroThreadFormatter KURO_THREAD_FORMATTER =new KuroThreadFormatter();

    public JsonParser injectJsonParser(){
        return null;
    }

    public String getGlobalTag(){
       return  "KuroLog";
    }

    public boolean enable(){
        return true;
    }

    /**
     * 是否包含线程信息
     * @return 默认不包含
     */
    public  boolean includeTread(){
        return false;
    }

    /**
     * 打印堆栈信息的深度
     * @return 深度
     */
    public int stackTraceDepth(){
        return 5;
    }

    /**
     * 让用户注册打印器
     * @return null
     */
    public KuroLogPrinter[] printers(){
        return null;
    }

    /**
     * 这里因为想打印任何对象，但是又不想耦合序列号工具比如Gson，所以提供一个接口让使用者自己序列化
     */
    public interface  JsonParser{
        String toJson(Object src);
    }

}
