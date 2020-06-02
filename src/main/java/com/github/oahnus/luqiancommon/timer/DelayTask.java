package com.github.oahnus.luqiancommon.timer;

import lombok.Data;

import java.util.UUID;

/**
 * Created by oahnus on 2020-01-03
 * 16:09.
 */
@Data
public class DelayTask {

    private String name;
    private Runnable task;
    private volatile boolean isCancel; // 是否取消任务
    private long delayMs; // 延时时间 ms毫秒
    private long timeoutTimestamp;
    private TimeSlot slot;

    private DelayTask nextTask;
    private DelayTask prevTask;
    private Boolean allways = false; // 任务是否常驻， false 只执行一次

    private String virtualId;

    public DelayTask(Runnable task, long delayMs) {
        this.task = task;
        this.delayMs = delayMs;
        this.name = Thread.currentThread().getName() + Thread.currentThread().getId();

        this.timeoutTimestamp = System.currentTimeMillis() + delayMs;
        this.virtualId = UUID.randomUUID().toString();
    }

    public DelayTask(Runnable task, long delayMs, String name) {
        this(task, delayMs);
        this.name = name;
    }

    public void refresh() {
        this.timeoutTimestamp = System.currentTimeMillis() + delayMs;
    }

    public void cancel() {
        this.isCancel = true;
    }

    public String toString() {
        return String.format("DelayTask{name: %s, delayMs: %s}", name, delayMs);
    }
}
