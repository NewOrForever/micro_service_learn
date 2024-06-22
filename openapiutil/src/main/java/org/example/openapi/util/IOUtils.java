package org.example.openapi.util;

/**
 * ClassName:IOUtil
 * Package:org.example.util
 * Description:
 *
 * @Date:2024/6/22 10:40
 * @Author:qs@1.com
 */
public class IOUtils {

    private IOUtils() {
    }

    public static void closeQuietly(AutoCloseable is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
    }

    public static void closeIfCloseable(Object maybeCloseable) {
        if (maybeCloseable instanceof AutoCloseable) {
            closeQuietly((AutoCloseable) maybeCloseable);
        }
    }

}
