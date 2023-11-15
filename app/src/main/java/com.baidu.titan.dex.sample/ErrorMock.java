package com.baidu.titan.dex.sample;

/**
 * Author: tong
 * Date: 2023/11/14$
 */
public class ErrorMock {
    public static void stackOverflowError() {
        ErrorMock.stackOverflowError();
    }

    public static void throwRuntimeException() {
        throw new RuntimeException("ErrorMock throwRuntimeException");
    }
}
