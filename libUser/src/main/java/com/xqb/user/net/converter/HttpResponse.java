package com.xqb.user.net.converter;


public class HttpResponse {
    public int errcode;
    public String message;

    public boolean isSuccessful() {
        return errcode == 0;
    }
}
