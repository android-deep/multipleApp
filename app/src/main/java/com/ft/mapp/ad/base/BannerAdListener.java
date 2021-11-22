package com.ft.mapp.ad.base;

import android.view.View;

public interface BannerAdListener {
    void onError(int code, String msg);

    void onBannerAdLoad(View adView);

    void onAdClicked();

    void onAdShow(View adView);

    void onAdClose();
}
