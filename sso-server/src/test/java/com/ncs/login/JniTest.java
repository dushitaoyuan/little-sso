package com.ncs.login;

import org.junit.Test;

/**
 * @author lianglei
 * @date 2019/1/15 17:36
 * @desc jni测试
 **/
public class JniTest {
    @Test
    public void jniTest() throws Exception {
        int value = 0;
        System.out.println(Integer.valueOf(5636176).equals(value));
        Object v=value;
        Integer success=0;
        System.out.println(success.equals(value));
    }

}
