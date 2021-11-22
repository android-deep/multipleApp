package com.ft.mapp.ad.base;

import android.view.View;

public interface SplashAdListener {
    void onError(int code, String msg);

    void onTimeout();

    void onSplashAdLoad(View adView);

    void onAdClicked();

    void onAdShow(View adView);

    void onAdSkip();

    void onAdClose();
}
