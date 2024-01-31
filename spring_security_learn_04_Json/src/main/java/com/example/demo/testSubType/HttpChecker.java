package com.example.demo.testSubType;

import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.api.naming.pojo.healthcheck.impl.Http;
import com.alibaba.nacos.api.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:HttpChecker
 * Package:com.example.demo.testSubType
 * Description:
 *
 * @Date:2024/1/31 14:34
 * @Author:qs@1.com
 */
public class HttpChecker extends AbstractChecker {
    public static final String TYPE = "HTTP";

    private String path = "";

    private String headers = "";

    private int expectedResponseCode = 200;

    public HttpChecker() {
        super(HttpChecker.TYPE);
    }

    public int getExpectedResponseCode() {
        return this.expectedResponseCode;
    }

    public void setExpectedResponseCode(final int expectedResponseCode) {
        this.expectedResponseCode = expectedResponseCode;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getHeaders() {
        return this.headers;
    }

    public void setHeaders(final String headers) {
        this.headers = headers;
    }

    @JsonIgnore
    public Map<String, String> getCustomHeaders() {
        if (StringUtils.isBlank(headers)) {
            return Collections.emptyMap();
        }
        final Map<String, String> headerMap = new HashMap<String, String>(16);
        for (final String s : headers.split(Constants.NAMING_HTTP_HEADER_SPILIER)) {
            final String[] splits = s.split(":");
            if (splits.length != 2) {
                continue;
            }
            headerMap.put(StringUtils.trim(splits[0]), StringUtils.trim(splits[1]));
        }
        return headerMap;
    }

}
