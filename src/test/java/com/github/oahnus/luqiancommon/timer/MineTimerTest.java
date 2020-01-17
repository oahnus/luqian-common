package com.github.oahnus.luqiancommon.timer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.TimeUnit;

/**
 * Created by oahnus on 2020-01-07
 * 13:26.
 */
@RunWith(JUnit4.class)
public class MineTimerTest {
    @Test
    public void testRemoveTask() throws InterruptedException {
        MineTimer timer = MineTimer.getInstance();
        DelayTask task = new DelayTask(() -> {
            System.out.println("20秒时间到");
        }, 5000, "测试");
        timer.add(task);

        TimeUnit.SECONDS.sleep(5);
        boolean ret = timer.remove(task.getVirtualId());
        TimeUnit.SECONDS.sleep(15);
    }
}
