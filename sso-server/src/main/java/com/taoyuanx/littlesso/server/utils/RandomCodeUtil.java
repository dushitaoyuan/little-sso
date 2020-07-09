package com.taoyuanx.littlesso.server.utils;

import java.util.Random;

/**
 * @author dushitaoyuan
 * @desc 随机码工具类
 * @date 2020/1/3
 */
public class RandomCodeUtil {
    /**
     * 随机生成字符
     *
     * @return
     */
    private static char[] rand_chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
            't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    public static String getRandCode(int len) {
        StringBuilder pwd = new StringBuilder(len);
        Random r = new Random();
        for (int i = 0; i < len; i++) {
            pwd.append(rand_chars[r.nextInt(rand_chars.length)]);
        }
        return pwd.toString();

    }


}
