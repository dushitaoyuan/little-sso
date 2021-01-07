package com.taoyuanx.sso.client.token;

import com.alibaba.fastjson.annotation.JSONField;
import com.taoyuanx.sso.client.dto.SSOUser;
import lombok.Data;

@Data
public class SSOToken {

    /**
     * 生效
     */
    @JSONField(name = "v")
    private Long effectTime;

    @JSONField(name = "e")
    private Long endTime;
    /**
     * token类型
     */
    @JSONField(name = "t")
    private Integer type;
    /**
     * 创建时间
     */
    @JSONField(name = "c")
    private Long createTime;
    @JSONField(name = "d")
    private String ssoUser;


}