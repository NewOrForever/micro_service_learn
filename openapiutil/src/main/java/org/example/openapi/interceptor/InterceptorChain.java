package org.example.openapi.interceptor;

import org.example.openapi.core.ApiRequest;
import org.example.openapi.core.ApiResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName:InterceptorChain
 * Package:org.example.core
 * Description:
 *
 * @Date:2024/6/22 10:27
 * @Author:qs@1.com
 */
public class InterceptorChain implements AutoCloseable {
    private List<RequestInterceptor> requestInterceptors = new ArrayList<>();
    private List<ResponseInterceptor> responseInterceptors = new ArrayList<>();

    private InterceptorChain() {
    }

    public static InterceptorChain create() {
        return new InterceptorChain();
    }


    public void addRequestInterceptor(RequestInterceptor interceptor) {
        this.requestInterceptors.add(interceptor);
    }

    public void addResponseInterceptor(ResponseInterceptor interceptor) {
        this.responseInterceptors.add(interceptor);
    }

    @Override
    public void close() {
        requestInterceptors.clear();
        responseInterceptors.clear();
    }

    public InterceptorContext modifyRequest(InterceptorContext context, AttributeMap attributes) {
        InterceptorContext result = context;
        for (RequestInterceptor interceptor : requestInterceptors) {
            ApiRequest interceptorResult = interceptor.modifyRequest(result, attributes);
            result.setApiRequest(interceptorResult);
        }
        return result;
    }

    public InterceptorContext modifyResponse(InterceptorContext context, AttributeMap attributes) {
        InterceptorContext result = context;
        for (ResponseInterceptor interceptor : responseInterceptors) {
            ApiResponse interceptorResult = interceptor.modifyResponse(result, attributes);
            result.setApiResponse(interceptorResult);
        }
        return result;
    }
}
