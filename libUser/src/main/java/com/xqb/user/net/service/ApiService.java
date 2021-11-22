package com.xqb.user.net.service;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    //注册
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/register")
    Call<String> register(@HeaderMap Map<String, String> headers, @Body String body);

    //账密登录
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/login")
    Call<String> login(@HeaderMap Map<String, String> headers, @Body String body);

    //三方登录
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/third")
    Call<String> thirdLogin(@HeaderMap Map<String, String> headers, @Body String body);

    //注销
    @POST("api/cancel")
    @FormUrlEncoded
    Call<String> unregister(@HeaderMap Map<String,String> headers, @Field("app_uuid") String uuid);

    //获取用户信息
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/device")
    Call<String> getUserInfo(@HeaderMap Map<String, String> headers, @Body String body);

    //商品列表
    @GET("api/product")
    Call<String> getProduct(@HeaderMap Map<String,String> headers);

    //下单
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/buy")
    Call<String> buy(@HeaderMap Map<String, String> headers, @Body String body);

    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/password/change")
    Call<String> changePwd(@HeaderMap Map<String, String> headers, @Body String body);

    //检查更新
    @GET("api/version")
    Call<String> checkUpdate(@HeaderMap Map<String,String> headers);

    //常见问题
    @GET("api/guide")
    Call<String> faq();

    //查询订单状态
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/orderstatus")
    Call<String> orderStatus(@HeaderMap Map<String,String> headers, @Body String body);

    //激活
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/activation")
    Call<String> activation(@HeaderMap Map<String, String> headers, @Body String body);

    //领取免费会员
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @POST("api/memberad")
    Call<String> receiveVip(@HeaderMap Map<String, String> headers, @Body String body);

    //广告开关
    @Headers({
            "Cache-Control: no-cache",
            "Content-Type: application/json; charset=utf-8",
            "Accept: application/json; charset=utf-8"
    })
    @GET("api/adswitch")
    Call<String> adSwitch(@HeaderMap Map<String, String> headers);
}
