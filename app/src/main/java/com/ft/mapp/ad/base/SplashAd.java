package com.ft.mapp.ad.base;

public class SplashAd {

    protected String TAG = "MySplashAd";

    protected int AD_TIME_OUT = 3000;

    protected SplashAdListener listener;

    public void setAdListener(SplashAdListener listener){
        this.listener = listener;
    }

}
