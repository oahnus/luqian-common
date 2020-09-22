package com.github.oahnus.luqiancommon.timer;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Created by oahnus on 2020-01-03
 * 16:32.
 */
@Slf4j
public class MineTimer {
    private static DelayQueue<TimeSlot> slotDelayQueue = new DelayQueue<>();

    private TimeWheel timeWheel;

    private ExecutorService workerThreadPool;
    private ExecutorService bossThreadPool;

    private static MineTimer INSTANCE;
    private static long TICK_MS = 20;

    private MineTimer() {
        bossThreadPool = Executors.newSingleThreadExecutor();
        workerThreadPool = Executors.newFixedThreadPool(10);

        timeWheel = new TimeWheel(TICK_MS, 20, System.currentTimeMillis(), slotDelayQueue);
        bossThreadPool.execute(() -> {
            while (true) {
                INSTANCE.advanceTime(TICK_MS);
            }
        });
    }

    public static MineTimer getInstance() {
        if (INSTANCE == null) {
            synchronized (MineTimer.class) {
                if (INSTANCE == null) {
                    INSTANCE = new MineTimer();
                }
            }
        }
        return INSTANCE;
    }

    public void add(DelayTask delayTask) {
        if (!timeWheel.addTask(delayTask)) {
            log.debug("RUN TASK {}", delayTask);
            Future<?> future = workerThreadPool.submit(delayTask.getTask());
            future.cancel(true);

            // 如果延时任务需要常驻, 在提交执行时，将自身再次加入槽中
            if (delayTask.getAlways()) {
                delayTask.refresh();
                timeWheel.addTask(delayTask);
            }
        }
    }

    public boolean remove(String virtualId) {
        for (TimeSlot timeSlot : slotDelayQueue) {
            DelayTask rootTask = timeSlot.getRootTask();
            DelayTask head = rootTask.getNextTask();
            while (!head.equals(rootTask)) {
                if (head.getVirtualId().equals(virtualId)) {
                    head.cancel();
                    return true;
                }
                head = head.getNextTask();
            }
        }
        return false;
    }

    public void advanceTime(long tickMs) {
        try {
            TimeSlot slot = slotDelayQueue.poll(tickMs, TimeUnit.MILLISECONDS);
            if (slot != null) {
                long slotExpire = slot.getSlotExpire();
                timeWheel.advanceTime(slotExpire);
                slot.flush(this::add);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long taskSize() {
        return timeWheel.taskSize();
    }
}
