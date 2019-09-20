package com.github.oahnus.luqiancommon.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by oahnus on 2017/10/7
 * 22:24.
 */
public class MD5Helper {
    private static Logger LOGGER = LoggerFactory.getLogger(MD5Helper.class);

    private static Random RANDOM = new Random();
    private static final String DEFAULT_SALT = "jSeI32!f;%ir(Ms23";
    private static final String SALT_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()";
    private static final int SALT_CHARS_LEN = SALT_CHARS.length();

    public static String generateSalt(Integer digit) {
        return  IntStream.range(0, digit)
                .mapToObj(i -> String.valueOf(SALT_CHARS.charAt(RANDOM.nextInt(SALT_CHARS_LEN))))
                .reduce("", (str, ch) -> str + ch);
    }

    public static String generateMD5(String clearText) {
        return generateMD5(clearText, DEFAULT_SALT);
    }

    public static String generateMD5(String clearText, String slat) {
        try {
            int length = clearText.length();
            String preText = clearText.substring(0, length / 2) + slat + clearText.substring(length / 2);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = messageDigest.digest(preText.getBytes("utf-8"));
            return toCipherText(bytes);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            LOGGER.error("ERROR: {}", e.getMessage());
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
        System.out.println("Cipher:" + cipher);

        String salt = generateSalt(5);
        System.out.println(generateMD5("123456", salt));
    }
}
