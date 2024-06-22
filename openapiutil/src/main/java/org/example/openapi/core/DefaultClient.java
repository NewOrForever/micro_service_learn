package org.example.openapi.core;

import org.apache.http.HttpVersion;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.example.openapi.exception.OpenApiException;
import org.example.openapi.httpclient.HttpClientHelper;
import org.example.openapi.httpclient.HttpRequestBuilder;

/**
 * 默认的Client
 */
public class DefaultClient implements IClient {

    private int connectTimeout = 180000;
    private int socketTimeout = 180000;


    @Override
    public ApiResponse executeRequest(ApiRequest apiRequest, RuntimeOptions runtimeOptions) {
        try {
            ApiRequest.check(apiRequest);

            String url = apiRequest.composeUrl();

            // 获取httpclient
            CloseableHttpClient client = HttpClientHelper.getHttpClient(apiRequest.getHost(), apiRequest.getPort());

            HttpUriRequest request = HttpRequestBuilder.create(apiRequest).url(url).version(HttpVersion.HTTP_1_1).header(apiRequest.getHeaders()).buildHttpRequest();

            CloseableHttpResponse response = client.execute(request);
            return new ApiResponse(response);
        } catch (Exception e) {
            throw new OpenApiException(e.getMessage(), e);
        }
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        this.socketTimeout = socketTimeout;
    }
}

