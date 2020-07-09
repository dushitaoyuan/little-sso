package com.taoyuanx.littlesso.server.utils;

import com.ncs.pm.commons.utils.PropertiesUtil;
import com.ncs.pm.commons.utils.SpringContextUtil;
import com.taoyuanx.littlesso.server.config.SsoServerProperties;
import org.springframework.util.StringUtils;

/**
 * @author 桃源
 * @description jsp 函数工具类
 * @date 2019/6/10
 */
public class JspFnsUtil {

    /**
     * 获取系统配置
     *
     * @param configKey
     * @return
     */
    public static String getConfig(String configKey) {
        return PropertiesUtil.getSystemProperty(configKey);
    }


    /**
     * 获取系统版本
     *
     * @return
     */
    public static String getSystemVersion() {
        SsoServerProperties config = SpringContextUtil.getBean(SsoServerProperties.class);
        return config.getVersion();
    }

    /**
     * 按长度显示字符
     *
     * @param content 原始字符
     * @param len     显示长度
     * @return
     */
    public static String showStringLen(String content, Integer len) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        int length = content.length();
        if (length < len) {
            return content;
        }
        return content.substring(0, len) + "...";
    }


}
