package com.github.oahnus.luqiancommon.timer;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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
            workerThreadPool.submit(delayTask.getTask());
        }
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
}
