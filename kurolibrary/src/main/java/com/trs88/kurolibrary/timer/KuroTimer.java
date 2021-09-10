package com.trs88.kurolibrary.timer;

import android.os.SystemClock;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 仿Timer 将定时换成SystemClock.uptimeMillis()
 */
public class KuroTimer {
    private final TaskQueue queue = new TaskQueue();

    private final TimerThread thread = new TimerThread(queue);

    private final Object threadReaper = new Object() {
        protected void finalize() throws Throwable {
            synchronized(queue) {
                thread.newTasksMayBeScheduled = false;
                queue.notify(); // In case queue is empty.
            }
        }
    };

    private final static AtomicInteger nextSerialNumber = new AtomicInteger(0);
    private static int serialNumber() {
        return nextSerialNumber.getAndIncrement();
    }

    public KuroTimer() {
        this("Timer-" + serialNumber());
    }


    public KuroTimer(boolean isDaemon) {
        this("Timer-" + serialNumber(), isDaemon);
    }

    public KuroTimer(String name) {
        thread.setName(name);
        thread.start();
    }


    public KuroTimer(String name, boolean isDaemon) {
        thread.setName(name);
        thread.setDaemon(isDaemon);
        thread.start();
    }


    public void schedule(KuroTimerTask task, long delay) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        sched(task, SystemClock.uptimeMillis()+delay, 0);
    }


    public void schedule(KuroTimerTask task, Date time) {
        task.setIsDate(true);
        sched(task, time.getTime(), 0);
    }


    public void schedule(KuroTimerTask task, long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, SystemClock.uptimeMillis()+delay, -period);
    }

    public void schedule(KuroTimerTask task, Date firstTime, long period) {
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        task.setIsDate(true);
        sched(task, firstTime.getTime(), -period);
    }


    public void scheduleAtFixedRate(KuroTimerTask task, long delay, long period) {
        if (delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, SystemClock.uptimeMillis()+delay, period);
    }


    public void scheduleAtFixedRate(KuroTimerTask task, Date firstTime,
                                    long period) {
        if (period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        task.setIsDate(true);
        sched(task, firstTime.getTime(), period);
    }

    private void sched(KuroTimerTask task, long time, long period) {
        if (time < 0)
            throw new IllegalArgumentException("Illegal execution time.");

        // Constrain value of period sufficiently to prevent numeric
        // overflow while still being effectively infinitely large.
        if (Math.abs(period) > (Long.MAX_VALUE >> 1))
            period >>= 1;

        synchronized(queue) {
            if (!thread.newTasksMayBeScheduled)
                throw new IllegalStateException("Timer already cancelled.");

            synchronized(task.lock) {
                if (task.state != KuroTimerTask.VIRGIN)
                    throw new IllegalStateException(
                            "Task already scheduled or cancelled");
                task.nextExecutionTime = time;
                task.period = period;
                task.state = KuroTimerTask.SCHEDULED;
            }

            queue.add(task);
            if (queue.getMin() == task)
                queue.notify();
        }
    }

    public void cancel() {
        synchronized(queue) {
            thread.newTasksMayBeScheduled = false;
            queue.clear();
            queue.notify();  // In case queue was already empty.
        }
    }


    public int purge() {
        int result = 0;

        synchronized(queue) {
            for (int i = queue.size(); i > 0; i--) {
                if (queue.get(i).state == KuroTimerTask.CANCELLED) {
                    queue.quickRemove(i);
                    result++;
                }
            }

            if (result != 0)
                queue.heapify();
        }

        return result;
    }
}

class TimerThread extends Thread {

    boolean newTasksMayBeScheduled = true;


    private TaskQueue queue;

    TimerThread(TaskQueue queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            mainLoop();
        } finally {
            // Someone killed this Thread, behave as if Timer cancelled
            synchronized(queue) {
                newTasksMayBeScheduled = false;
                queue.clear();  // Eliminate obsolete references
            }
        }
    }


    private void mainLoop() {
        while (true) {
            try {
                KuroTimerTask task;
                boolean taskFired;
                synchronized(queue) {
                    // Wait for queue to become non-empty
                    while (queue.isEmpty() && newTasksMayBeScheduled)
                        queue.wait();
                    if (queue.isEmpty())
                        break; // Queue is empty and will forever remain; die

                    // Queue nonempty; look at first evt and do the right thing
                    long currentTime, executionTime;
                    task = queue.getMin();
                    synchronized(task.lock) {
                        if (task.state == KuroTimerTask.CANCELLED) {
                            queue.removeMin();
                            continue;  // No action required, poll queue again
                        }

                        if (task.getIsDate()){
                            currentTime = System.currentTimeMillis();
                        }else {
                            currentTime = SystemClock.uptimeMillis();
                        }

                        executionTime = task.nextExecutionTime;
                        if (taskFired = (executionTime<=currentTime)) {
                            if (task.period == 0) { // Non-repeating, remove
                                queue.removeMin();
                                task.state = KuroTimerTask.EXECUTED;
                            } else { // Repeating task, reschedule
                                queue.rescheduleMin(
                                        task.period<0 ? currentTime   - task.period
                                                : executionTime + task.period);
                            }
                        }
                    }
                    if (!taskFired) // Task hasn't yet fired; wait
                        queue.wait(executionTime - currentTime);
                }
                if (taskFired)  // Task fired; run it, holding no locks
                    task.run();
            } catch(InterruptedException e) {
            }
        }
    }
}

class TaskQueue {

    private KuroTimerTask[] queue = new KuroTimerTask[128];


    private int size = 0;


    int size() {
        return size;
    }


    void add(KuroTimerTask task) {
        // Grow backing store if necessary
        if (size + 1 == queue.length)
            queue = Arrays.copyOf(queue, 2*queue.length);

        queue[++size] = task;
        fixUp(size);
    }

    KuroTimerTask getMin() {
        return queue[1];
    }


    KuroTimerTask get(int i) {
        return queue[i];
    }


    void removeMin() {
        queue[1] = queue[size];
        queue[size--] = null;  // Drop extra reference to prevent memory leak
        fixDown(1);
    }


    void quickRemove(int i) {
        assert i <= size;

        queue[i] = queue[size];
        queue[size--] = null;  // Drop extra ref to prevent memory leak
    }


    void rescheduleMin(long newTime) {
        queue[1].nextExecutionTime = newTime;
        fixDown(1);
    }

    boolean isEmpty() {
        return size==0;
    }

    void clear() {
        // Null out task references to prevent memory leak
        for (int i=1; i<=size; i++)
            queue[i] = null;

        size = 0;
    }


    private void fixUp(int k) {
        while (k > 1) {
            int j = k >> 1;
            if (queue[j].nextExecutionTime <= queue[k].nextExecutionTime)
                break;
            KuroTimerTask tmp = queue[j];  queue[j] = queue[k]; queue[k] = tmp;
            k = j;
        }
    }


    private void fixDown(int k) {
        int j;
        while ((j = k << 1) <= size && j > 0) {
            if (j < size &&
                    queue[j].nextExecutionTime > queue[j+1].nextExecutionTime)
                j++; // j indexes smallest kid
            if (queue[k].nextExecutionTime <= queue[j].nextExecutionTime)
                break;
            KuroTimerTask tmp = queue[j];  queue[j] = queue[k]; queue[k] = tmp;
            k = j;
        }
    }

    void heapify() {
        for (int i = size/2; i >= 1; i--)
            fixDown(i);
    }
}
