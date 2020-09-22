package com.github.oahnus.luqiancommon.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertTrue;

/**
 * Created by oahnus on 2020-09-22
 */
@RunWith(JUnit4.class)
public class ValidUtilsTest {
    @Test
    public void testGenerateIdCardNum() {
        for (int i = 0; i < 10; i++) {
            String idcard = ValidUtils.generateIdcard();
            assertTrue(ValidUtils.validIdCardNumber(idcard));
            System.out.println(idcard);
        }
    }
}
