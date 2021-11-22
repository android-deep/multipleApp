package com.xqb.user.bean;

import com.google.gson.annotations.SerializedName;

import androidx.annotation.Keep;

import java.util.List;


@Keep
public class UserInfo extends BaseBean{

    @SerializedName("user_id")
    public String userId = "";

    public String gender;
    public String name;

    public String mobile;

    @SerializedName("is_vip")
    public boolean isVip = false;

    public List<String> message;

    //vip类型
    public String vip = "normal";

    //用户当前状态
    @SerializedName("vip_format")
    public String vipFormat = "普通用户";

    @SerializedName("vip_expire_time")
    public long expireTime;

    @SerializedName("has_buy")
    public int hasBuy = 0;

    public String token;

    @SerializedName("register_time")
    public long registerTime;

    @SerializedName("response_time")
    public long responseTime = 0;

    @SerializedName("member_ad_status")
    public Integer vipReceive = 0;

    public String groupid = "";
    public String code = "";

    public String app_uuid = "";
}
