package org.example.openapi.interceptor;

import org.example.openapi.core.ApiRequest;

/**
 * ClassName:RequestInterceptor
 * Package:org.example.core
 * Description:
 *
 * @Date:2024/6/22 10:31
 * @Author:qs@1.com
 */
public interface RequestInterceptor {

    ApiRequest modifyRequest(InterceptorContext context, AttributeMap attributes);

}
