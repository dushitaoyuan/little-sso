package com.taoyuanx.sso.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author dushitaoyuan
 * @desc token user object data field as less as possible
 * @date 2020/12/29
 */
@Setter
@Getter
public class SSOTokenUser extends SSOUser {


    private String refreshToken;

    private Long expire;

    private Integer tokenType;


}
