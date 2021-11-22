package com.xqb.user.net.converter;

/**
 * File description
 *
 * @date 2018/11/14
 */
public class ApiException extends RuntimeException {

    private int code;//错误码

    public ApiException(int code, String defaultMessage) {
        this(getErrorMessage(code, defaultMessage));
        this.code = code;
    }

    public ApiException(int code) {
        this(getErrorMessage(code, null));
        this.code = code;
    }

    public ApiException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

    private static String getErrorMessage(int code, String defaultMessage) {
        return defaultMessage;
    }

}
