package com.taoyuanx.littlesso.server.web.controller;

import com.taoyuanx.littlesso.server.dao.UserDao;
import com.taoyuanx.littlesso.server.entity.AppSysInfoEntity;
import com.taoyuanx.littlesso.server.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Controller
public class HelpController {

    @Autowired
    UserDao userDao;

    /**
     * 获取logo图
     */
    @GetMapping("logo")
    public void getLogoImage(HttpServletResponse response, Long appSysId) {
        AppSysInfoEntity appSysInfoDTO = userDao.queryAppsysInfo(appSysId);
        if (appSysInfoDTO.getLogoImageBytes() != null) {
            try {
                byte[] imgBytes = appSysInfoDTO.getLogoImageBytes();
                OutputStream outputStream = response.getOutputStream();
                outputStream.write(imgBytes);
            } catch (IOException e) {
                throw new ServiceException("图片获取失败。");
            }
        }
    }

    /**
     * 获取logo图
     * @return
     */
    @GetMapping("listAuthedAppSys")
    @ResponseBody
    public List<AppSysInfoEntity> listAuthedAppSys(Long appSysId, Integer centerId) {
        return userDao.findAllAuthedAppSysByUserId(appSysId, centerId);
    }
}
