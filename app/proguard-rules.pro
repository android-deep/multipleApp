# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-dontshrink
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature,EnclosingMethod
-keepclassmembers class * implements java.io.Serializable {*;}

-dontwarn android.**
-dontwarn com.tencent.**
-dontwarn andhook.**
-dontwarn org.slf4j.**
-dontwarn org.eclipse.**
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-keep class com.baidu.vi.** {*;}
-dontwarn com.baidu.**
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.ContentProvider

# Parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class * extends android.os.Binder{
    public <methods>;
}

-keepclasseswithmembernames class * {
    native <methods>;
}
# android
-keep class android.**{
    *;
}


# OkDownload required begin
# okhttp https://github.com/square/okhttp/#proguard
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keep class com.liulishuo.okdownload.OkDownloadProvider {*;}

# okdownload:okhttp
-keepnames class com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection

# okdownload:sqlite
-keep class com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite {
        public com.liulishuo.okdownload.core.breakpoint.DownloadStore createRemitSelf();
        public com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite(android.content.Context);
}
-dontwarn edu.umd.cs.findbugs.annotations.SuppressFBWarnings
# OkDownload required end

-dontwarn org.slf4j.impl.StaticLoggerBinder
-dontwarn org.slf4j.impl.StaticMDCBinder
-dontwarn org.slf4j.impl.StaticMarkerBinder
-dontwarn android.content.SyncInfo

# Gson
-keep interface com.google.gson.** { *; }
-keep class com.google.gson.** { *; }


# Google play services
-dontwarn com.google.android.**
-keep class com.google.android.** {*;}
-keep class com.android.** {*;}
-keep class com.google.android.gms.ads.** {public *;}
-keep class com.google.ads.** {public *;}
-keep class com.google.android.gms.ads.identifier.** { *; }
-keep class * extends java.util.ListResourceBundle {
    protected java.lang.Object[][] getContents();
}
-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepattributes Signature
-keepclassmembers class free.vpn.unblock.proxy.turbovpn.models.** {
  *;
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
    public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

#andhook
-keep class andhook.lib.AndHook$Dalvik
-keepclassmembers class andhook.lib.AndHook$Dalvik {
   native <methods>;
}
-keep class andhook.lib.AndHook
-keepclassmembers class andhook.lib.AndHook {
   native <methods>;
}
-keep class andhook.lib.YunOSHelper
-keepclassmembers class andhook.lib.YunOSHelper {
   public *;
}

-keep class de.robv.android.xposed.*
-keepclassmembers class de.robv.android.xposed.* {
   *;
}
-keep class android.app.AndroidAppHelper
-keepclassmembers class android.app.AndroidAppHelper {
   public *;
}

-keep class andhook.lib.xposed.XC_MethodHook
-keepclassmembers class andhook.lib.xposed.XC_MethodHook {
   *;
}
-keep class andhook.lib.xposed.XC_MethodHook$*
-keepclassmembers class andhook.lib.xposed.XC_MethodHook$* {
   *;
}
-keep class * extends andhook.lib.xposed.XC_MethodHook
-keepclassmembers class * extends andhook.lib.xposed.XC_MethodHook {
   public *;
   protected *;
}
#-keep class * extends andhook.lib.xposed.XC_MethodReplacement
#-keepclassmembers class * extends andhook.lib.xposed.XC_MethodReplacement {
#   *;
#}

-keep class andhook.lib.xposed.XposedBridge
-keepclassmembers class andhook.lib.xposed.XposedBridge {
   *;
}

-keep class io.vposed.VPosed
-keepclassmembers class io.vposed.VPosed {
   public *;
}

-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}


#腾讯地图 2D sdk
-keep class com.tencent.mapsdk.**{*;}
-keep class com.tencent.tencentmap.**{*;}

-keepclassmembers class ** {
    public void on*Event(...);
}
-keep class c.t.**{*;}
-keep class com.tencent.map.geolocation.**{*;}
-keep class com.tencent.tencentmap.lbssdk.service.**{*;}

-keep class com.tencent.lbssearch.**{*;}
-keep class com.google.gson.examples.android.model.** { *; }


-dontwarn  org.eclipse.jdt.annotation.**
-dontwarn  c.t.**

# 友盟统计
-keep class org.jdeferred2.**{*;}

-keep class com.umeng.** {*;}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# 友盟分享
-dontshrink
-dontoptimize
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**

-keep public class com.umeng.socialize.* {*;}


-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
   *;
}
-keep class com.tencent.mm.opensdk.** {
   *;
}
-keep class com.tencent.wxop.** {
   *;
}
-keep class com.tencent.mm.sdk.** {
   *;
}


-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.kakao.** {*;}
-dontwarn com.kakao.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
}
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class com.xqb.user.bean.**{*;}

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.umeng.socialize.impl.ImageImpl {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
   *;
}

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepattributes Signature

-keepattributes Signature,SourceFile,LineNumberTable,Exceptions

-keep class com.taobao.android.dexposed.** {*;}
-keep class me.weishu.epic.art.** {*;}

# delete log in release mode.
-assumenosideeffects class com.taobao.android.dexposed.utility.Logger {
          public static void i(...);
          public static void w(...);
          public static void d(...);
          public static void e(...);
}

-assumenosideeffects class com.taobao.android.dexposed.utility.Debug {
          public static *** hexdump(...);
}

-keep class com.bytedance.sdk.openadsdk.** { *; }
-keep public interface com.bytedance.sdk.openadsdk.downloadnew.** {*;}
-keep class com.pgl.sys.ces.* {*;}


# 删除代码中Log相关的代码
-assumenosideeffects class cn.ipaynow.webankwallet.plugin.log.LogUtils {
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** d(...);
    public static *** e(...);
}

#---------------------------------项目专有配置区---------------------------------
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.auth.AlipaySDK{ public *;}
-keep class com.alipay.sdk.auth.APAuthInfo{ public *;}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*
-keep class cn.gov.pbc.tsm.*{*;}
-keep class com.UCMobile.PayPlugin.*{*;}
-keep class com.unionpay.*{*;}
-dontwarn com.unionpay.**

-keep class com.ipaynow.plugin.api.IpaynowPlugin{
    <fields>;
    <methods>;
}
-keep class com.ipaynow.plugin.manager.route.dto.RequestParams{
    <fields>;
    <methods>;
}
-keep class com.ipaynow.plugin.manager.route.dto.ResponseParams{
    <fields>;
    <methods>;
}
-keep class com.ipaynow.plugin.manager.route.impl.ReceivePayResult{
    <fields>;
    <methods>;
}
-keep class com.alipay.android.app.IAlixPay {
    <fields>;
    <methods>;
}
-keep class com.ipaynow.plugin.utils.StringUtils{
    <fields>;
    <methods>;
}
-keep class com.alipay.android.app.IRemoteServiceCallback {
    <fields>;
    <methods>;
}

-keep class com.ipaynow.plugin.utils.NativeUtils{
   <fields>;
   <methods>;
}

-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties { *; }

# If you DO use SQLCipher:
-keep class org.greenrobot.greendao.database.SqlCipherEncryptedHelper { *; }

# If you do NOT use SQLCipher:
-dontwarn net.sqlcipher.database.**
# If you do NOT use RxJava:
-dontwarn rx.**

-keep class com.yilan.sdk.**{
    *;
}
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
-dontwarn org.conscrypt.*
-dontwarn okio.**

###阿里云混淆
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}


###其他混淆
-keep class com.qq.e.** {
    public protected *;
}
-keep class android.support.v4.**{
    public *;
}
-keep class android.support.v7.**{
    public *;
}


-keep class org.chromium.** {*;}
-keep class org.chromium.** { *; }
-keep class aegon.chrome.** { *; }
-keep class com.kwai.**{ *; }
-dontwarn com.kwai.**
-dontwarn com.kwad.**
-dontwarn com.ksad.**
-dontwarn aegon.chrome.**


-keepclassmembers class * extends android.app.Activity { public void *(android.view.View);
}
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep class com.baidu.mobads.*.** { *; }


-keep, includedescriptorclasses class com.asus.msa.SupplementaryDID.** { *; }
-keepclasseswithmembernames class com.asus.msa.SupplementaryDID.** { *; }
-keep, includedescriptorclasses class com.asus.msa.sdid.** { *; }
-keepclasseswithmembernames class com.asus.msa.sdid.** { *; }
-keep public class com.netease.nis.sdkwrapper.Utils {public <methods>;}
-keep class com.bun.miitmdid.**{*;}
-keep class com.bun.lib.**{*;}
-keep class com.samsung.android.deviceidservice.**{*;}
-keep class a.**{*;}

-dontoptimize
-dontpreverify

-keep class com.ft.mapp.bean.** {*;}
-keep class com.ft.mapp.home.models.** {*;}
-keep class com.ft.mapp.utils.VipFunctionUtils {
     <fields>;
     <methods>;
}

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }