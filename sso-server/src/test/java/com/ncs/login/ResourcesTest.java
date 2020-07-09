package com.ncs.login;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.ncs.pm.commons.utils.HashUtil;
import com.ncs.pm.commons.utils.RSAUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author lianglei
 * @date 2019/1/16 13:11
 * @desc 资源路径测试
 **/
@Slf4j
public class ResourcesTest {
    @Test
    public void resourceClassPathtest() throws IOException {
        String rootDir = "D:/pm/root";
        Collection<File> rootCertFileList = FileUtils.listFiles(new File(rootDir), new SuffixFileFilter(new String[]{"cer", "pem", "der"}), null);

        Set<String> cache = Sets.newHashSet();
        List<X509Certificate> certificateList = Lists.newArrayListWithExpectedSize(rootCertFileList.size());
        for (File rootCert : rootCertFileList) {
            try {
                String certHash = HashUtil.hash(new FileInputStream(rootCert), HashUtil.MD5, HashUtil.HEX);
                if (cache.contains(certHash)) {
                    log.warn("[{}]根证书重复", rootCert);
                    continue;
                }
                //ncs自定义证书,x509标准解析不了,调用 jni.verifyCertValidity(userCer, rootCert);
                X509Certificate cert = RSAUtil.getPublicKeyCer(new FileInputStream(rootCert));
                cache.add(certHash);
                certificateList.add(cert);
            } catch (Exception e) {
                log.error("[{}]根证书解析失败", rootCert,e);
            }
        }
    }
}
