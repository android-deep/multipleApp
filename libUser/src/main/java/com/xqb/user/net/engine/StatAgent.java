package com.xqb.user.net.engine;

import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;
import com.xqb.user.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class StatAgent {

    public static void onEvent(Context context, String eventId) {
        MobclickAgent.onEvent(context, eventId);
        printEventDetails(eventId, null);
    }

    public static void onEvent(Context context, String eventId, String key, String value) {
        Map<String, Object> params = new HashMap<>();
        params.put(key, value);
        onEvent(context, eventId, params);
    }


    public static void onEvent(Context context, String eventId, Map<String, Object> params) {
        MobclickAgent.onEventObject(context, eventId, params);
        printEventDetails(eventId, params);
    }

    private static void printEventDetails(String eventId, Map<String, Object> params) {
        if (!BuildConfig.DEBUG) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(eventId).append("  ");
        if (params != null && params.size() > 0) {
            for (Map.Entry<String, Object> e : params.entrySet()) {
                builder.append(e.getKey())
                        .append(" = ")
                        .append(e.getValue()).append("  ");
            }
        }
        Log.e("statEvent", builder.toString());
    }

}
