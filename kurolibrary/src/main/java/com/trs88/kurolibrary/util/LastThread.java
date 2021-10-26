package com.trs88.kurolibrary.util;

public class LastThread {

    private long sleep;// 需要睡眠多久

    private LastThreadListener listenr;// 回调

    private Object value;// 携带对象

    private int count = 0;// 线程标识

    private Thread curr;// 当前正在执行的线程

    public static final int END = -1;// 回调标记

    public static final int START = 1; // 回调标记为开始

    public LastThread(LastThreadListener listenr) {
        this.listenr = listenr;
    }

    /**
     * 调用此方法会启动一个新线程，如果在新线程之前还有旧线程没睡醒，那么旧线程的call方法就不会调用
     * <P>
     * 假设： 9点20调用call生成一个线程T1，T1会睡眠2分钟 9点21再次调用call生成一个线程A2 由于T1没有睡醒，call方法就被再次调用，所以T1的{@link LastThreadListener}
     * 中的call不会执行，只执行了T2的 {@link LastThreadListener}中的call 最后结果：{@link LastThread}中的call 调用了两次，但是 {@link LastThreadListener}中的call只执行了一次
     *
     * @param obj 允许携带的参数，@link {@link LastThreadListener}
     */
    public void call(Object obj) {
        start(obj);
    }

    /**
     * 在call 之前调用，线程睡醒后才会执行{@link LastThreadListener }中方法的call
     *
     * @param sleep 毫秒
     */
    public void setSleep(long sleep) {
        this.sleep = sleep;
    }

    /**
     * 携带参数
     *
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * 停止所有没睡醒的线程
     */
    public void cancel() {
        allowExcuteVersion();
        stop();
    }

    // 真正处理逻辑
    private void start(final Object obj) {
        allowExcuteVersion();
        stop();

        curr = new Thread(new Runnable() {

            public void run() {
                if (sleep > 0) {
                    try {
                        Thread.sleep(sleep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                Thread currentThread = Thread.currentThread();

                boolean return1 = isReturn(currentThread);
                if (return1) {
                    if (listenr != null) {
                        listenr.call(END, value);
                    }
                    return;
                }

                if (listenr != null) {
                    listenr.call(START, obj);
                }

            }
        });
        curr.setName(count + "");
        curr.start();
    }

    // 最新线程的标记
    private void allowExcuteVersion() {
        count++;
    }

    // 停止当前的线程
    private void stop() {
        if (curr != null) {
            try {
                curr.stop();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 是否调用线程的{@link LastThreadListener}中call
     *
     * @param thread
     * @return
     */
    private boolean isReturn(Thread thread) {
        String name = thread.getName();
        int curr = Integer.valueOf(name);
        if (curr < count) {
            return true;
        }
        return false;
    }

}
