package com.taoyuanx.sso.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author dushitaoyuan
 * 字符串工具
 */
public class StrUtil {

    public static boolean isEmpty(String str) {
        return null == str || "".equals(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 参照log4j  模板匹配
     * <p>
     * xx{}xx{} 1,2
     *
     * @param pattern
     * @param objects
     * @return
     */
    public static String log4jFormat(String pattern, Object... objects) {
        if (isEmpty(pattern)) {
            return null;
        }
        char[] arr = pattern.toCharArray();
        StringBuilder temp = new StringBuilder();
        int count = 0, objLen = objects.length, len = arr.length;
        char left = "{".charAt(0);
        char right = "}".charAt(0);
        for (int i = 0; i < len; i++) {
            if (count < objLen) {
                if (i < len - 1 && left == arr[i] && right == arr[i + 1]) {
                    temp.append(objects[count++]);
                    i++;
                } else {
                    temp.append(arr[i]);
                }
            } else {
                temp.append(arr[i]);
            }
        }
        return temp.toString();

    }
    public static String[] tokenizeToStringArray(String str, String delimiters) {
        return tokenizeToStringArray(str, delimiters, true, true);
    }

    public static String[] tokenizeToStringArray(
            String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

        if (str == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(str, delimiters);
        List tokens = new ArrayList();
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (trimTokens) {
                token = token.trim();
            }
            if (!ignoreEmptyTokens || token.length() > 0) {
                tokens.add(token);
            }
        }
        return toStringArray(tokens);
    }

    public static String[] toStringArray(Collection collection) {
        if (collection == null) {
            return null;
        }
        return (String[]) collection.toArray(new String[collection.size()]);
    }
}
