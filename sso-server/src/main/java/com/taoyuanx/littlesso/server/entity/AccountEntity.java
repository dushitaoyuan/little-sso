package com.taoyuanx.littlesso.server.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author lianglei
 * @date 2019/1/8 13:41
 * @desc 账户实体
 **/
@Data
public class AccountEntity {

    private Long id;

    /**
     * 账号
     */
    private String accountNum;

    /**
     * 密码（MD5）
     */
    private String password;

    /**
     * 账号状态 0：有效
     * -1：失效
     * 1：冻结
     */
    private Integer status;

    /**
     * 账户锁定结束时间
     */
    private Date lockedEndtime;

    /**
     * 账号类型 1：普通账号  2：测试账号  3：虚拟账号 4：内部系统账号 5：外部系统账号
     */
    private Integer type;
}
