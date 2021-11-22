package com.ft.mapp.ad.base;

public interface RewardAdListener {

    void onError(int code, String msg);

    void onTimeout();

    void onAdClicked();

    void onAdShow();

    void onRewardVerify();

    void onAdSkip();

    void onAdClose();

}
