package com.taoyuanx.littlesso.server.config;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ncs.pm.commons.utils.HashUtil;
import com.ncs.pm.commons.utils.PropertiesUtil;
import com.ncs.pm.commons.utils.RSAUtil;
import com.taoyuanx.littlesso.server.utils.FileStreamUtil;
import com.vip.vjtools.vjkit.time.DateFormatUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.util.*;

/**
 * @author lianglei
 * @date 2019/1/9 18:46
 * @desc 系统配置
 **/
@Data
@Component
@Slf4j
public class SsoServerProperties implements InitializingBean {
    //票据过期时间
    private Long ticketValidTime;
    private PublicKey serverPublicKey;
    private PrivateKey serverPrivateKey;
    private Certificate certificate;
    private String cookieDomain;

    //服务端工作模式
    private String ssoServerMode;
    //根证书目录
    private String rootCertDir;
    private List<String> certBas64List;


    private Boolean userCertExpireValidate;


    private boolean passordErrorLockEnable;
    private Integer passordErrorLockCount;
    private Integer passordErrorLockTimeWindowMin;
    private Long passordErrorLockTime;


    private String version;


    @Override
    public void afterPropertiesSet() throws Exception {
        String validTime = PropertiesUtil.getSystemProperty("sso.ticket.validTime");
        ticketValidTime = StringUtils.isNotEmpty(validTime) ? Long.parseLong(validTime) : 8 * 60 * 1000L;

        //初始化服务端证书
        String serverP12Password = PropertiesUtil.getSystemProperty("sso.server.p12_password");
        String serverP12Path = PropertiesUtil.getSystemProperty("sso.server.p12");
        InputStream p12Stream = FileStreamUtil.getFileStream(serverP12Path);
        KeyStore keyStore = RSAUtil.getKeyStore(p12Stream, serverP12Password);
        serverPublicKey = RSAUtil.getPublicKey(keyStore);
        serverPrivateKey = RSAUtil.getPrivateKey(keyStore, serverP12Password);
        certificate = RSAUtil.getCertificate(keyStore);
        cookieDomain = PropertiesUtil.getSystemProperty("sso.cookie.domain");
        rootCertDir = PropertiesUtil.getSystemProperty("sso.server.rootCertDir");

        ssoServerMode = PropertiesUtil.getSystemProperty("sso.server.mode");

        //加载根证书
        certBas64List = loadRootCert(rootCertDir);
        rootCertDir = PropertiesUtil.getSystemProperty("sso.server.rootCertDir");
        String temp = PropertiesUtil.getSystemProperty("sso.server.userCertExpireValidate");
        userCertExpireValidate = StringUtils.isEmpty(temp) ? false : Boolean.valueOf(temp);

        String passwordErrorLockEnable = PropertiesUtil.getSystemProperty("sso.server.password.error_lock_enable");
        if (StringUtils.isNotEmpty(passwordErrorLockEnable) && Boolean.valueOf(passwordErrorLockEnable)) {
            passordErrorLockEnable = true;
            passordErrorLockCount = Integer.parseInt(PropertiesUtil.getSystemProperty("sso.server.password.error_lock_count"));
            passordErrorLockTimeWindowMin = Integer.parseInt(PropertiesUtil.getSystemProperty("sso.server.password.error_lock_time_window_min"));
            passordErrorLockTime = Long.parseLong(PropertiesUtil.getSystemProperty("sso.server.password.error_lock_time_min"));

        }
        this.version = PropertiesUtil.getSystemProperty("sso.server.version", DateFormatUtil.formatDate(DateFormatUtil.PATTERN_DEFAULT, new Date()));

    }

    private List<String> loadRootCert(String rootCertDir) {
        String classpathPrefix = "classpath:";
        File rootCertDirFile = null;
        if (rootCertDir.startsWith(classpathPrefix)) {
            rootCertDirFile = new File(Thread.currentThread().getContextClassLoader()
                    .getResource(rootCertDir.replaceFirst(classpathPrefix, "")).getPath());
        } else {
            rootCertDirFile = new File(rootCertDir);
        }

        if (rootCertDirFile.exists() && rootCertDirFile.isDirectory()) {
            Collection<File> rootCertFileList = FileUtils.listFiles(rootCertDirFile, new SuffixFileFilter("cer"), null);
            if (CollectionUtil.isNotEmpty(rootCertFileList)) {
                Set<String> cache = Sets.newHashSet();
                List<String> tempCertBas64List = Lists.newArrayListWithExpectedSize(rootCertFileList.size());
                rootCertFileList.stream().forEach(rootCertFile -> {
                    try {
                        String certHash = HashUtil.hash(new FileInputStream(rootCertFile), HashUtil.MD5, HashUtil.HEX);
                        if (cache.contains(certHash)) {
                            log.warn("[{}]根证书重复", rootCertFile);
                            return;
                        }
                        cache.add(certHash);
                        tempCertBas64List.add(Base64.encodeBase64String(FileUtils.readFileToByteArray(rootCertFile)));
                    } catch (Exception e) {
                        log.error("[{}]根证书解析失败", rootCertFile, e);
                    }
                });
                return Collections.unmodifiableList(tempCertBas64List);

            }
        }


        return null;
    }


}
