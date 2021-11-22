package com.xqb.user.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import androidx.annotation.Keep;

@Keep
public class VipProductInfo extends BaseBean{

    @SerializedName("response_time")
    public long responseTime = 0;

    public long interval = 86400; //单位秒

    public List<Product> list;

    @Keep
    public static class Product extends BaseBean{
        public String type;
        public String title;
        public String price;
        @SerializedName("old_price")
        public String prePrice;
        public String desc;
        public String activity;

        public boolean isChosen;
    }

}
