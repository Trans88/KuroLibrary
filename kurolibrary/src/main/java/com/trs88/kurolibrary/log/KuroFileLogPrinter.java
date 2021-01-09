package com.trs88.kurolibrary.log;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 本地文件日志打印器
 */
public class KuroFileLogPrinter implements KuroLogPrinter {
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final String logPath;
    private final long retentionTime;
    private LogWriter writer;
    private volatile PrintWorker worker;
    private static KuroFileLogPrinter instance;

    public KuroFileLogPrinter(String logPath, long retentionTime) {
        this.logPath = logPath;
        this.retentionTime = retentionTime;
        this.writer =new LogWriter();
        this.worker =new PrintWorker();
        cleanExpiredLog();
    }



    /**
     * 创建KuroFilePrinter
     * @param logPath log保存路径，如果是外部路径需要确保已经有外部存储的读写权限
     * @param retentionTime log文件的有效时长，单位毫秒，<=0表示一直有效
     * @return KuroFilePrinter
     */
    public static synchronized KuroFileLogPrinter getInstance(String logPath, long retentionTime){
        if (instance ==null){
            instance =new KuroFileLogPrinter(logPath,retentionTime);
        }

        return instance;
    }

    private void doPrint(KuroLogModel logModel){
        String lastFileName =writer.getPreFileName();
        if (lastFileName ==null){
            String newFileName =genFileName();
            if (writer.isReady()){
                writer.close();
            }

            if (!writer.ready(newFileName)){
                return;
            }
        }

        writer.append(logModel.flattenedLog());
    }

    private String genFileName(){
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 清楚过期Log
     */
    private void cleanExpiredLog() {
        if (retentionTime<=0){
            return;
        }

        long currentTimeMills =System.currentTimeMillis();
        File logDir =new File(logPath);
        File[] files = logDir.listFiles();
        if (files ==null){
            return;
        }

        for (File file : files) {
            if (currentTimeMills-file.lastModified()>retentionTime){
                file.delete();
            }
        }
    }



    @Override
    public void print(@NonNull KuroLogConfig config, int level, String tag, @NonNull String printString) {
        long timeMillis=System.currentTimeMillis();
        if (!worker.isRunning()){
            worker.start();
        }
        worker.put(new KuroLogModel(timeMillis,level,tag,printString));
    }

    private class PrintWorker implements Runnable{
        private BlockingQueue<KuroLogModel> logs =new LinkedBlockingQueue<>();
        private volatile boolean running;

        /**
         * 将log放入打印队列
         * @param log 要被打印的log
         */
        void put(KuroLogModel log){
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 判断工作线程是否在运行中
         * @return
         */
        boolean isRunning(){
            synchronized (this){
                return running;
            }
        }

        /**
         * 启动工作线程
         */
        void start(){
            synchronized (this){
                EXECUTOR.execute(this);
                running =true;
            }
        }

        @Override
        public void run() {
            KuroLogModel log;
            while (true){
                try {
                    log =logs.take();
                    doPrint(log);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    synchronized (this){
                        running =false;
                    }
                }
            }
        }
    }


    /**
     * 基于BufferedWriter将log写入文件
     */
    private class LogWriter{
        private String preFileName;
        private File logFile;
        private BufferedWriter bufferedWriter;

        boolean isReady(){
            return bufferedWriter !=null;
        }

        String getPreFileName(){
            return preFileName;
        }

        /**
         * log写入前的准备操作
         *
         * @param newFileName 要保存log的文件名
         * @return true 表示准备就绪
         */
        boolean ready(String newFileName){
            preFileName =newFileName;
            logFile =new File(logPath,newFileName);

            //当log不存在时创建log文件
            if(!logFile.exists()){
                try {
                    File parent = logFile.getParentFile();
                    if (!parent.exists()){
                        parent.mkdir();
                    }
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    preFileName =null;
                    logFile =null;
                    return false;
                }
            }

            try {
                bufferedWriter =new BufferedWriter(new FileWriter(logFile,true));
            } catch (IOException e) {
                e.printStackTrace();
                preFileName =null;
                logFile =null;
                return false;
            }

            return true;
        }

        /**
         * 关闭bufferedWriter
         * @return
         */
        boolean close(){
            if (bufferedWriter!=null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }finally {
                    bufferedWriter = null;
                    preFileName = null;
                    logFile = null;
                }
            }
            return true;
        }

        /**
         * 将log写入文件
         * @param flattenedLog 格式化后的log
         */
        void append(String flattenedLog){
            try {
                bufferedWriter.write(flattenedLog);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
