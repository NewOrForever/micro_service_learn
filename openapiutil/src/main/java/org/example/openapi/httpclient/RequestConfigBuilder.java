package org.example.openapi.httpclient;

import org.apache.http.client.config.RequestConfig;

import java.util.Map;

/**
 * ClassName:RequestConfigBuilder
 * Package:org.example.httpclient
 * Description:
 *
 * @Date:2024/6/21 15:40
 * @Author:qs@1.com
 */
public class RequestConfigBuilder {
    private RequestConfig.Builder builder;

    public static int SOCKET_TIMEOUT = 180000;
    public static int CONNECT_TIMEOUT = 180000;

    public RequestConfigBuilder() {
        builder = RequestConfig.custom();
    }

    public RequestConfigBuilder connectTimeout(Map<String, Object> map) {
        int timeout;
        if (map == null) {
            timeout = CONNECT_TIMEOUT;
        } else {
            Object object = map.get("connectTimeout");
            try {
                timeout = Integer.parseInt(String.valueOf(object));
            } catch (Exception e) {
                timeout = CONNECT_TIMEOUT;
            }
        }

        this.builder.setConnectTimeout(timeout);
        return this;
    }


    public RequestConfigBuilder socketTimeout(Map<String, Object> map) {
        int timeout;
        if (map == null) {
            timeout = SOCKET_TIMEOUT;
        } else {
            Object object = map.get("socketTimeout");
            try {
                timeout = Integer.parseInt(String.valueOf(object));
            } catch (Exception e) {
                timeout = SOCKET_TIMEOUT;
            }
        }

        this.builder.setSocketTimeout(timeout);
        return this;
    }

    public RequestConfig buildRequestConfig() {
        return this.builder.build();
    }

}
