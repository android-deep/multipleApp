package com.ft.mapp.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

public class LollipopFixedWebView extends WebView {
    public LollipopFixedWebView(Context context) {
        super(context);
    }

    public LollipopFixedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LollipopFixedWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LollipopFixedWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public static Context getFixedContext(Context context) {
        return context.createConfigurationContext(new Configuration());
    }

}
