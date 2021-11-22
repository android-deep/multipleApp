package com.ft.mapp.home;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;

import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.jaeger.library.StatusBarUtil;
import com.xqb.user.net.engine.ApiServiceDelegate;
import com.xqb.user.util.StatUtils;

import androidx.annotation.Nullable;

public class PrivacyPolicyActivity extends VActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.activity_privacy_policy);

        WebView webView = findViewById(R.id.privacy_policy_webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        String url = "http://app.fntmob.com/uploads/file/POLICY/"+ ApiServiceDelegate.APP_KEY+"/"+ StatUtils.getChannel(this) +".HTML";
        webView.loadUrl(url);

//        ScrollView scrollView = findViewById(R.id.scroll_view);
//        scrollView.smoothScrollTo(0,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
