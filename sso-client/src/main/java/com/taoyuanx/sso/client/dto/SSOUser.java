package com.taoyuanx.sso.client.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @desc sso user
 * @date 2020/12/29
 */
@Setter
@Getter
public class SSOUser implements Serializable {
    private String sessionId;
    private String username;
    private Long userId;
}
