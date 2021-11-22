package com.ft.mapp.utils;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.ft.mapp.home.HomeActivity;
import com.ft.mapp.home.activity.WebViewActivity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShopAppUtil {

    public static void openTaoBaoApp(Context context, String title, String url) {
        Log.i("data===", "===url===" + url);
        if (checkPackage(context, "com.taobao.taobao")) {
            if (url.startsWith("https://")) {
                url = url.replaceFirst("https://", "taobao://");
            } else if (url.startsWith("http://")) {
                url = url.replaceFirst("http://", "taobao://");
            } else if (url.startsWith("tbopen://")) {
                url = url.replaceFirst("tbopen://", "taobao://");
            }
            try {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri uri = Uri.parse(url);
                intent.setData(uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                openWebView(context, title, url);
            }
        } else {
            openWebView(context, title, url);
        }
    }

    public static void openMeiTuanApp(Context context, String url) {
        Log.i("data===", "===url===" + url);
        try {
            if (checkPackage(context, "com.sankuai.meituan")) {
                if (url.startsWith("https://")) {
                    url = url.replaceFirst("https://", "imeituan://");
                } else if (url.startsWith("http://")) {
                    url = url.replaceFirst("http://", "imeituan://");
                }
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    openWebView(context, "", url);
                }
            } else {
                openSystemWeb(context, "https://w.dianping.com/cube/evoke/zz_cps/meituan.html?lch=cps:waimai:1:929ce0f96ce81339c9165928f8b982e8:202011161500&url=https%3A%2F%2Frunion.meituan.com%2Furl%3Fkey%3D929ce0f96ce81339c9165928f8b982e8%26url%3Dhttps%253A%252F%252Fi.meituan.com%252Fawp%252Fhfe%252Fblock%252Findex.html%253Fcube_h%253D3c2965e3772fbc38d508%2526cube_i%253D79098%2526appkey%253D929ce0f96ce81339c9165928f8b982e8%253A202011161500%26sid%3D202011161500");
//                openWebView(context, title, url);
            }
        } catch (Exception e) {

        }
    }

    public static void openTianMaoApp(Context context, String title, String url) {
        if (checkPackage(context, "com.tmall.wireless")) {
            if (url.startsWith("https://")) {
                url = url.replaceFirst("https://", "tmall://");
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            openWebView(context, title, url);
        }
    }

    public static void openJingDongApp(Context context, String title, String url) {
        if (checkPackage(context, "com.jingdong.app.mall")) {
            if (url.startsWith("https://")) {
                url = url.replaceFirst("https://", "jingdong://");
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            openWebView(context, title, url);
        }
    }

    public static void openPinDuoDuoApp(Context context, String title, String url) {
        if (checkPackage(context, "com.xunmeng.pinduoduo")) {
            if (url.startsWith("https://mobile.yangkeduo.com/app.html?launch_url=")) {
                url = url.replaceFirst("https://mobile.yangkeduo.com/app.html?launch_url=", "pinduoduo://com.xunmeng.pinduoduo/");
            }
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri uri = Uri.parse("pinduoduo://com.xunmeng.pinduoduo/" + url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            openSystemWeb(context, url);
        }

    }


    public static void openWebView(Context context, String title, String url) {
        if (url.startsWith("taobao://")) {
            url = url.replaceFirst("taobao://", "https://");
        }
        if (url.startsWith("tbopen://")) {
            url = url.replaceFirst("tbopen://", "https://");
        }
        if (url.startsWith("tmall://")) {
            url = url.replaceFirst("tmall://", "https://");
        }
        if (url.startsWith("yangkeduo://")) {
            url = url.replaceFirst("yangkeduo://", "https://");
        }
        if (url.startsWith("pinduoduo://")) {
            url = url.replaceFirst("pinduoduo://", "https://");
        }
        WebViewActivity.start(context, url);
//        Intent intent = new Intent(context, WebViewActivity.class);
//        intent.putExtra(WebViewActivity.KEY_TITLE, title);
//        intent.putExtra(WebViewActivity.KEY_URL, url);
//        context.startActivity(intent);
    }

    public static void openSystemWeb(Context context, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkPackage(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) return false;
        try {
            context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }

    }

    public static void openThirdApp(@NotNull Context context, String actType, String actUrl) {
        if ("1".equals(actType)) {
            openTaoBaoApp(context, "", actUrl);
        } else {
            openMeiTuanApp(context, actUrl);
        }
    }
}