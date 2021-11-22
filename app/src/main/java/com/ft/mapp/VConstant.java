package com.ft.mapp;

import android.os.Build;

import com.ft.mapp.home.models.AppData;
import com.ft.mapp.home.models.RcmdAppData;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.server.bit64.Bit64Utils;
import com.xqb.user.net.engine.UserAgent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class VConstant {
    public static final String TikTok_PKG = "com.ss.android.ugc.trill";
    public static final String TikTok_PKG2 = "com.zhiliaoapp.musically";
    public static final String TikTok_PKG3 = "com.ss.android.ugc.aweme.lite";
    public static final String DOU_YIN_PKG = "com.ss.android.ugc.aweme";
    public static final String FB_PKG = "com.facebook.katana";
    public static final String PUBG_MOBILE_PACKAGE_NAME = "com.tencent.ig";
    public static final String PUBG2_MOBILE_PACKAGE_NAME = "com.tencent.tmgp.pubgmhd";
    public static final String SUBWAYSURF_PACKAGE_NAME = "com.kiloo.subwaysurf";
    public static final String MOBILE_LEGENDS_PACKAGE_NAME = "com.mobile.legends";
    public static final String AOV_PACKAGE_NAME = "com.garena.game.kgtw";
    public static final String HOLEIO_PACKAGE_NAME = "io.voodoo.holeio";
    public static final String RISEUP_PACKAGE_NAME = "com.riseup.game";
    public static final String BIUGO_PACKAGE_NAME = "com.yy.biu";
    public static final String TX_QQ = "com.tencent.mobileqq";
    public static final int BIT_64_VERSION = 106;

    public static final Set<String> sRcmdPkgs = new HashSet<>();

    static {
        sRcmdPkgs.add("com.tencent.mm");
        sRcmdPkgs.add(TX_QQ);
        sRcmdPkgs.add("com.immomo.momo");
        sRcmdPkgs.add(TikTok_PKG);
        sRcmdPkgs.add(TikTok_PKG2);
        sRcmdPkgs.add(DOU_YIN_PKG);
//        sRcmdPkgs.add(FB_PKG);
//        sRcmdPkgs.add(SUBWAYSURF_PACKAGE_NAME);
//        sRcmdPkgs.add(MOBILE_LEGENDS_PACKAGE_NAME);
//        sRcmdPkgs.add(AOV_PACKAGE_NAME);
//        sRcmdPkgs.add(HOLEIO_PACKAGE_NAME);
//        sRcmdPkgs.add(RISEUP_PACKAGE_NAME);
//        sRcmdPkgs.add(BIUGO_PACKAGE_NAME);
    }

    public static HashMap<String, AppData> getRcmdAppDataList() {
        HashMap<String, AppData> appDataList = new HashMap<>();

        for (String pkg : sRcmdPkgs) {
            if(!UserAgent.getInstance(VApp.getApp()).isVirtualLocationOn()&& Bit64Utils.isRunOn64BitProcess(pkg)){
                continue;
            }
            if (Build.VERSION.SDK_INT >= 29
                    && pkg.equals(DOU_YIN_PKG)) {
                continue;
            }
            if (VCore.get().isOutsideInstalled(pkg)) {
                appDataList.put(pkg, new RcmdAppData(pkg));
            }
        }

        return appDataList;
    }

}
