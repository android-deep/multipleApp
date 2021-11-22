package com.xqb.user.bean;

import androidx.annotation.Keep;

import com.google.gson.annotations.SerializedName;

import java.util.List;


@Keep
public class VersionBean extends BaseBean {

    public static final int AD_STATUS_ON = 1;
    public static final int AD_STATUS_OFF = 0;

    public String version;
    public int version_code;
    public String description;
    public boolean force;
    public boolean show_dialog;
    public String file;
    public String url;
    //广告开关 0:关 1:开
    @SerializedName("ad_status")
    public Integer ad_status = AD_STATUS_OFF;
    public long response_time = System.currentTimeMillis();
    public String kefu;
    @SerializedName("ad_switch")
    public AdSwitch ad_switch;

    @SerializedName("force_ad_switch")
    public Integer force_ad_switch = 0;

    public Integer ad_virtual = 0;

    @SerializedName("third_ad_switch")
    public Integer thirdAD = 0;

    public String version_audit;
    public String ad_virtual_version;

    public class AdSwitch {
        public Integer lottery = 0;
        public Integer reward = 0;
        public Integer minigame = 0;
        public Integer banner = 0;
        public Integer splash = 0;
        public Integer video = 0;
        public Integer draw = 0;
        public Integer top = 0;
        public Integer hotapp = 0;
    }

    @SerializedName("activity_dialog_time")
    public int actDialogInterval;

    @SerializedName("ad_activity")
    public List<AdActivity> adAct;

    public class AdActivity {
        public String keyname;
        @SerializedName("switch")
        public int isOpen;
        public String link;
        public String img;
        public String name;
        @SerializedName("link_type")
        public String type;
    }

//    "ad_weights": [
//    {
//        "keyname": "tt",
//            "weight": 50
//    },
//    {
//        "keyname": "qq",
//            "weight": 50
//    }
//  ]
    public List<AdWeights> ad_weights;

    public List<AdWeights> getAd_weights() {
        return ad_weights;
    }

    public void setAd_weights(List<AdWeights> ad_weights) {
        this.ad_weights = ad_weights;
    }

    public class AdWeights {
        public String keyname;
        public int weight;
    }

}
