package com.ft.mapp.utils;

import com.ipaynow.plugin.log.LogUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by yuyang on 2018/12/19.
 */
public class UrlEncodeUtils {

    public static String urlDecode(String value) {
        String result = "";
        try {
            result = URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("URL解码失败");
        }
        return result;
    }

    public static String urlEncode(String value) {
        String result = "";
        try {
            result = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.e("URL编码失败");
        }
        return result;
    }
}
