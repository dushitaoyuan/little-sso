package com.ncs.login;

import com.ncs.pm.commons.utils.PasswordUtil;
import org.junit.Test;

/**
 * @author lianglei
 * @date 2019/1/19 11:27
 * @desc 密码测试
 **/
public class PasswordTest {
    @Test
    public  void demoPasswordTest(){
        System.out.println(PasswordUtil.passwordEncode("123456"));
    }
}
