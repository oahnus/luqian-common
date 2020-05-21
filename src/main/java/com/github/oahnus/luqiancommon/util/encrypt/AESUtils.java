package com.github.oahnus.luqiancommon.util.encrypt;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by oahnus on 2020-04-18
 * 18:59.
 */
public class AESUtils {
    private static String OFFSET = "cliwn;123va9xt)g";
    private static String CODE_TYPE = "UTF-8";
    private static String SECRET = "clojwna)43:f9c23";

    private static IvParameterSpec ips = new IvParameterSpec(OFFSET.getBytes());
    private static SecretKeySpec sks = new SecretKeySpec(SECRET.getBytes(), "AES");

    static {
        encrypt("INIT");
    }

    public static void init(String offset, String secret) {
        if (offset == null || secret == null) {
            throw new RuntimeException("offset or secret cannot be null");
        }
        if (offset.length() != 16 || secret.length() != 16) {
            throw new RuntimeException("offset or secret length must be 16");
        }
        OFFSET = offset;
        SECRET = secret;
        ips = new IvParameterSpec(OFFSET.getBytes());
        sks = new SecretKeySpec(SECRET.getBytes(), "AES");
    }

    public static String encrypt(String clearText) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, sks, ips);
            byte[] bytes = cipher.doFinal(clearText.getBytes(CODE_TYPE));
            return Encrypt.encode(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String decrypt(String secretText) {
        try {
            byte[] bytes = Encrypt.decode(secretText);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, sks, ips);
            return new String(cipher.doFinal(bytes));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void main(String... args) {
        String encrypt = encrypt("123456");;
        System.out.println(encrypt);
        System.out.println(decrypt(encrypt));
    }
}
