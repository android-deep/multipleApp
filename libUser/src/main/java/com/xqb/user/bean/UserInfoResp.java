package com.xqb.user.bean;

public class UserInfoResp {

    /**
     * code : 1
     * msg : 重新登录成功
     * data : {"user_id":"13eecdd52bd94265a4cfb8e062ff2f58","mobile":"11@163.com","is_vip":true,"vip":"vip","vip_format":"付费用户","vip_expire_time":1592656813000,"token":"DIxAWCAUH8NENOjM8pTQ9rVp3uc2SgkmbH55czhywuGcdHEULfDHEIvbe7yO8w7J","register_time":1592484013000,"response_time":1592550772000,"groupid":""}
     */

    private int code;
    private String msg;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * user_id : 13eecdd52bd94265a4cfb8e062ff2f58
         * mobile : 11@163.com
         * is_vip : true
         * vip : vip
         * vip_format : 付费用户
         * vip_expire_time : 1592656813000
         * token : DIxAWCAUH8NENOjM8pTQ9rVp3uc2SgkmbH55czhywuGcdHEULfDHEIvbe7yO8w7J
         * register_time : 1592484013000
         * response_time : 1592550772000
         * groupid :
         */

        private String user_id;
        private String mobile;
        private boolean is_vip;
        private String vip;
        private String vip_format;
        private long vip_expire_time;
        private String token;
        private long register_time;
        private long response_time;
        private String groupid;

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public boolean isIs_vip() {
            return is_vip;
        }

        public void setIs_vip(boolean is_vip) {
            this.is_vip = is_vip;
        }

        public String getVip() {
            return vip;
        }

        public void setVip(String vip) {
            this.vip = vip;
        }

        public String getVip_format() {
            return vip_format;
        }

        public void setVip_format(String vip_format) {
            this.vip_format = vip_format;
        }

        public long getVip_expire_time() {
            return vip_expire_time;
        }

        public void setVip_expire_time(long vip_expire_time) {
            this.vip_expire_time = vip_expire_time;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public long getRegister_time() {
            return register_time;
        }

        public void setRegister_time(long register_time) {
            this.register_time = register_time;
        }

        public long getResponse_time() {
            return response_time;
        }

        public void setResponse_time(long response_time) {
            this.response_time = response_time;
        }

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }
    }
}
