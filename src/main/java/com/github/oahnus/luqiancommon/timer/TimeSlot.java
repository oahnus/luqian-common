package com.github.oahnus.luqiancommon.timer;

import lombok.Data;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Created by oahnus on 2020-01-03
 * 16:15.
 */
@Data
public class TimeSlot implements Delayed {
    private final DelayTask rootTask;
    private AtomicLong slotExpire = new AtomicLong();

    private AtomicLong counter = new AtomicLong();

    public TimeSlot() {
        rootTask = new DelayTask(null, -1);
        rootTask.setPrevTask(rootTask);
        rootTask.setNextTask(rootTask);
    }

    public boolean setSlotExpire(long expire) {
        return slotExpire.getAndSet(expire) != expire;
    }

    public long getSlotExpire() {
        return slotExpire.get();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return Math.max(0, unit.convert(slotExpire.get() - System.currentTimeMillis(), TimeUnit.MILLISECONDS));
    }

    @Override
    public int compareTo(Delayed o) {
        if (o instanceof TimeSlot) {
            return Long.compare(slotExpire.get(), ((TimeSlot) o).getSlotExpire());
        }
        return 0;
    }

    public void remove(DelayTask task) {
        synchronized (this) {
            if (task.getSlot().equals(this)) {
                DelayTask next = task.getNextTask();
                DelayTask prev = task.getPrevTask();
                prev.setNextTask(next);
                next.setPrevTask(prev);
                task.setNextTask(null);
                task.setPrevTask(null);
                task.setSlot(null);
                counter.decrementAndGet();
            }
        }
    }

    public void addTask(DelayTask task) {
        synchronized (this) {
            if (task.getSlot() == null) {
                task.setSlot(this);

                DelayTask tail = rootTask.getNextTask();

                task.setNextTask(rootTask);
                task.setPrevTask(tail);
                tail.setNextTask(task);
                rootTask.setPrevTask(task);

                counter.incrementAndGet();
            }
        }
    }

    public synchronized void flush(Consumer<DelayTask> func) {
        DelayTask head = rootTask.getNextTask();
        while (!Objects.equals(rootTask, head)) {
            this.remove(head);
            if (!head.isCancel()) {
                func.accept(head);
            }
            head = rootTask.getNextTask();
        }
        setSlotExpire(-1);
    }

    public long size() {
        return counter.get();
    }
}
