package com.github.oahnus.luqiancommon.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oahnus on 2019-11-01
 * 11:49.
 * 检验工具
 */
public class ValidUtil {
    private static Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9][0-9]{5}(19[0-9]{2}|200[0-9]|2010)(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$");
    // 身份证号 加权因子
    private static int[] ID_CARD_FACTOR = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    // 身份证号校验位
    private static char[] ID_CARD_PARITY = new char[]{'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 校验身份证号
     * @param idCardNumber
     * @return
     */
    public static boolean validIdCardNumber(String idCardNumber) {
        if (idCardNumber == null || idCardNumber.trim().equals("")) {
            return false;
        }
        Matcher matcher = ID_CARD_PATTERN.matcher(idCardNumber);
        if (!matcher.matches()) {
            return false;
        }
        String[] chars = idCardNumber.split("");
        //∑(ai×Wi)(mod 11)
        int sum = 0, ai = 0, wi = 0;
        for (int i = 0; i < 17; i++) {
            ai = Integer.valueOf(chars[i]);
            wi = ID_CARD_FACTOR[i];
            sum += ai * wi;
        }
        char result = ID_CARD_PARITY[sum % 11];
        if (result == 'X') {
            return "X".equals(chars[17]);
        } else {
            return String.valueOf(result).equals(chars[17]);
        }
    }
}
