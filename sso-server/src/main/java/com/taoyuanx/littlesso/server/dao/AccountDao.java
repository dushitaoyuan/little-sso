package com.taoyuanx.littlesso.server.dao;

import com.taoyuanx.littlesso.server.entity.AccountEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
 * @author lianglei
 * @date 2019/1/8 13:38
 * @desc 账户数据库接口
 **/
public interface AccountDao {
    AccountEntity findByAccountNum(@Param("accountNum") String accountNum);

    //更新账户状态
    int updateAccountStatus(@Param("accountId") Long accountId, @Param("accountStatus") Integer accountStatus);

    //锁定账户
    int lockedAccountStatus(@Param("accountId") Long accountId, @Param("accountStatus") Integer accountStatus, @Param("lockedEndtime") Date lockedEndtime);

    //解锁账户
    int unLockedAccountStatus(@Param("accountId") Long accountId, @Param("accountStatus") Integer accountStatus);
}
