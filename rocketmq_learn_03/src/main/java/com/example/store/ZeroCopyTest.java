package com.example.store;

import sun.nio.ch.FileChannelImpl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * ClassName:ZeroCopy
 * Package:com.example.store
 * Description:
 *
 * @Date:2024/7/26 14:15
 * @Author:qs@1.com
 */
public class ZeroCopyTest {
    public static void main(String[] args) {
        /**
         * 使用 {@link java.nio.channels.FileChannel#transferTo} 方法进行零拷贝
         */
    }
}
