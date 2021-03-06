package com.taoyuanx.sso.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * @author dushitaoyuan
 * @desc token user object data field as less as possible
 * @date 2020/12/29
 */
@Setter
@Getter
public class SSOUser implements Serializable {

    private String sessionId;
    /**
     * userId
     */
    private Long userId;

    private String username;


}
