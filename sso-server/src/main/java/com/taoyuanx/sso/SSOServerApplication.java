package com.taoyuanx.sso;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author dushitaoyuan
 * @date 2019/9/2622:46
 */
@MapperScan(basePackages = "com.taoyuanx.sso.mapper")
@SpringBootApplication
public class SSOServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SSOServerApplication.class, args);
    }
}
