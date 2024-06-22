package org.example.openapi.exception;

import java.util.Map;

/**
 * ClassName:OpenApiException
 * Package:org.example.exception
 * Description:
 *
 * @Date:2024/6/21 14:55
 * @Author:qs@1.com
 */
public class OpenApiException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String code;
    private String message;
    private Map<String, Object> data;

    public OpenApiException() {
    }

    public OpenApiException(String message) {
        super(message);
        this.message = message;
    }

    public OpenApiException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }

    public OpenApiException(String code, String message, Map<String, Object> data) {
        this.setCode(code);
        this.setMessage(message);
        this.data = data;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
