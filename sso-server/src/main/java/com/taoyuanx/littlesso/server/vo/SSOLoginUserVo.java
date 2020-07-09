package com.taoyuanx.littlesso.server.vo;

import com.taoyuanx.littlesso.server.entity.AppSysInfoEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author dushitaoyuan
 * @desc 登录对象封装
 * @date 2019/12/17
 */
@Data
public class SSOLoginUserVo {
    /**
     * 账户id，用户id
     */
    private Long accountId;
    private Long userId;
    private String username;

    //用户所属中心id
    private Integer centerId;
    private String userSn;

    private Integer userType;
    //账户名称
    private String accountName;

    private String clientIp;
    private Date loginDate;
    //会话id
    private String sessionId;
    //部门
    private Long deptId;
    private String deptName;


    private String ticket;
    /**
     * 已授权业务列表
     */
    private List<AppSysInfoEntity> authedAppSys;
}
