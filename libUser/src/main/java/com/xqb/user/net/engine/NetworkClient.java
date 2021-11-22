package com.xqb.user.net.engine;

import com.xqb.user.net.converter.NoSSLv3SocketFactory;

import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

public class NetworkClient {

    public static OkHttpClient.Builder getClientBuilder(boolean https) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS);
        try {
            if (https) {
                ConnectionSpec.Builder connSpecBuilder = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS);
                connSpecBuilder.tlsVersions(TlsVersion.TLS_1_0);
                X509TrustManager tm = new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {

                    }

                    public void checkServerTrusted(X509Certificate[] chain, String authType) {

                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                };
                SSLContext sc = SSLContext.getInstance("TLSv1");
                sc.init(null, new TrustManager[]{tm}, null);
                SSLSocketFactory noSSLv3Factory = new NoSSLv3SocketFactory(sc.getSocketFactory());
                okHttpBuilder.connectionSpecs(Collections.singletonList(connSpecBuilder.build()))
                        .sslSocketFactory(noSSLv3Factory, tm);
                okHttpBuilder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname != null;
                    }
                });
            }
            return okHttpBuilder;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return okHttpBuilder;
    }
}
