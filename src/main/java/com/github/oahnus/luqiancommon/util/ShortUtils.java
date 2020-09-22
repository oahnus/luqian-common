package com.github.oahnus.luqiancommon.util;

/**
 * Created by oahnus on 2020-08-07
 */
public class ShortUtils {
    private static final String CHARS = "abcdefghijklmnopqrstuvwxyz1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static String longToShort(long num) {
        StringBuilder s = new StringBuilder();
        int remain;
        long dividend = num;
        if (dividend == 0) {
            return String.valueOf(CHARS.charAt(0));
        }
        while (dividend > 0) {
            remain = (int) (dividend % 62);
            s.append(CHARS.charAt(remain));
            dividend = dividend / 62;
        }

        return s.reverse().toString();
    }

    public static Long shortToLong(String shortStr) {
        String[] split = shortStr.split("");
        long sum = 0;
        for (int i = 0; i < split.length; i++) {
            int j = split.length - i - 1;
            String s = split[j];
            int num = CHARS.indexOf(s);
            sum += num * Math.pow(62, i);
        }
        return sum;
    }
}
