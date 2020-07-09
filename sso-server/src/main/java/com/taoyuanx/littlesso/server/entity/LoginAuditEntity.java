package com.taoyuanx.littlesso.server.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lianglei
 * @date 2019/1/19 13:45
 * @desc 登录审计实体
 **/
@Data
public class LoginAuditEntity implements Serializable {

    public static final Integer OP_RESULT_SUCCESS = 1;
    public static final Integer OP_RESULT_FAILED = 0;
    private Long id;

    private String sessionId;
    private Long userId;
    private Long userCenterId;

    /**
     * 业务id
     */
    private String appSysId;
    /**
     * 用户名称
     */
    private String username;

    /**
     * 账号
     */
    private String accountNum;
    /**
     * 用户证书 sn号
     */
    private String userSn;

    private String opAddress;
    private Date opDate;
    private Integer opResult;
    private String opDesc;
}
