package com.taoyuanx.littlesso.server.dao;

import com.taoyuanx.littlesso.server.entity.AppSysInfoEntity;
import com.taoyuanx.littlesso.server.entity.PkcCertInfoEntity;
import com.taoyuanx.littlesso.server.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author lianglei
 * @date 2019/1/8 13:38
 * @desc 用户数据库接口
 **/
public interface UserDao {
    //账户id查找用户
    UserInfoEntity findByAccountId(@Param("accountId") Long accountId);

    //根据用户id获取证书
    PkcCertInfoEntity findUserCertByUserIndex(@Param("userIndex") Long userIndex);

    //根据证书sn获取证书
    PkcCertInfoEntity findUserCertByUserSn(@Param("userSn") String userSn);

    //根据证书id查询用户
    UserInfoEntity findUserByCertId(@Param("certId") Long certId);

    //查找用户下所有被授权的业务
    List<AppSysInfoEntity> findAllAuthedAppSysByUserId(@Param("userIndex") Long userIndex, @Param("centerId") Integer centerId);

    //获取业务信息
    AppSysInfoEntity queryAppsysInfo(@Param("appSysId") Long appSysId);
}
