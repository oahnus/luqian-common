package com.github.oahnus.luqiancommon.util;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Created by oahnus on 2017/11/28
 * 14:05.
 */
public class DESHelper {
    private static final String KEY = "jxk;3dfefs";
    private static final String CODE_TYPE = "UTF-8";

    public static String encodeDES(String clearText) throws InvalidKeyException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, InvalidKeySpecException, NoSuchPaddingException, UnsupportedEncodingException {
        // 可信任随机数源
        SecureRandom random = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(KEY.getBytes(CODE_TYPE));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // key的长度不能够小于8位字节
        Key secretKey = keyFactory.generateSecret(dks);
        // 指定 加密方法/加密模式/填充规则
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        // 指定iv
//        IvParameterSpec iv = new IvParameterSpec("12345678".getBytes());
        // ECB 模式使用随机生成的iv
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
        byte[] bytes = cipher.doFinal(clearText.getBytes());
        // 输出方式 BASE64
        return new String(Base64.getEncoder().encode(bytes));
    }

    public static String decodeDES(String cipherText) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(KEY.getBytes(CODE_TYPE));
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);

        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), CODE_TYPE);
    }
}
