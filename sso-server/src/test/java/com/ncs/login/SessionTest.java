package com.ncs.login;

import com.ncs.pm.commons.utils.PasswordUtil;
import com.taoyuanx.littlesso.server.utils.SessionUtil;
import org.junit.Test;

import java.util.Arrays;

/**
 * @author lianglei
 * @date 2019/1/9 17:42
 * @desc sessionid 测试
 **/
public class SessionTest {
    @Test
    public void sessionIdTest() throws Exception {
        String sessionId = SessionUtil.makeSessionId("1", "2");
        System.out.println(sessionId);
        System.out.println(SessionUtil.isValidSessionId(sessionId));
        System.out.println(Arrays.toString(SessionUtil.parseSessionData(sessionId)));
        System.out.println(PasswordUtil.passwordEncode("123456"));
    }

}
