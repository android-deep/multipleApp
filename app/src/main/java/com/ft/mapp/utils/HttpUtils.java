package com.ft.mapp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {
    private static OkHttpClient okHttp;

    public static OkHttpClient getOkHttp() {
        if (okHttp == null) {
            okHttp = new OkHttpClient.Builder().build();
        }
        return okHttp;
    }

    public static String getResponse(String url) {
        Request request = new Request.Builder()
                .url(url)
                .get()
                .header("User-Agent", userAgent)
                .build();
        try {
            Response response = getOkHttp().newCall(request).execute();
            return response.body().string();
        } catch (Throwable e) {
            //
        }
        return "";
    }

    public static final String userAgent = "Mozilla/5.0.html (iPhone; U; CPU iPhone OS 4_3_3 like Mac " +
            "OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) " +
            "Version/5.0.html.2 Mobile/8J2 Safari/6533.18.5";

    public static String getCompleteUrl(String text) {
        Pattern p = Pattern.compile(
                "((http|ftp|https)://)(([a-zA-Z0-9._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})*(/[a-zA-Z0-9&%_./-~-]*)?",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(text);
        boolean find = matcher.find();
        if (find) {
            return matcher.group();
        } else {
            return "";
        }
    }
}
