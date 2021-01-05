package com.taoyuanx.sso.core.token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author dushitaoyuan
 * @date 2020/11/29
 */
@Data
public class SuperToken {

    /**
     * 生效
     */
    @JsonProperty("v")
    private Long effectTime;

    @JsonProperty("e")
    private Long endTime;
    /**
     * token类型
     */
    @JsonProperty("t")
    private Integer type;
    /**
     * 创建时间
     */
    @JsonProperty("c")
    private Long createTime;

    /**
     * token 签名
     */
    @JsonIgnore
    private String sign;
    /**
     * token 数据
     */
    @JsonIgnore
    private byte[] data;

}
