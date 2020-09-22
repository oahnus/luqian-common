package com.github.oahnus.luqiancommon.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oahnus on 2019-11-01
 * 11:49.
 * 检验工具
 */
public class ValidUtils {
    private static Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9][0-9]{5}(19[0-9]{2}|20[0-9]{2})(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{3}[0-9xX]$");
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

        char result = calCheckDigit(chars);

        if (result == 'X') {
            return "X".equals(chars[17]);
        } else {
            return String.valueOf(result).equals(chars[17]);
        }
    }

    /**
     * 中国 银行卡号 luhn校验算法
     * @param bankNum 卡号
     * @return
     */
    public static boolean bankNumLuhn(String bankNum) {
        int sum = 0;
        int length = bankNum.length();
        for (int i = 0; i < length; i++) {
            char ch = bankNum.charAt(length - i - 1);
            if ((ch >= 'a' && ch<='z') || (ch >= 'A' && ch<='Z')) {
                return false;
            }
            int num = Character.digit(ch, 10);

            if (((i + 1) & 1) == 0) {
                int val = num << 1;
                sum += (val > 9 ? val - 9 : val);
            } else {
                sum += num;
            }
        }

        if (sum == 0) return false;

        return (sum & 9) == 0;
    }

    private static char calCheckDigit(String[] strings) {
        //∑(ai×Wi)(mod 11)
        int sum = 0, ai = 0, wi = 0;
        for (int i = 0; i < 17; i++) {
            ai = Integer.valueOf(strings[i]);
            wi = ID_CARD_FACTOR[i];
            sum += ai * wi;
        }
        return ID_CARD_PARITY[sum % 11];
    }

    public static String generateIdcard() {
        Random random = new Random();
        String regionCode = String.valueOf(random.nextInt(99999) + 100000);
        int year = random.nextInt(120) + 1900;
        LocalDate bornDate = LocalDate.of(year, 1, 1).plusDays(random.nextInt(365));
        String born = bornDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String seq = String.format("%03d", random.nextInt(998) + 1);
        String prefix = regionCode + born + seq;
        char checkDigit = calCheckDigit(prefix.split(""));
        return prefix + checkDigit;
    }
}
