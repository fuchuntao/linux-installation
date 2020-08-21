package cn.meiot.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class RandomUtil {

    private static final Random ran = new Random();
    private static final String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static StringBuffer sb;

    /**
     * 生成指定长度的随机字符串
     *
     * @return
     */
    public static String getStr(Integer length) {
        sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int x = ran.nextInt(60);
            sb.append(str.charAt(x));
        }

        return sb.toString();
    }

    /**
     * 获取随机数（数字）
     *
     * @param length
     * @return
     */
    public static String getNum(Integer length) {
        sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int x = ran.nextInt(10);
            sb.append(x);
        }
        return sb.toString();
    }


}
