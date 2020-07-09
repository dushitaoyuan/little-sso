package com.taoyuanx.littlesso.server.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author lianglei
 * @date 2019/1/10 14:36
 * @desc 读取文件流工具
 **/
public class FileStreamUtil {
    private static String CLASSPATH_PREFIX = "classpath:";


    public static InputStream getFileStream(String filePath) throws Exception {
        if (filePath.startsWith(CLASSPATH_PREFIX)) {
            filePath = filePath.replaceFirst(CLASSPATH_PREFIX, "");
            return FileStreamUtil.class.getClassLoader().getResourceAsStream(filePath);
        }
        File file = new File(filePath);
        if (file.exists()) {
            return new FileInputStream(file);
        }
        return null;
    }


}
