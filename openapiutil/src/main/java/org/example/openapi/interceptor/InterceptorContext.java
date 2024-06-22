package org.example.openapi.interceptor;

import org.example.openapi.core.ApiRequest;
import org.example.openapi.core.ApiResponse;

/**
 * ClassName:InterceptorContext
 * Package:org.example.core
 * Description:
 *
 * @Date:2024/6/22 10:28
 * @Author:qs@1.com
 */
public class InterceptorContext {
    private ApiRequest apiRequest;
    private ApiResponse apiResponse;

    private InterceptorContext() {
    }

    public static InterceptorContext create() {
        return new InterceptorContext();
    }

    public ApiRequest apiRequest() {
        return apiRequest;
    }


    public ApiResponse apiResponse() {
        return apiResponse;
    }

    public InterceptorContext setApiRequest(ApiRequest apiRequest) {
        this.apiRequest = apiRequest;
        return this;
    }

    public InterceptorContext setApiResponse(ApiResponse apiResponse) {
        this.apiResponse = apiResponse;
        return this;
    }

    public InterceptorContext copy() {
        return create().setApiRequest(apiRequest)
                .setApiResponse(apiResponse);
    }
}