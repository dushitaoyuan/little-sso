package com.taoyuanx.littlesso.server.entity;

import lombok.Data;

/**
 * @author lianglei
 * @date 2019/1/8 13:41
 * @desc 业务实体
 **/
@Data
public class AppSysInfoEntity {
    /**
     * 业务id
     */
    private Long appSysId;
    /**
     * 业务名称
     */
    private String appSysName;
    /**
     * 业务url
     */
    private String appSysUrl;

    /**
     * logo图
     */
    private byte[] logoImageBytes;

    /**
     * 是否过期:2、即将过期,已过期1，正常 0
     */
    private Integer status;

    /**
     * 是否最新业务 ：1是0否
     */
    private Integer newAppsys;

}
