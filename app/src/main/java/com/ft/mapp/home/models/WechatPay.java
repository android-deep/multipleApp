package com.ft.mapp.home.models;

import com.google.gson.annotations.SerializedName;

public class WechatPay {
//    {"code":1,"msg":"\u4e0b\u5355\u6210\u529f","data":{"appid":"wx8870a64dc1cdabe7","partnerid":"1606361882","prepayid":"wx272137424722996f891a91c06e25a30000","package":"Sign=WXPay"
//            ,"noncestr":"9elv91rjl27smeid8alhppc30mquo2ft"
//            ,"timestamp":1622122662,"sign":"DEE26380F6A6ABB719D6033317DC0AA2"}}
    private int code;
    private String msg;
    private Data data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data{
        private String appid;
        private String partnerid;
        private String prepayid;
        @SerializedName("package")
        private String packageName;
        private String noncestr;
        private String timestamp;
        private String sign;

        public String getAppid() {
            return appid;
        }

        public void setAppid(String appid) {
            this.appid = appid;
        }

        public String getPartnerid() {
            return partnerid;
        }

        public void setPartnerid(String partnerid) {
            this.partnerid = partnerid;
        }

        public String getPrepayid() {
            return prepayid;
        }

        public void setPrepayid(String prepayid) {
            this.prepayid = prepayid;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }

        public String getNoncestr() {
            return noncestr;
        }

        public void setNoncestr(String noncestr) {
            this.noncestr = noncestr;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }
}
