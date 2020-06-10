package com.github.oahnus.luqiancommon.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Created by oahnus on 2020-06-09
 */
public class CaptchaUtils {
    private static Random random = new Random();

    public static void captcha(String captcha, OutputStream out) throws IOException {
        genCaptchaImg(captcha, out, false);
    }

    public static void captcha(String captcha, OutputStream out, Boolean withConfusion) throws IOException {
        genCaptchaImg(captcha, out, withConfusion);
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

    private static void genCaptchaImg(String captcha, OutputStream out, Boolean withConfusion) throws IOException {
        int w = 100, h = 40;
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, w, h);
        graphics.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));

        int offset = 10;
        for (String ch : captcha.split("")){
            graphics.setColor(randomColor());
            graphics.drawString(ch, offset, (h * 2 / 3));
            offset += 20;
        }

        if (withConfusion) {
            Graphics2D graphics2D = (Graphics2D) graphics;
            graphics2D.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (int i=0;i<3;i++) {
                int x1 = random.nextInt(w / 2) + 10;
                int y1 = random.nextInt(h);
                int x2 = random.nextInt(w / 2)  + w/2 - 10;
                int y2 = random.nextInt(h);
                graphics2D.drawLine(x1, y1, x2, y2);
                graphics2D.setColor(randomColor());
                graphics2D.fillOval(random.nextInt(w), random.nextInt(h), 4, 4);
                graphics2D.fillOval(random.nextInt(w), random.nextInt(h), 4, 4);
            }
        }
        ImageIO.write(image, "jpeg", out);
        out.flush();
    }

    private static Color randomColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}
