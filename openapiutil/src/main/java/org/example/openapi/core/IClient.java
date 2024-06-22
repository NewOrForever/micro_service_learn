package org.example.openapi.core;

/**
 * ClassName:IClient
 * Package:org.example.pay.guangda
 * Description:
 *
 * @Date:2024/6/20 16:04
 * @Author:qs@1.com
 */
public interface IClient {
    /**
     * api 调用
     */
    ApiResponse executeRequest(ApiRequest apiRequest, RuntimeOptions runtimeOptions);

}
