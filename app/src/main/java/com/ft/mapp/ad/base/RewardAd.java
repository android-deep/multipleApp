package com.ft.mapp.ad.base;

import android.app.Activity;

public abstract class RewardAd {

    protected Activity activity;

    protected String TAG = "MyRewardAd";

    protected int AD_TIME_OUT = 3000;

    protected RewardAdListener listener;

    public void setAdListener(RewardAdListener listener) {
        this.listener = listener;
    }

    public abstract void loadReward(Activity activity, String adCode);

}
