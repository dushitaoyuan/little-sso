package com.ncs.ticket.entity;

import lombok.Data;

@Data
public class Ticket {
    // 客户端的IP地址
    private String clientAddress;
    // 登陆时间
    private Long loginTime;
    // 用户id
    private Long userId;
    // 用户名称（登录账号）
    private String userName;
    //用户姓名
    private String realName;
    //会话id
    private String sessionId;
    //用户中心id
    private String userCenterId;
    //用户Sn
    private String userSn;
    //分中心名称
    private String centerName;
    /*登陆类型，1:本地登录，2：代理登录，3：托管登录*/
    private Integer loginType;
    //代理人
    private String agent;
    //代理人ID
    private String agentId;
    //代理人所在省
    private String agentProvince;
    //托管中心
    private String trusteeshipCentre;
    // 用户账户
    private String userAccount;
    // 部门id
    private String deptId;
    // 部门名称
    private String deptName;

    //用户类型
    private String userType;

    //部门编号-(按层级划分)
    private String orgCode;
    //归属地编号
    private String areaCode;

    //票据的创建时间
    private Long createTime;
    //票据过期时间
    private Long endTime;
}
