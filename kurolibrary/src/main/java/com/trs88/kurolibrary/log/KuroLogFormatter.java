package com.trs88.kurolibrary.log;

public interface KuroLogFormatter<T> {
    String format(T data);
}
