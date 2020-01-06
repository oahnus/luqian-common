package com.github.oahnus.luqiancommon.timer;

import lombok.Data;

/**
 * Created by oahnus on 2020-01-03
 * 16:09.
 */
@Data
public class DelayTask {

    private String name;
    private Runnable task;
    private volatile boolean isCancel;
    private long delayMs;
    private long timeoutTimestamp;
    private TimeSlot slot;

    private DelayTask nextTask;
    private DelayTask prevTask;

    public DelayTask(Runnable task, long delayMs) {
        this.task = task;
        this.delayMs = delayMs;

        this.timeoutTimestamp = System.currentTimeMillis() + delayMs;
    }

    public DelayTask(Runnable task, long delayMs, String name) {
        this(task, delayMs);
        this.name = name;
    }

    public void cancel() {
        this.isCancel = true;
    }

    public String toString() {
        return String.format("DelayTask{name: %s, delayMs: %s}", name, delayMs);
    }
}
