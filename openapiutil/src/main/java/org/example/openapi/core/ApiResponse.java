package org.example.openapi.core;

import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.example.openapi.exception.OpenApiException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * ClassName:ApiResponse
 * Package:org.example.core
 * Description:
 *
 * @Date:2024/6/22 10:28
 * @Author:qs@1.com
 */
public class ApiResponse {
    /** http响应 */
    public CloseableHttpResponse response;
    /** http状态码 */
    public int statusCode;
    /** http状态信息 */
    public String statusMessage;
    /** http头信息 */
    public HashMap<String, String> headers;
    /** http响应体 */
    public InputStream body;

    public ApiResponse() {
        headers = new HashMap<>();
    }

    public ApiResponse(CloseableHttpResponse response) throws IOException {
        headers = new HashMap<>();
        this.response = response;
        statusCode = response.getStatusLine().getStatusCode();
        statusMessage = response.getStatusLine().getReasonPhrase();
        body = response.getEntity().getContent();
        Header[] allHeaders = response.getAllHeaders();
        // 将header转换为map
        for (Header header : allHeaders) {
            headers.put(header.getName(), header.getValue());
        }
    }

    public InputStream getResponse() {
        return this.body;
    }

    public String getResponseBody() {
        if (null == body) {
            return String.format("{\"message\":\"%s\"}", statusMessage);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        try {
            while (true) {
                final int read = body.read(buff);
                if (read == -1) {
                    break;
                }
                os.write(buff, 0, read);
            }
        } catch (Exception e) {
            throw new OpenApiException(e.getMessage(), e);
        }
        return new String(os.toByteArray());
    }


}
