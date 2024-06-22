package org.example.openapi.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.InputStreamEntity;
import org.example.openapi.core.ApiRequest;

import java.util.Map;

/**
 * ClassName:HttpRequestBuilder
 * Package:org.example.httpclient
 * Description:
 *
 * @Date:2024/6/21 16:54
 * @Author:qs@1.com
 */
public class HttpRequestBuilder {
    private RequestBuilder builder;

    public HttpRequestBuilder(RequestBuilder builder) {
        this.builder = builder;
    }

    public HttpRequestBuilder url(String url) {
        this.builder.setUri(url);
        return this;
    }

    public HttpRequestBuilder version(HttpVersion version) {
        this.builder.setVersion(version);
        return this;
    }

    public HttpRequestBuilder header(Map<String, String> headers) {
        for (String headerName : headers.keySet()) {
            this.builder.addHeader(headerName, headers.get(headerName));
        }
        return this;
    }

    public static HttpRequestBuilder create(ApiRequest request) {
        RequestBuilder innerBuilder;
        String method = request.getMethod().toUpperCase();
        switch (method) {
            case "DELETE":
                innerBuilder = RequestBuilder.delete();
                break;
            case "POST":
                innerBuilder = RequestBuilder.post();
                break;
            case "PUT":
                innerBuilder = RequestBuilder.put();
                break;
            case "PATCH":
                innerBuilder = RequestBuilder.patch();
                break;
            default:
                innerBuilder = RequestBuilder.get();
                break;
        }
        HttpEntity entity = new InputStreamEntity(request.getBody());
        innerBuilder.setEntity(entity);

        return new HttpRequestBuilder(innerBuilder);
    }

    public HttpUriRequest buildHttpRequest() {
        return this.builder.build();
    }

}
