package com.github.oahnus.luqiancommon.timer;

import java.util.concurrent.DelayQueue;

/**
 * Created by oahnus on 2020-01-02
 * 10:35.
 */
public class TimeWheel {
    private TimeSlot[] slots;
    private int slotSize;
    private long tickMs;
    private long interval;
    private long currTimestamp;
    private volatile TimeWheel overflowWheel;

    private DelayQueue<TimeSlot> slotQueues;

    public TimeWheel(long tickMs, int slotSize, long currTimestamp, DelayQueue<TimeSlot> slotQueues) {
        this.tickMs = tickMs;
        this.slotQueues = slotQueues;
        this.slotSize = slotSize;
        this.interval = tickMs * slotSize;
        this.currTimestamp = currTimestamp - (currTimestamp % tickMs);

        this.slots = new TimeSlot[slotSize];
        for (int i = 0; i < slots.length; i++) {
            this.slots[i] = new TimeSlot();
        }
    }

    public boolean addTask(DelayTask task) {
        long timeoutTimestamp = task.getTimeoutTimestamp();

        if (timeoutTimestamp < currTimestamp + tickMs) {
            return false;
        }

        if (timeoutTimestamp >= currTimestamp + interval) {
            TimeWheel overflowWheel = getOverflowWheel();
            return overflowWheel.addTask(task);
        } else {
            int slotIndex = (int) ((timeoutTimestamp / tickMs) % slotSize);
            TimeSlot slot = slots[slotIndex];
            slot.addTask(task);

            if (slot.setSlotExpire(timeoutTimestamp)) {
                slotQueues.offer(slot);
            }
            return true;
        }
    }

    public TimeWheel getOverflowWheel() {
        if (this.overflowWheel == null) {
            synchronized (this) {
                if (this.overflowWheel == null) {
                    this.overflowWheel = new TimeWheel(interval, slotSize, currTimestamp, slotQueues);
                }
            }
        }
        return this.overflowWheel;
    }

    public void advanceTime(long timestamp) {
        if (timestamp > currTimestamp + tickMs) {
            currTimestamp = timestamp - (timestamp & (tickMs - 1));
            if (overflowWheel != null) {
                overflowWheel.advanceTime(timestamp);
            }
        }
    }

    public long taskSize() {
        return slotQueues.size();
    }
}
