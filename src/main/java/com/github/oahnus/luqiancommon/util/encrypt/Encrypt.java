package com.github.oahnus.luqiancommon.util.encrypt;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by oahnus on 2020-05-21
 * 16:01.
 */
public class Encrypt {
    public static final String CODE_TYPE = "UTF-8";

    public static String encodeBase64(byte[] bytes) throws UnsupportedEncodingException {
        return new String(Base64.getEncoder().encode(bytes), CODE_TYPE);
    }
    public static byte[] decodeBase64(String base64Str) {
        return Base64.getDecoder().decode(base64Str);
    }
    public static String encode(byte[] bytes) {
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

    public static byte[] decode(String str) {
        byte[] bytes = new byte[str.length()/2];
        for (int i=0;i<str.length(); i+=2) {
            int val = Integer.valueOf(str.substring(i, i + 2), 16);
            bytes[i/2] = (byte)val;
        }
        return bytes;
    }
}
