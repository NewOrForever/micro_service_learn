package org.example.openapi.core;

import org.apache.commons.lang3.StringUtils;
import org.example.openapi.exception.OpenApiException;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:ApiRequest
 * Package:org.example
 * Description:
 *
 * @Date:2024/6/21 13:24
 * @Author:qs@1.com
 */
public class ApiRequest {
    public final static String URL_ENCODING_UTF_8 = "UTF-8";
    public final static String PROTOCOL_HTTP = "http";
    public final static String PROTOCOL_HTTPS = "https";

    private String protocol;

    private String host;

    private Integer port;

    private String method;

    private String pathname;

    private Map<String, String> query;

    private Map<String, String> headers;

    private InputStream body;

    public ApiRequest() {
        protocol = "http";
        method = "GET";
        query = new HashMap<String, String>();
        headers = new HashMap<String, String>();
    }

    public static ApiRequest create() {
        return new ApiRequest();
    }

    public static void check(ApiRequest self) {
        if (self == null) {
            throw new OpenApiException("api 请求为空");
        }

        if (StringUtils.isBlank(self.host)) {
            throw new OpenApiException("host 为空");
        }
    }

    public String composeUrl() {
        Map<String, String> queries = this.query;
        String protocol = null == this.protocol ? PROTOCOL_HTTP : this.protocol;
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(protocol);
        urlBuilder.append("://").append(this.host);
        if (null != this.pathname) {
            urlBuilder.append(this.pathname);
        }
        if (queries != null && queries.size() > 0) {
            if (urlBuilder.indexOf("?") >= 1) {
                urlBuilder.append("&");
            } else {
                urlBuilder.append("?");
            }
            try {
                for (Map.Entry<String, String> entry : queries.entrySet()) {
                    String key = entry.getKey();
                    String val = entry.getValue();
                    if (val == null || "null".equals(val)) {
                        continue;
                    }
                    urlBuilder.append(URLEncoder.encode(key, URL_ENCODING_UTF_8));
                    urlBuilder.append("=");
                    urlBuilder.append(URLEncoder.encode(val, URL_ENCODING_UTF_8));
                    urlBuilder.append("&");
                }
            } catch (Exception e) {
                throw new OpenApiException(e.getMessage(), e);
            }
            int strIndex = urlBuilder.length();
            urlBuilder.deleteCharAt(strIndex - 1);
        }
        return urlBuilder.toString();
    }


    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPathname() {
        return pathname;
    }

    public void setPathname(String pathname) {
        this.pathname = pathname;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public void setQuery(Map<String, String> query) {
        this.query = query;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public InputStream getBody() {
        return body;
    }

    public void setBody(InputStream body) {
        this.body = body;
    }

    @Override
    public String toString() {
        String output = "Protocol: " + this.protocol + "\nHost:" + this.host + "\nPort: " + this.port + "\n" + this.method + " " + this.pathname
                + "\n";
        output += "Query:\n";
        for (Map.Entry<String, String> e : this.query.entrySet()) {
            output += "    " + e.getKey() + ": " + e.getValue() + "\n";
        }
        output += "Headers:\n";
        for (Map.Entry<String, String> e : this.headers.entrySet()) {
            output += "    " + e.getKey() + ": " + e.getValue() + "\n";
        }
        return output;
    }
}