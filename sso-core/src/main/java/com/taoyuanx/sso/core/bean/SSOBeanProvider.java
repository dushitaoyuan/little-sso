package com.taoyuanx.sso.core.bean;

/**
 * @author dushitaoyuan
 * @desc bean获取
 * @date 2020/12/29
 */
public interface SSOBeanProvider {
    <T> T getBean(Class<T> clazz);
}
