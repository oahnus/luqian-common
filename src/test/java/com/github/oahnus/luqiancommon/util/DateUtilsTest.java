package com.github.oahnus.luqiancommon.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.github.oahnus.luqiancommon.util.DateUtils.*;

/**
 * Created by oahnus on 2020-09-22
 */
@RunWith(JUnit4.class)
public class DateUtilsTest {
    @Test
    public void testFormatInMultiThread() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateTime = "2016-12-30 15:35:34";

        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                for (int i1 = 0; i1 < 5; i1++) {
                    // Error
//                        System.out.println(Thread.currentThread().getName() + "\t" + dateFormat.parse(dateTime));
                    System.out.println(Thread.currentThread().getName() + "\t" + string2Date(dateTime, PATTERN_YMD_HMS2));
                }
            }).start();
        }
    }
}
