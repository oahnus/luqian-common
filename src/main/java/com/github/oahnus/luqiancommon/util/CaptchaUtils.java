package com.github.oahnus.luqiancommon.util;

import sun.font.FontDesignMetrics;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

/**
 * Created by oahnus on 2020-06-09
 */
public class CaptchaUtils {
    private static Random random = new Random();
    private static final int DEFAULT_WIDTH = 100;
    private static final int DEFAULT_HEIGHT = 40;

    /**
     * 生成验证码
     * @param captcha 验证码文本
     * @param out 输出流
     * @throws IOException e
     */
    public static void captcha(String captcha, OutputStream out) throws IOException {
        captcha(captcha, out, false);
    }

    /**
     * 生成验证码
     * @param captcha 验证码文本
     * @param out 输出流
     * @param withConfusion 是否包含直线等混淆线
     * @throws IOException e
     */
    public static void captcha(String captcha, OutputStream out, Boolean withConfusion) throws IOException {
        captcha(captcha, out, withConfusion, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 生成验证码
     * @param captcha 验证码文本
     * @param out 输出流
     * @param withConfusion 是否包含直线等混淆线
     * @param w width
     * @param h height
     */
    public static void captcha(String captcha, OutputStream out, Boolean withConfusion, int w, int h) throws IOException {
        int captchaLen = captcha.length();

        // 计算给定尺寸内能得到的最大正方形
        int sideLen = w / captchaLen;
        if (sideLen > h) {
            sideLen = h - 4;
        }
        // 字体大小设为正方形边长
        int fontSize = sideLen;

        // 画布
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, w, h);

        // 字体
        Font font = new Font(Font.MONOSPACED, Font.BOLD, fontSize);
        graphics.setFont(font);
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);

        // 获取完整字符串width
        int stringWidth = metrics.stringWidth(captcha);
        // 字符间隔width
        int spaceWidth = (w - stringWidth) / (captchaLen + 1);
        // 单个字符width
        int charWidth = stringWidth / captchaLen;
        // 计算字符串横纵绘制坐标
        int top = (h - metrics.getHeight()) / 2 + metrics.getAscent();
        int left = spaceWidth;
        for (String ch : captcha.split("")){
            graphics.setColor(randomColor());
            graphics.drawString(ch, left, top);
            left = left + charWidth + spaceWidth;
        }

        if (withConfusion) {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i=0;i<3;i++) {
                int x1 = random.nextInt(w / 2) + 10;
                int y1 = random.nextInt(h);
                int x2 = random.nextInt(w / 2)  + w/2 - 10;
                int y2 = random.nextInt(h);
                graphics2D.setColor(randomColor());
                graphics2D.drawLine(x1, y1, x2, y2);
            }
            for (int i=0;i<8;i++) {
                graphics2D.setColor(randomColor());
                graphics2D.fillOval(random.nextInt(w), random.nextInt(h), 4, 4);
                graphics2D.fillOval(random.nextInt(w), random.nextInt(h), 4, 4);
            }
        }
        ImageIO.write(image, "jpeg", out);
        out.flush();
    }

    public static String[] genArithmeticCaptcha() {
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        int operator = random.nextInt(2);

        if (operator == 0) {
            String expression = String.format("%d%s%d=?", a, "+", b);
            return new String[]{expression, String.valueOf(a + b)};
        } else {
            String expression = String.format("%d%s%d = ?", a, "-", b);
            return new String[]{expression, String.valueOf(a - b)};
        }
    }

    private static Color randomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
