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
 * ???????????????
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
     * ????KuroFilePrinter
     * @param logPath log????��???????????��????????????????��???��???
     * @param retentionTime log???????��???????��????<=0???????��
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
     * ???????Log
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
         * ??log??????????
         * @param log ????????log
         */
        void put(KuroLogModel log){
            try {
                logs.put(log);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * ?��??????????????????
         * @return
         */
        boolean isRunning(){
            synchronized (this){
                return running;
            }
        }

        /**
         * ???????????
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
     * ????BufferedWriter??log��?????
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
         * log��????????????
         *
         * @param newFileName ?????log???????
         * @return true ??????????
         */
        boolean ready(String newFileName){
            preFileName =newFileName;
            logFile =new File(logPath,newFileName);

            //??log???????????log???
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
         * ???bufferedWriter
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
         * ??log��?????
         * @param flattenedLog ????????log
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
