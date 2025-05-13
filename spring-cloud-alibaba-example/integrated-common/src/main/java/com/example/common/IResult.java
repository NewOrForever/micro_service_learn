package com.example.common;

/**
 * ClassName:IResult
 * Package:com.example.common
 * Description:
 *
 * @Date:2025/3/27 16:59
 * @Author:qs@1.com
 */
public interface IResult {

    /**
     * Get result code.
     * @return result code
     */
    Integer getCode();

    /**
     * Get result message.
     * @return result message
     */
    String getMessage();

}
