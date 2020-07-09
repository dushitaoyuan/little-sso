package com.taoyuanx.littlesso.server.entity;

import lombok.Data;

@Data
public class UserInfoEntity {

    private Long userIndex;


    private Long userId;

    /**
     * 用户名称（与账号区分）
     */

    private String username;

    private Integer centerId;

    private Long orgIndex;

    /**
     * 用户状态 0：正常  1：删除
     */
    private Integer status;


    private String email;

    private String identityNo;

    private Long certId;
}