package com.ft.mapp.ad.base;

import android.view.ViewGroup;

public abstract class BannerAd {

    protected String TAG = "MyBannerAd";

    protected int AD_TIME_OUT = 3000;

    protected BannerAdListener listener;

    public void setAdListener(BannerAdListener listener) {
        this.listener = listener;
    }

    public abstract void loadBanner(ViewGroup bannerContainer, String adCode);

}
