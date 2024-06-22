package org.example.openapi.httpclient;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName:HttpClientHelper
 * Package:org.example.httpclient
 * Description:
 *
 * @Date:2024/6/21 15:13
 * @Author:qs@1.com
 */
public class HttpClientHelper {
    public static final ConcurrentHashMap<String, CloseableHttpClient> clients = new ConcurrentHashMap<>();

    public static CloseableHttpClient getHttpClient(String host, int port, Map<String, Object> map) {
        String key = getClientKey(host, port);
        CloseableHttpClient client = clients.get(key);
        if (null == client) {
            client = creatClient(map);
            clients.put(key, client);
        }
        return client;
    }

    public static CloseableHttpClient getHttpClient(String host, int port) {
        return getHttpClient(host, port, null);
    }

    public static CloseableHttpClient creatClient(Map<String, Object> map) {
        RequestConfig requestConfig = new RequestConfigBuilder()
                .connectTimeout(map)
                .socketTimeout(map)
                .buildRequestConfig();

        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public static String getClientKey(String host, int port) {
        return String.format("%s:%d", host, port);
    }

}
