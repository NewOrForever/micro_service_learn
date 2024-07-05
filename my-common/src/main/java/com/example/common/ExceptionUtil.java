package com.example.common;

/**
 * ClassName:ExceptionUtil
 * Package:com.example.common
 * Description: 异常工具类
 *
 * @Date:2024/7/4 14:19
 * @Author:qs@1.com
 */
public class ExceptionUtil {
    /**
     * 获取异常的完整描述
     *
     * @param e 异常
     * @return 异常的完整描述
     */
    public static String exceptionFullDesc(final Throwable e) {
        StringBuilder sb = new StringBuilder();
        if (e != null) {
            sb.append(e.toString()).append("\r\n");

            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                for (StackTraceElement element : stackTrace) {
                    sb.append(element.toString()).append("\r\n");
                }
            }
        }

        return sb.toString();
    }

    /**
     * 获取异常的简单描述
     *
     * @param e 异常
     * @return 异常的简单描述
     */
    public static String exceptionSimpleDesc(final Throwable e) {
        StringBuilder sb = new StringBuilder();
        if (e != null) {
            sb.append(e.toString());

            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace != null && stackTrace.length > 0) {
                StackTraceElement element = stackTrace[0];
                sb.append(", ");
                sb.append(element.toString());
            }
        }

        return sb.toString();
    }

}
