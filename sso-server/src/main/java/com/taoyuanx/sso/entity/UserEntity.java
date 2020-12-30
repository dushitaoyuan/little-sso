package com.taoyuanx.sso.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class UserEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * username
     */
    @TableField("username")
    private String username;

    /**
     * 密码
     */
    @TableField("password")
    @JsonIgnore
    private String password;


    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;
    /**
     * 登录成功后跳转的地址
     */
    @TableField("login_redirect_url")
    private String loginRedirectUrl;

    /**
     * 正常
     */
    public static Integer STATUS_NORMAAL = 1;
    /**
     * 冻结
     */
    public static Integer STATUS_LOCKED = 0;


}
