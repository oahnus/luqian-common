package com.github.oahnus.luqiancommon.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by oahnus on 2017/4/17
 */
public class QrCodeUtil {
    private static BASE64Encoder encoder = new BASE64Encoder();
    private static BASE64Decoder decoder = new BASE64Decoder();

    /**
     * 读取二维码内容
     * @param in 输入流
     * @return 文本内容
     * @throws IOException e
     * @throws NotFoundException e
     */
    public static String readQrcode(InputStream in) throws IOException, NotFoundException {
        return readQrcode(in, "utf-8");
    }

    /**
     * 读取二维码内容
     * @param path 二维码图片文件路径
     * @param charset 编码方式
     * @return 文本内容
     * @throws IOException e
     * @throws NotFoundException e
     */
    public static String readQrcode(String path, String charset) throws IOException, NotFoundException {
        InputStream in = new FileInputStream(new File(path));
        return readQrcode(in, charset);
    }

    /**
     * 读取二维码内容
     * @param in 输入流
     * @param charset 编码方式
     * @return 文本内容
     * @throws IOException e
     * @throws NotFoundException e
     */
    public static String readQrcode(InputStream in, String charset) throws IOException, NotFoundException {
        BufferedImage image = ImageIO.read(in);

        // 二维码读取
        MultiFormatReader reader = new MultiFormatReader();
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
        //指定参数
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.CHARACTER_SET, charset);

        Result result = reader.decode(binaryBitmap, hints);
        return result.getText().trim();
    }

    /**
     * 生成二维码
     * @param content 文本内容
     * @param out 输出流
     * @param sideLen 二维码边长
     * @throws IOException e
     * @throws WriterException e
     */
    public static void createQrcode(String content, OutputStream out, int sideLen) throws IOException, WriterException {
        createQrcode(content, out, "utf-8" ,sideLen);
    }

    /**
     * 生成二维码
     * @param content 文本内容
     * @param out 输出流
     * @param charset 编码方式
     * @param sideLen 二维码边长
     * @throws IOException e
     * @throws WriterException e
     */
    public static void createQrcode(String content, OutputStream out, String charset, int sideLen) throws IOException, WriterException {
        String format = "jpg";

        //定义二维码参数
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, charset);
        //容错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //边框
        hints.put(EncodeHintType.MARGIN,2);

        int oncolor = 0xFF000000;
        int offcolor = 0xFFFFFFFF;
        MatrixToImageConfig config = new MatrixToImageConfig(oncolor, offcolor);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, sideLen, sideLen, hints);
        MatrixToImageWriter.writeToStream(bitMatrix, format, out);
    }
}
