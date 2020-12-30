package com.taoyuanx.sso.client.utils;

import com.alibaba.fastjson.JSON;
import com.taoyuanx.sso.client.core.Result;
import com.taoyuanx.sso.client.ex.SSOClientException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.util.Objects;

@Slf4j
public class OkHttpUtil {


    public static <T> T request(OkHttpClient client, Request request, Class<T> type) throws SSOClientException {

        Response response = null, temp;
        try {
            temp = response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                if (type == null) {
                    return null;
                }
                if (response.getClass().equals(type)) {
                    response = null;
                    return (T) temp;
                } else {
                    Result result = JSON.parseObject(response.body().string(), Result.class);
                    if (type.equals(String.class)) {
                        return (T) result.getData();
                    }
                    return JSON.parseObject(result.getData(), type);
                }
            }
            throw new SSOClientException(StrUtil.log4jFormat("ssoServer 调用异常,异常结果:{}", response.body().string()));
        } catch (SSOClientException e) {
            throw e;
        } catch (Exception e) {
            throw new SSOClientException(StrUtil.log4jFormat("ssoServer调用异常,请求地址:{}", request.url()), e);
        } finally {
            if (Objects.nonNull(response)) {
                response.close();
            }
        }

    }


}
