package com.trs88.kurolibrary.log;

/**
 * 堆栈打印格式化
 */
public class KuroStackTraceFormatter implements KuroLogFormatter<StackTraceElement[]> {
    @Override
    public String format(StackTraceElement[] data) {
        StringBuilder sb =new StringBuilder(128);
        if (data ==null ||data.length ==0){
            return null;
        }else if (data.length ==1){
            return "\t─ " +data[0].toString();
        }else {
            for (int i = 0, len =data.length; i < len; i++) {
                if (i ==0){
                    sb.append("stackTrace: \n");
                }
                if (i !=len-1){
                    sb.append("\t ├ ");
                    sb.append(data[i].toString());
                    sb.append("\n");
                }else {
                    sb.append("\t└");
                    sb.append(data[i].toString());
                }
            }
            return sb.toString();
        }
    }
}
