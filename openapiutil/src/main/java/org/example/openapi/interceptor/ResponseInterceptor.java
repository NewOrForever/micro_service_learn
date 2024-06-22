package org.example.openapi.interceptor;

import org.example.openapi.core.ApiResponse;

/**
 * ClassName:ResponseInterceptor
 * Package:org.example.core
 * Description:
 *
 * @Date:2024/6/22 10:42
 * @Author:qs@1.com
 */
public interface ResponseInterceptor {
    ApiResponse modifyResponse(InterceptorContext context, AttributeMap attributes);
}
