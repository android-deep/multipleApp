package com.ft.mapp.home;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ft.mapp.R;
import com.ft.mapp.home.pipi.AdWebActivity;
import com.ft.mapp.utils.ToastUtil;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class WebFragment extends Fragment {

    private WebView webView;

    //    https://w.qirexue.com/lmaid/746472
    public WebFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = view.findViewById(R.id.web_view_cus);
        initWebView();
//        webView.loadUrl("https://w.qirexue.com/lmaid/746472");
    }

    @SuppressLint("JavascriptInterface")
    private void initWebView() {
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();

        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.setJavaScriptEnabled(true);

        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        //其他细节操作
        webSettings.setCacheMode(WebSettings.LOAD_NORMAL); //关闭webview中缓存
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webView.addJavascriptInterface(requireActivity(), "test");
        webSettings.setAllowFileAccess(true); // 允许访问文件
        webSettings.setSupportZoom(true); // 支持缩放
        webSettings.setLoadWithOverviewMode(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        webSettings.setAppCacheEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }
        webSettings.setUseWideViewPort(true); // 关键点
        webSettings.setAllowFileAccessFromFileURLs(false);
        webSettings.setAllowUniversalAccessFromFileURLs(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//        webView.setWebChromeClient(webchromeclient);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView webView, String s) {
                super.onPageFinished(webView, s);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("webxiaoshuo","url = "+url);
                if (url.contains("login")){
                    ToastUtil.show(requireContext(),"请先登录");
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                super.onReceivedError(webView, i, s, s1);
            }

        });
    }
}
