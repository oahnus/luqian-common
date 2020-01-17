package com.github.oahnus.luqiancommon.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by oahnus on 2017/10/7
 * 22:24.
 */
public class MD5Helper {
    private static Random RANDOM = new Random();
    private static final String DEFAULT_SALT = "";
    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()";
    private static final int SALT_CHARS_LEN = SALT_CHARS.length();

    public enum MD5_BITS {
        _16BITS,
        _32BITS
    }

    public static String generateSalt(Integer digit) {
        return  IntStream.range(0, digit)
                .mapToObj(i -> String.valueOf(SALT_CHARS.charAt(RANDOM.nextInt(SALT_CHARS_LEN))))
                .reduce("", (str, ch) -> str + ch);
    }

    public static String generateMD5(String clearText) {
        return generateMD5(clearText, DEFAULT_SALT);
    }

    public static String generateMD5(String clearText, MD5_BITS bits) {
        String md5 = generateMD5(clearText);
        if (bits.equals(MD5_BITS._32BITS)) {
            return md5;
        } else {
            return md5.substring(8, 24);
        }
    }

    public static String generateMD5(String clearText, String slat) {
        try {
            int length = clearText.length();
            String preText = clearText.substring(0, length / 2) + slat + clearText.substring(length / 2);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(preText.getBytes(StandardCharsets.UTF_8));
            return toCipherText(bytes);
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    private static String toCipherText(byte[] bytes) {
        StringBuilder cipherText = new StringBuilder();
        for (byte aByte : bytes) {
            int val = aByte & 0xff;
            if (val < 16) {
                cipherText.append("0");
            }
            cipherText.append(Integer.toHexString(val));
        }
        return cipherText.toString();
    }

    public static void main(String... args) {
        String cipher = generateMD5("123456");
        System.out.println(cipher);
        System.out.println(generateMD5("123456", MD5_BITS._16BITS));

        String salt = generateSalt(5);
        System.out.println(generateMD5("123456", salt));
    }
}
