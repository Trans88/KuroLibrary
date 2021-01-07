package com.trs88.kurolibrary.log;

/**
 * 线程日志格式化
 */
public class KuroThreadFormatter implements KuroLogFormatter<Thread> {
    @Override
    public String format(Thread data) {
        return "Thread:"+data.getName();
    }
}
