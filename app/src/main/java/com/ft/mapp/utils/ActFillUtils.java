package com.ft.mapp.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ft.mapp.VApp;
import com.ft.mapp.home.activity.WebViewActivity;
import com.xqb.user.bean.VersionBean;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.net.engine.UserAgent;

public class ActFillUtils {

    public static void fillAd(ViewGroup clickArea, ImageView iv, VersionBean.AdActivity ad) {
        if (ad != null) {
            iv.setVisibility(View.VISIBLE);
            Glide.with(iv.getContext())
                    .load(ad.img)
                    .centerCrop()
                    .into(iv);
            if (clickArea != null) {
                clickArea.setOnClickListener(v -> {
                    WebViewActivity.start(clickArea.getContext(), ad.link);
                    StatAgent.onEvent(VApp.getApp(), ad.keyname);
                });
            }
            iv.setOnClickListener(v -> {
                WebViewActivity.start(iv.getContext(), ad.link);
                StatAgent.onEvent(VApp.getApp(), ad.keyname);
            });
        }
    }

    public static void fillAd(ImageView iv, VersionBean.AdActivity ad) {
        fillAd(null, iv, ad);
    }

}
