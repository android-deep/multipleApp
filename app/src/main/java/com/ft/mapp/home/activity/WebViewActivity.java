package com.ft.mapp.home.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ft.mapp.R;
import com.ft.mapp.utils.ShopAppUtil;
import com.jaeger.library.StatusBarUtil;

import java.net.URLDecoder;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class WebViewActivity extends AppCompatActivity {
    private WebView webview;
    private static final String URL = "URL";
    private boolean firstLoad = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_webview_layout);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorAccent));
        webview = findViewById(R.id.webview);
        String url = getIntent().getStringExtra(URL);
        final String loginUrlTag = "https://login.m.taobao.com/login.htm?";
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, "url为空，打开失败", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        webview.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url1 = request.getUrl().toString();
                Log.i("overrideUrlLoading-----", "url = " + url1);
//                if (firstLoad) {
//                    firstLoad = false;
//                    return super.shouldOverrideUrlLoading(view, request);
//                } else {
//                    ShopAppUtil.openTaoBaoApp(WebViewActivity.this, "", url1);
////                    return true;
//                    return super.shouldOverrideUrlLoading(view, request);
//                }
                if (firstLoad) {
                    Disposable subscribe = Observable.just(0).delay(3000, TimeUnit.MILLISECONDS)
                            .subscribe(integer -> firstLoad = false);
                    return super.shouldOverrideUrlLoading(view, request);
                } else {
                    if (url1.startsWith(loginUrlTag)) {
                        int i = url1.indexOf("=");
                        String substring = url1.substring(i + 1);
                        String decode = URLDecoder.decode(substring);
                        ShopAppUtil.openTaoBaoApp(WebViewActivity.this, "", decode);
                        return true;
                    } else {
                        ShopAppUtil.openTaoBaoApp(WebViewActivity.this, "", url1);
                        return true;
                    }
                }

//                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i("onPageFinished-----", "url = " + url);
            }
        });
        webview.loadUrl(url);
    }

    public static void start(Context context, String url) {
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(URL, url);
        context.startActivity(starter);
    }

}
