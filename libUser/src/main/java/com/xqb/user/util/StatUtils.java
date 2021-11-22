package com.xqb.user.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.RequiresApi;

import com.xqb.user.BuildConfig;


public class StatUtils {

    private StatUtils() {

    }

    private static volatile String sCountryCode;
    private static volatile String sImsi = null;

    /**
     * 获得当前Apk的版本数，如：10000
     */
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获得当前Apk的版本名，如：10000
     */
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "invalid";
        }
    }

    /**
     * 获得手机注册网络的所在国家代码(大写)，错误返回'??'
     */
    private static String getCountryCode(Context context) {
        if (context == null) {
            return Locale.getDefault().getCountry();
        }
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String country = null;
        if (telephonyManager != null) {
            country = telephonyManager.getNetworkCountryIso();
        }

        if (TextUtils.isEmpty(country)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                country = context.getResources().getConfiguration().getLocales().get(0).getCountry();
            } else {
                country = context.getResources().getConfiguration().locale.getCountry();
            }
        }

        if (country != null && country.length() == 2) {
            sCountryCode = country.toUpperCase(Locale.US);
        } else {
            sCountryCode = "??";
        }
        return sCountryCode;
    }

    public static String getCachedCountryCode(Context context) {
        if (TextUtils.isEmpty(sCountryCode)) {
            return getCountryCode(context);
        } else {
            return sCountryCode;
        }
    }

    public static String getHexToken(String originText) {
        if (TextUtils.isEmpty(originText)) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(originText.getBytes());
            return bytesToHex(md.digest());
        } catch (Exception e) {
            return null;
        }
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String getImsi(Context context) {
        if (sImsi == null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                String imsi = tm.getSimOperator();
                if (!TextUtils.isEmpty(imsi)) {
                    sImsi = imsi;
                } else {
                    sImsi = "";
                }
            }
        }
        return sImsi;
    }

    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    public static String generateHeaderToken(Context context, String versionCode, String nonce) {
        StringBuilder builder = new StringBuilder();
        try {
            builder.append("apk_sign")
                    .append(StatUtils.getAppSignature(context))
                    .append("app_ver_code").append(versionCode)
                    .append("nonce").append(nonce)
                    .append("auth_appkey").append("MuYmxvY2twcm94eXYZbnByb29ubGluZY");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(builder.toString().getBytes());
            return StatUtils.bytesToHex(md.digest());
        } catch (Exception e) {
            return "";
        }
    }

    public static String getAppSignature(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return getSignatureApi28(context);
        } else {
            return getSignature(context);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("PackageManagerGetSignatures")
    private static String getSignature(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                if (!TextUtils.isEmpty(currentSignature)) {
                    return currentSignature.trim();
                }
            }
        } catch (Throwable e) {
            //
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private static String getSignatureApi28(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNING_CERTIFICATES);
            SigningInfo info = packageInfo.signingInfo;
            Signature[] signatures;
            if (info.hasMultipleSigners()) {
                signatures = info.getApkContentsSigners();
            } else {
                signatures = info.getSigningCertificateHistory();
            }
            for (Signature signature : signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());
                String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                if (!TextUtils.isEmpty(currentSignature)) {
                    return currentSignature.trim();
                }
            }
        } catch (Throwable e) {
            //
        }
        return null;
    }

    public static String getMetaData(Context context, String name) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            return String.valueOf(ai.metaData.get(name));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "common";
    }

    public static String getChannel(Context context){
        if (BuildConfig.DEBUG){
//            return "sougou_1";
            return "guanwang";
        }else{
            return getMetaData(context,"UMENG_CHANNEL");
        }
    }

    public static int getGDTChannelCode(Context context){
        String channel = StatUtils.getMetaData(context, "UMENG_CHANNEL");
        switch (channel){
            case "baidu":
                return 1;
            case "toutiao":
                return 2;
            case "guangdiantong":
                return 3;
            case "sougou":
                return 4;
            case "other":
                return 5;
            case "oppo":
                return 6;
            case "vivo":
                return 7;
            case "huawei":
                return 8;
            case "yingyongbao":
                return 9;
            case "xiaomi":
                return 10;
            case "jinli":
                return 11;
            case "baidushoujiizhushou":
                return 12;
            case "meizu":
                return 13;
            default:
                return 999;
        }
    }
}
