package com.github.oahnus.luqiancommon.util;

import java.util.Random;

/**
 * Created by oahnus on 2020-02-28
 * 17:52.
 */
public class RandomUtil {
    private static Random random = new Random();

    private static final String SEQ_1 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SEQ_2 = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123467890";

    public static String genNchars(int n) {
        int max = SEQ_1.length();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<n;i++) {
            int idx = random.nextInt(max);
            sb.append(SEQ_1.charAt(idx));
        }
        return sb.toString();
    }

    public static String genNcharsWithNumber(int n) {
        int max = SEQ_2.length();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<n;i++) {
            int idx = random.nextInt(max);
            sb.append(SEQ_2.charAt(idx));
        }
        return sb.toString();
    }

    public static void main(String... args) {
        for (int i = 0; i < 10; i++) {
            String s = genNchars(8);
            System.out.println(s);
        }
    }
}
