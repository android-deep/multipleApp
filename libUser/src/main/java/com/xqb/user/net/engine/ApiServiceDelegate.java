package com.xqb.user.net.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.xqb.user.BuildConfig;
import com.xqb.user.R;
import com.xqb.user.bean.BaseResponse;
import com.xqb.user.bean.FaqModel;
import com.xqb.user.bean.OrderStatusResp;
import com.xqb.user.bean.UserInfo;
import com.xqb.user.bean.VersionBean;
import com.xqb.user.bean.VipProductInfo;
import com.xqb.user.net.converter.StringConverterFactory;
import com.xqb.user.net.lisenter.ApiCallback;
import com.xqb.user.net.service.ApiService;
import com.xqb.user.util.GsonUtil;
import com.xqb.user.util.ResponseCode;
import com.xqb.user.util.StatUtils;
import com.xqb.user.util.UmengStat;
import com.xqb.user.util.UserSharePref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ApiServiceDelegate {
    private static final String KEY_X_AUTH_TOKEN = "X-Auth-Token";
    private static final String KEY_APP_KEY = "app-key";
    private static final String KEY_APP_CHANNEL = "app-channel";
    private static final String KEY_APP_CHANNEL_DEV = "app-channel-dev";
    private static final String KEY_APP_VERSION = "version";
    private static final String KEY_APP_VERSION_CODE = "version-code";

    public static final String APP_KEY = "bnu5oyu4jvaqdtxv3ydmecbj";

    private Context mContext;
    private final List<String> mUrlList;
    private int mUrlIndex = 0;
    private int mTryTimes = 0;
    private Random random = new Random(System.currentTimeMillis());

    public ApiServiceDelegate(Context context) {
        mContext = context;
        mUrlList = new ArrayList<>();
//        String baseUrl = "http://192.168.0.46/";
//        String baseUrl = "https://mopen.77haibao.com/";
        String baseUrl = "https://app.fntmob.com/";
        mUrlList.add(baseUrl);
    }

    public ApiService getAPIService() {
        OkHttpClient.Builder builder = NetworkClient.getClientBuilder(false);
        //配置日志拦截器
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        return getRetrofitBuilder(mUrlList.get(mUrlIndex), builder.build())
                .build().create(ApiService.class);
    }

    /**
     * @param url    域名
     * @param client okhttp请求客户端
     * @return retrofit的构建器
     */
    public Retrofit.Builder getRetrofitBuilder(String url, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(new StringConverterFactory())
                .client(client);
    }

    private void moveToNextUrl() {
        if (++mUrlIndex < mUrlList.size()) {
            return;
        }
        mUrlIndex = 0;
    }

    public void register() {
        register("", "", null);
    }

    public void register(String mobile, String password, final ApiCallback callback) {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String versionName = String.valueOf(StatUtils.getVersionName(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        Map<String, String> header = getHeader(versionName, versionCode, nonce);
        JSONObject params = getRegisterParam(mContext, versionCode, nonce);
        try {
            params.put("mobile", mobile);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Call<String> request = getAPIService().register(header, params.toString());
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {

                String body = response.body();
                if (response.isSuccessful() && body != null) {
                    Type type = new TypeToken<BaseResponse<UserInfo>>() {
                    }.getType();
                    BaseResponse<UserInfo> userResponse = GsonUtil.gson2Bean(body, type);
                    if (userResponse == null) {
                        if (callback != null) {
                            callback.onFail("信息获取失败");
                        }
                        return;
                    }
                    if (userResponse.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                        UserInfo userInfo = userResponse.getData();
                        String userInfoStr = GsonUtil.getGsonString(userInfo);
                        String cacheInfo = UserSharePref.getInstance(mContext).getString(UserSharePref.KEY_USER_INFO);
                        if (!TextUtils.isEmpty(cacheInfo)) {
                            UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
                                    userInfoStr);
                            UserAgent.getInstance(mContext).setUserInfo(userInfo);
                        }
                        if (callback != null) {
                            callback.onSuccess();
                        }
                    } else {
                        if (callback != null) {
                            callback.onFail(userResponse.getMsg());
                        }
                    }

                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", "register");
                    if (response.errorBody() != null) {
                        params.put("error", response.errorBody());
                    } else {
                        params.put("error", "body null");
                    }

                    if (callback != null) {
                        callback.onFail("注册失败");
                        StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", "register");
                params.put("error", t.getMessage());
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                if (t instanceof UnknownHostException) {
                    moveToNextUrl();
                    mTryTimes++;
                    if (mTryTimes < mUrlList.size()) {
                        register();
                    }
                }
                if (callback != null) {
                    callback.onFail(mContext.getString(R.string.error_network));
                }
            }
        });
    }

    @SuppressLint("HardwareIds")
    private JSONObject getRegisterParam(Context context, String versionCode, String nonce) {
        JSONObject json = new JSONObject();
        String uuid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (TextUtils.isEmpty(uuid)) {
            uuid = UUID.randomUUID().toString();
        }
        try {
            if (BuildConfig.DEBUG) {
//                json.put("app_uuid", "test100001038");
                json.put("app_uuid", uuid);
            } else {
                json.put("app_uuid", uuid);
            }
            json.put("os_name", "Android");
            json.put("os_ver", Build.VERSION.RELEASE);
            json.put("os_lang", Locale.getDefault().toString());
            json.put("dev_manufacturer", Build.MANUFACTURER);
            json.put("dev_model", Build.MODEL);
            json.put("app_ver_name", StatUtils.getVersionName(context));
            json.put("app_ver_code", versionCode);
            json.put("app_channel", StatUtils.getChannel(context));
            json.put("app_channel_dev", StatUtils.getMetaData(context, "UMENG_CHANNEL_DEV"));
            json.put("nonce", nonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public Map<String, String> getHeader() {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        return getHeader(StatUtils.getVersionName(mContext), versionCode, nonce);
    }

    public Map<String, String> getHeader(String versionName, String versionCode, String nonce) {
        Map<String, String> header = new HashMap<>();
        String token = StatUtils.generateHeaderToken(mContext, versionCode, nonce);
        header.put(KEY_X_AUTH_TOKEN, TextUtils.isEmpty(token) ? "" : token);
        header.put(KEY_APP_KEY, APP_KEY);
        header.put(KEY_APP_CHANNEL, StatUtils.getChannel(mContext));
        header.put(KEY_APP_CHANNEL_DEV, StatUtils.getMetaData(mContext, "UMENG_CHANNEL_DEV"));
        header.put(KEY_APP_VERSION, versionName);
        header.put(KEY_APP_VERSION_CODE, versionCode);
        return header;
    }

    public void thirdLogin(final String name, final String gender, final String iconurl, final Integer loginType, final String uid, final ApiCallback callback) {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), versionCode, nonce);
        JSONObject params = getThirdLoginParams(name, gender, iconurl, loginType, uid, versionCode, nonce);
        final Call<String> request = getAPIService().thirdLogin(header, params.toString());
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (callback != null) {
                    String body = response.body();
                    if (response.isSuccessful() && body != null) {
                        Type type = new TypeToken<BaseResponse<UserInfo>>() {
                        }.getType();
                        BaseResponse<UserInfo> userResponse = GsonUtil.gson2Bean(body, type);
                        if (userResponse == null) {
                            callback.onFail("信息获取失败");
                            return;
                        }
                        if (userResponse.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                            callback.onSuccess();
                            UserInfo userInfo = userResponse.getData();
                            String userInfoStr = GsonUtil.getGsonString(userInfo);
                            UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
                                    userInfoStr);
                            UserAgent.getInstance(mContext).setUserInfo(userInfo);
                        } else {
                            callback.onFail(userResponse.getMsg());
                        }

                    } else {
                        Map<String, Object> params = new HashMap<>();
                        params.put("name", "login");
                        if (response.errorBody() != null) {
                            params.put("error", response.errorBody());
                        } else {
                            params.put("error", "body null");
                        }
                        callback.onFail("登录失败");
                        StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", "login");
                params.put("error", t.getMessage());
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                if (t instanceof UnknownHostException) {
                    moveToNextUrl();
                    mTryTimes++;
                    if (mTryTimes < mUrlList.size()) {
                        thirdLogin(name, gender, iconurl, loginType, uid, callback);
                    } else {
                        if (callback != null) {
                            callback.onFail(mContext.getString(R.string.error_network));
                        }
                    }
                }
            }
        });
    }

    public void login(final String mobile, final String password, final ApiCallback callback) {
        String verionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), verionCode, nonce);
        JSONObject params = getLoginParams(mobile, password, verionCode, nonce);
        final Call<String> request = getAPIService().login(header, params.toString());
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (callback != null) {
                    String body = response.body();
                    if (response.isSuccessful() && body != null) {
                        Type type = new TypeToken<BaseResponse<UserInfo>>() {
                        }.getType();
                        BaseResponse<UserInfo> userResponse = GsonUtil.gson2Bean(body, type);
                        if (userResponse == null) {
                            callback.onFail("信息获取失败");
                            return;
                        }
                        if (userResponse.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                            callback.onSuccess();
                            UserInfo userInfo = userResponse.getData();
                            String userInfoStr = GsonUtil.getGsonString(userInfo);
                            UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
                                    userInfoStr);
                            UserAgent.getInstance(mContext).setUserInfo(userInfo);
                        } else {
                            callback.onFail(userResponse.getMsg());
                        }

                    } else {
                        Map<String, Object> params = new HashMap<>();
                        params.put("name", "login");
                        if (response.errorBody() != null) {
                            params.put("error", response.errorBody());
                        } else {
                            params.put("error", "body null");
                        }
                        callback.onFail("登录失败");
                        StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", "login");
                params.put("error", t.getMessage());
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                if (t instanceof UnknownHostException) {
                    moveToNextUrl();
                    mTryTimes++;
                    if (mTryTimes < mUrlList.size()) {
                        login(mobile, password, null);
                    }
                }
                if (callback != null) {
                    callback.onFail(mContext.getString(R.string.error_network));
                }
            }
        });
    }

    @SuppressLint("HardwareIds")
    private JSONObject getLoginParams(String mobile, String password, String versionCode, String nonce) {
        JSONObject json = new JSONObject();
        try {
            String uuid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            json.put("app_uuid", uuid);
            json.put("mobile", mobile);
            json.put("password", password);
            json.put("app_ver_code", versionCode);
            json.put("nonce", nonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private JSONObject getThirdLoginParams(String name, String gender, String iconurl, Integer loginType, String uid, String versionCode, String nonce) {
        JSONObject json = new JSONObject();
        try {
            String uuid = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
            if (TextUtils.isEmpty(uuid)) {
                uuid = UUID.randomUUID().toString();
            }
            json.put("app_uuid", uuid);
            json.put("app_ver_code", versionCode);
            json.put("nonce", nonce);
            json.put("name", name);
            json.put("gender", gender);
            json.put("iconurl", iconurl);
            json.put("login_type", loginType);
            json.put("uid", uid);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }


    public void getUserInfo() {
        getUserInfo(false);
    }

    public void getUserInfo(final boolean retry) {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), versionCode, nonce);
        String params = getUserInfoParam(versionCode, nonce);
        if (TextUtils.isEmpty(params)) {
            return;
        }
        Call<String> request = getAPIService().getUserInfo(header, params);
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                String body = response.body();
                if (response.isSuccessful() && body != null) {
                    Type type = new TypeToken<BaseResponse<UserInfo>>() {
                    }.getType();
                    BaseResponse<UserInfo> userResponse = GsonUtil.gson2Bean(body, type);
                    if (userResponse == null) {
                        return;
                    }
                    if (userResponse.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                        UserInfo userInfo = userResponse.getData();
                        String userInfoStr = GsonUtil.getGsonString(userInfo);
                        UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
                                userInfoStr);
                        UserAgent.getInstance(mContext).setUserInfo(userInfo);
                    } else {
                        UserAgent.getInstance(mContext).clearUserInfo();
//                        Toast.makeText(mContext, userResponse.getMsg(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", "device");
                    if (response.errorBody() != null) {
                        params.put("error", response.errorBody());
                    } else {
                        params.put("error", "body null");
                    }
                    StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                }
                UserSharePref.getInstance(mContext).getBoolean(UserSharePref.KEY_PENDING_USER_INFO, false);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", "device");
                params.put("error", t.getMessage());
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                if (t instanceof UnknownHostException) {
                    moveToNextUrl();
                    mTryTimes++;
                    if (mTryTimes < mUrlList.size()) {
                        getUserInfo();
                    }
                }
                if (retry) {
                    UserSharePref.getInstance(mContext).getBoolean(UserSharePref.KEY_PENDING_USER_INFO, true);
                }

            }
        });
    }

    public Observable<UserInfo> getUserInfoForResult(final boolean retry) {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        final Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), versionCode, nonce);
        final String params = getUserInfoParam(versionCode, nonce);

        return Observable.create(new ObservableOnSubscribe<UserInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<UserInfo> e) {
                if (TextUtils.isEmpty(params)) {
                    return;
                }
                Call<String> request = getAPIService().getUserInfo(header, params);
                request.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        String body = response.body();
                        if (response.isSuccessful() && body != null) {
                            Type type = new TypeToken<BaseResponse<UserInfo>>() {
                            }.getType();
                            BaseResponse<UserInfo> userResponse = GsonUtil.gson2Bean(body, type);
                            if (userResponse == null) {
                                e.onError(new Exception("error"));
                                return;
                            }
                            if (userResponse.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                                UserInfo userInfo = userResponse.getData();
                                String userInfoStr = GsonUtil.getGsonString(userInfo);
                                UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
                                        userInfoStr);
                                UserAgent.getInstance(mContext).setUserInfo(userInfo);
                                e.onNext(userInfo);
                            } else {
                                e.onError(new Exception("error"));
                                UserAgent.getInstance(mContext).clearUserInfo();
//                                Toast.makeText(mContext, userResponse.getMsg(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Map<String, Object> params = new HashMap<>();
                            params.put("name", "device");
                            if (response.errorBody() != null) {
                                params.put("error", response.errorBody());
                            } else {
                                params.put("error", "body null");
                            }
                            StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                            e.onError(new Exception("error"));
                        }
                        UserSharePref.getInstance(mContext).getBoolean(UserSharePref.KEY_PENDING_USER_INFO, false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("name", "device");
                        params.put("error", t.getMessage());
                        StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                        if (t instanceof UnknownHostException) {
                            moveToNextUrl();
                            mTryTimes++;
                            if (mTryTimes < mUrlList.size()) {
                                getUserInfo();
                            }
                        }
                        if (retry) {
                            UserSharePref.getInstance(mContext).getBoolean(UserSharePref.KEY_PENDING_USER_INFO, true);
                        }
                        e.onError(t);

                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<OrderStatusResp> getOrderStatus() {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        final Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), versionCode, nonce);
        final String params = getUserInfoParam(versionCode, nonce);

        return Observable.create(new ObservableOnSubscribe<OrderStatusResp>() {
            @Override
            public void subscribe(final ObservableEmitter<OrderStatusResp> e) {
                if (TextUtils.isEmpty(params)) {
                    return;
                }
                Call<String> request = getAPIService().orderStatus(header, params);
                request.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        String body = response.body();
                        if (response.isSuccessful() && body != null) {
                            OrderStatusResp orderStatus = GsonUtil.gson2Bean(body, OrderStatusResp.class);
                            if (orderStatus == null) {
                                e.onError(new Exception("error"));
                                return;
                            }
                            if (orderStatus.getCode() == ResponseCode.CODE_SUCCESS) {
//                                UserInfo userInfo = orderStatus.getData();
//                                String userInfoStr = GsonUtil.getGsonString(userInfo);
//                                UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
//                                        userInfoStr);
//                                UserAgent.getInstance(mContext).setUserInfo(userInfo);
                                e.onNext(orderStatus);
                            } else {
                                e.onError(new Exception("error"));
//                                UserAgent.getInstance(mContext).clearUserInfo();
//                                Toast.makeText(mContext, orderStatus.getMsg(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Map<String, Object> params = new HashMap<>();
                            params.put("name", "device");
                            if (response.errorBody() != null) {
                                params.put("error", response.errorBody());
                            } else {
                                params.put("error", "body null");
                            }
                            StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                            e.onError(new Exception("error"));
                        }
                        UserSharePref.getInstance(mContext).getBoolean(UserSharePref.KEY_ORDER_STATUS, false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("name", "device");
                        params.put("error", t.getMessage());
                        StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                        e.onError(t);

                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private String getUserInfoParam(String versionCode, String nonce) {
        UserInfo userInfo = UserAgent.getInstance(mContext).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.userId)) {
            StatAgent.onEvent(mContext, UmengStat.USER_ID_NULL);
            return "";
        }
        JSONObject json = new JSONObject();
        try {
            json.put("token", userInfo.token);
            json.put("user_id", userInfo.userId);
            json.put("app_ver_code", versionCode);
            json.put("nonce", nonce);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public Observable<UserInfo> receiveVip() {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        final Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), versionCode, nonce);
        final String params = getUserInfoParam(versionCode, nonce);

        return Observable.create(new ObservableOnSubscribe<UserInfo>() {
            @Override
            public void subscribe(final ObservableEmitter<UserInfo> e) {
                if (TextUtils.isEmpty(params)) {
                    return;
                }
                Call<String> request = getAPIService().receiveVip(header, params);
                request.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        String body = response.body();
                        if (response.isSuccessful() && body != null) {
                            Type type = new TypeToken<BaseResponse<UserInfo>>() {
                            }.getType();
                            BaseResponse<UserInfo> userResponse = GsonUtil.gson2Bean(body, type);
                            if (userResponse == null) {
                                e.onError(new Exception("error"));
                                return;
                            }
                            if (userResponse.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                                UserInfo userInfo = userResponse.getData();
                                String userInfoStr = GsonUtil.getGsonString(userInfo);
                                UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
                                        userInfoStr);
                                UserAgent.getInstance(mContext).setUserInfo(userInfo);
                                e.onNext(userInfo);
                            } else {
                                e.onError(new Exception("error"));
                                UserAgent.getInstance(mContext).clearUserInfo();
                                Toast.makeText(mContext, userResponse.getMsg(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Map<String, Object> params = new HashMap<>();
                            params.put("name", "device");
                            if (response.errorBody() != null) {
                                params.put("error", response.errorBody());
                            } else {
                                params.put("error", "body null");
                            }
                            StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                            e.onError(new Exception("error"));
                        }
                        UserSharePref.getInstance(mContext).getBoolean(UserSharePref.KEY_PENDING_USER_INFO, false);
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("name", "device");
                        params.put("error", t.getMessage());
                        StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                        e.onError(t);
                    }
                });
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public void getProduct() {
        Map<String, String> header = getHeader();
        Call<String> request = getAPIService().getProduct(header);
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    String body = response.body();
                    Type type = new TypeToken<BaseResponse<VipProductInfo>>() {
                    }.getType();
                    BaseResponse<VipProductInfo> productResp = GsonUtil.gson2Bean(body, type);
                    if (productResp == null) {
                        return;
                    }
                    if (productResp.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                        UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_PRODUCT_INFO,
                                GsonUtil.getGsonString(productResp.getData()));
                        UserAgent.getInstance(mContext).setProductInfo(productResp.getData());
                    } else {
                        Toast.makeText(mContext, productResp.getMsg(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, "name", "product");
                if (t instanceof UnknownHostException) {
                    moveToNextUrl();
                    mTryTimes++;
                    if (mTryTimes < mUrlList.size()) {
                        getProduct();
                    }
                }
                Toast.makeText(mContext, mContext.getString(R.string.error_network), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void getProduct(Callback<String> callback) {
        Call<String> request = getAPIService().getProduct(getHeader());
        request.enqueue(callback);
    }

    public String buy(String type, String mhtOrderName, String mhtOrderDetail, String payType, int outputType) {
        String verionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), verionCode, nonce);
        String params = getBuyParam(type, mhtOrderName, mhtOrderDetail, payType, outputType, verionCode, nonce);
        if (TextUtils.isEmpty(params)) {
            return "";
        }
        Call<String> request = getAPIService().buy(header, params);
        try {
            Response<String> response = request.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, "name", "buy");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getBuyParam(String type, String mhtOrderName, String mhtOrderDetail, String payType, int outputType, String versionCode, String nonce) {
        UserInfo userInfo = UserAgent.getInstance(mContext).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.userId)) {
            StatAgent.onEvent(mContext, UmengStat.USER_ID_NULL);
            return "";
        }
        JSONObject json = new JSONObject();
        try {
            json.put("token", userInfo.token);
            json.put("user_id", userInfo.userId);
            json.put("type", type);
            json.put("mhtOrderName", mhtOrderName);
            json.put("mhtOrderDetail", mhtOrderDetail);
            json.put("payType", payType);
            json.put("outputType", outputType);
            json.put("app_ver_code", versionCode);
            json.put("nonce", nonce);
            json.put("pname", mContext.getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public void adSwitch(final ApiCallback callback) {
        Call<String> request = getAPIService().adSwitch(getHeader());
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Type type = new TypeToken<BaseResponse<VersionBean>>() {
                    }.getType();
                    BaseResponse<VersionBean> versionResp = GsonUtil.gson2Bean(response.body(), type);
                    if (versionResp == null) {
                        callback.onFail("数据解析失败");
                        return;
                    }
                    if (versionResp.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                        UserAgent.getInstance(mContext).setVersionBean(versionResp.getData());
                        UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_VERION_INFO,
                                GsonUtil.getGsonString(versionResp.getData()));
                        callback.onSuccess();
                    } else {
                        callback.onFail(versionResp.getCode() + "-" + versionResp.getMsg());
                    }
                } else {
                    callback.onFail(mContext.getString(R.string.error_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, "name", "version");
                moveToNextUrl();
                mTryTimes++;
                if (mTryTimes < mUrlList.size()) {
                    checkUpdate(callback);
                } else {
//                    String errorMsg = t.getMessage().isEmpty() ? t.getLocalizedMessage() : t.getMessage();
                    callback.onFail(mContext.getString(R.string.error_network));
                }
            }
        });
    }

    public void changePwd(String oldPwd, String newPwd) {
        String verionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), verionCode, nonce);

        JSONObject params = new JSONObject();
        try {
            params.put("old_password", oldPwd);
            params.put("password", newPwd);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        Call<String> request = getAPIService().buy(header, params.toString());
        try {
            Response<String> response = request.execute();
            if (response.isSuccessful()) {
                return;
            }
            StatAgent.onEvent(mContext, UmengStat.API_FAIL, "name", "buy");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkUpdate(final ApiCallback callback) {
        Call<String> request = getAPIService().checkUpdate(getHeader());
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful()) {
                    Type type = new TypeToken<BaseResponse<VersionBean>>() {
                    }.getType();
                    BaseResponse<VersionBean> versionResp = GsonUtil.gson2Bean(response.body(), type);
                    if (versionResp == null) {
                        callback.onFail("数据解析失败");
                        return;
                    }
                    if (versionResp.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                        UserAgent.getInstance(mContext).setVersionBean(versionResp.getData());
                        UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_VERION_INFO,
                                GsonUtil.getGsonString(versionResp.getData()));
                        callback.onSuccess();
                    } else {
                        callback.onFail(versionResp.getCode() + "-" + versionResp.getMsg());
                    }
                } else {
                    callback.onFail(mContext.getString(R.string.error_network));
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, "name", "version");
                moveToNextUrl();
                mTryTimes++;
                if (mTryTimes < mUrlList.size()) {
                    checkUpdate(callback);
                } else {
//                    String errorMsg = t.getMessage().isEmpty() ? t.getLocalizedMessage() : t.getMessage();
                    callback.onFail(mContext.getString(R.string.error_network));
                }
            }
        });
    }

    public void loadFAQ(final Callback<String> callback) {
        final Call<String> request = getAPIService().faq();
        request.enqueue(callback);
    }

    public void activation(String activationCode, final ApiCallback callback) {
        String versionCode = String.valueOf(StatUtils.getVersionCode(mContext));
        String nonce = String.valueOf(random.nextInt(10000000));
        Map<String, String> header = getHeader(StatUtils.getVersionName(mContext), versionCode, nonce);
        String params = getActivationParam(activationCode, versionCode, nonce);

        final Call<String> request = getAPIService().activation(header, params);
        request.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,
                            response.body());
                    UserInfo userInfo = GsonUtil.gson2Bean(response.body(), UserInfo.class);
                    UserAgent.getInstance(mContext).setUserInfo(userInfo);
                } else {
                    Map<String, Object> params = new HashMap<>();
                    params.put("name", "activation");
                    if (response.errorBody() != null) {
                        params.put("error", response.errorBody());
                    } else {
                        params.put("error", "body null");
                    }
                    StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);

                    try {
                        String errorMsg = response.errorBody().toString();
                        JSONObject object = new JSONObject(errorMsg);
                        String message = object.optString("message");
                        if (!TextUtils.isEmpty(message)) {
//                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (callback != null) {
                    if (response.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        callback.onFail("激活失败");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Map<String, Object> params = new HashMap<>();
                params.put("name", "activation");
                params.put("error", t.getMessage());
                StatAgent.onEvent(mContext, UmengStat.API_FAIL, params);
                if (t instanceof UnknownHostException) {
                    moveToNextUrl();
                    mTryTimes++;
                    if (mTryTimes < mUrlList.size()) {
                        register();
                    }
                }
                if (callback != null) {
                    callback.onFail(mContext.getString(R.string.error_network));
                }
            }
        });
    }

    private String getActivationParam(String activationCode, String versionCode, String nonce) {
        UserInfo userInfo = UserAgent.getInstance(mContext).getUserInfo();
        if (userInfo == null || TextUtils.isEmpty(userInfo.userId)) {
            StatAgent.onEvent(mContext, UmengStat.USER_ID_NULL);
            return "";
        }
        JSONObject json = new JSONObject();
        try {
            json.put("token", userInfo.token);
            json.put("user_id", userInfo.userId);
            json.put("app_ver_code", versionCode);
            json.put("nonce", nonce);
            json.put("code", activationCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }

    public void unRegister(final ApiCallback callback) {
        getAPIService().unregister(getHeader(),UserAgent.getInstance(mContext).getUserInfo().app_uuid)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful()) {
                            UserAgent.getInstance(mContext).setUserInfo(null);
                            UserSharePref.getInstance(mContext).putString(UserSharePref.KEY_USER_INFO,"");
                            callback.onSuccess();
                        } else {
                            callback.onFail("注销失败");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        callback.onFail("注销失败");
                    }
                });

    }

}
