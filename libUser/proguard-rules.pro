# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep public class * extends com.xqb.user.bean.BaseBean

# OkHttp
-keepattributes Signature
-keepattributes *Annotation*
-keep interface okhttp3.** { *; }
-keep class okhttp3.** { *; }
-dontwarn okhttp3.**

-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-dontwarn org.conscrypt.OpenSSLProvider
-dontwarn org.conscrypt.Conscrypt
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations
-keepattributes EnclosingMethod
-keepclasseswithmembers class * {@retrofit2.* <methods>;}
-keepclasseswithmembers interface * {@retrofit2.* <methods>;}