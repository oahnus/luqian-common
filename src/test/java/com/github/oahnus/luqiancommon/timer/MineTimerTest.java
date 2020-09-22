package com.github.oahnus.luqiancommon.timer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 * Created by oahnus on 2020-09-22
 */
@RunWith(JUnit4.class)
public class MineTimerTest {

    @Test
    public void testMineTimer() throws InterruptedException {
        MineTimer.getInstance().add(new DelayTask(() -> {}, 2000));
        assertEquals(MineTimer.getInstance().taskSize(), 1);
        TimeUnit.SECONDS.sleep(3);
        assertEquals(MineTimer.getInstance().taskSize(), 0);
    }
}
