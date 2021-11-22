package com.ft.mapp.home.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ft.mapp.home.adapters.VipAdapter;
import com.ft.mapp.home.models.WechatPay;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.RequestUtils;
import com.ft.mapp.utils.UrlEncodeUtils;
import com.ft.mapp.widgets.CommonDialog;
import com.ft.mapp.widgets.ObserveHorizontalScrollView;
import com.fun.vbox.client.core.VCore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipaynow.plugin.api.IpaynowPlugin;
import com.ipaynow.plugin.manager.route.dto.ResponseParams;
import com.ipaynow.plugin.manager.route.impl.ReceivePayResult;
import com.ipaynow.plugin.view.IpaynowLoading;
import com.jaeger.library.StatusBarUtil;
import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.home.BuyTipActivity;
import com.ft.mapp.utils.ToastUtil;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.xqb.user.bean.BaseResponse;
import com.xqb.user.bean.OrderStatusResp;
import com.xqb.user.bean.UserInfo;
import com.xqb.user.bean.VipProductInfo;
import com.xqb.user.net.engine.ApiServiceDelegate;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.util.GsonUtil;
import com.xqb.user.util.ResponseCode;
import com.xqb.user.util.UmengStat;
import com.yalantis.ucrop.util.ScreenUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONObject;

import io.reactivex.observers.ResourceObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VipActivity extends VActivity implements View.OnClickListener, ReceivePayResult, IWXAPIEventHandler {

    private TextView mPayPriceTv;
    private VipProductInfo mProductInfo;

    private final String PRODUCT_TYPE_ALL = "forever";
    private final String PRODUCT_TYPE_YEAR = "year";
    private final String PRODUCT_TYPE_MONTH = "month";
    private final String PRODUCT_TYPE_DAY = "day";

    private String mProductType = PRODUCT_TYPE_ALL;
    private String mProductTitle = PRODUCT_TYPE_ALL;

    private final String PAY_CHANNEL_ALIPAY = "zhifubao";
    private final String PAY_CHANNEL_WXPAY = "weixin";

    //zhifubao：支付宝，weixin：微信
    private String payChannelType = PAY_CHANNEL_ALIPAY;

    private ObserveHorizontalScrollView svFun;
    private LinearLayout layoutFun;

    private TextView mIsVipTv;
    private TextView mVipDesTv;
    private TextView tvPay;
    private RadioButton rbWx;
    private RadioButton rbAli;
    private ListView lvVip;
    private String[] priceArray = new String[4];

    private IpaynowPlugin mIpaynowplugin;
    private IpaynowLoading mLoadingDialog;
    private WebView webView;

    private FrameLayout layoutTrack;
    private View thumb;

    //正在支付中
    private boolean isPaying = false;
    private VipAdapter adapter;
    private IWXAPI api;


    public static void go(Context context) {
        Intent vipIntent = new Intent(context, VipActivity.class);
        if (context instanceof Activity) {
            context.startActivity(vipIntent);
        } else {
            vipIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(vipIntent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.activity_vip_purchase);
        initView();
        initPayPlugin();
        initData();
//        setWebView();
    }

    private void initView() {

        svFun = findViewById(R.id.vip_sv_function);
        layoutFun = findViewById(R.id.vip_layout_function);

        tvPay = findViewById(R.id.vip_goto_pay);
//        tvPay = findViewById(R.id.vip_tv_goto_pay);
        rbWx = findViewById(R.id.vip_rb_pay_wx);
        rbAli = findViewById(R.id.vip_rb_pay_ali);

        lvVip = findViewById(R.id.vip_level_lv);

        tvPay.setOnClickListener(this);
        mPayPriceTv = findViewById(R.id.vip_pay_price);

        webView = findViewById(R.id.vip_webview);
        thumb = findViewById(R.id.vip_purchase_view_thumb);
        layoutTrack = findViewById(R.id.vip_purchase_layout_track);

        mIsVipTv = findViewById(R.id.is_vip_tv);
        mVipDesTv = findViewById(R.id.vip_des_tv);

        LinearLayout layoutMockSteps = findViewById(R.id.vip_layout_function_mock_step);
        if (!UserAgent.getInstance(this).isVirtualLocationOn()) {
            layoutMockSteps.setVisibility(View.GONE);
        }

        findViewById(R.id.buy_tip_tv).setOnClickListener(this);
        findViewById(R.id.vip_tv_goto_pay).setOnClickListener(this);
        findViewById(R.id.vip_layout_pay_wx).setOnClickListener(this);
        findViewById(R.id.vip_layout_pay_ali).setOnClickListener(this);
        StatAgent.onEvent(this, UmengStat.PAY_ENTER);

        setTrack();
    }

    private void setTrack() {
        layoutFun.post(() -> {
            int screenWidth = ScreenUtils.getScreenWidth(VipActivity.this);
            float trackWidth = screenWidth * 0.1f;
            float thumbWidth = screenWidth * 0.05f;
            ViewGroup.LayoutParams layoutParams = thumb.getLayoutParams();
            layoutParams.width = (int) thumbWidth;
            thumb.requestLayout();
            int max = layoutFun.getMeasuredWidth();
            int overWidth = max - screenWidth;
            svFun.setListener((l, t) -> {
                float targetX = l / (float) overWidth * (trackWidth - thumbWidth);
                thumb.setX(targetX);
            });
        });

    }

    private void initPayPlugin() {
        mIpaynowplugin = IpaynowPlugin.getInstance().init(this);
        mIpaynowplugin.unCkeckEnvironment();

        mLoadingDialog = mIpaynowplugin.getDefaultLoading();

        api = WXAPIFactory.createWXAPI(this, "wx3a6e087ddb52dede");
        api.handleIntent(getIntent(), this);
    }

    private void initData() {
        getProduct();
//        mProductInfo = UserAgent.getInstance(this).getProductInfo();
//        if (mProductInfo == null || mProductInfo.list == null) {
//            new ApiServiceDelegate(this).getProduct();
//            ToastUtil.show(this,"vip 列表获取失败");
//            finish();
//        } else {
//            try {
//                for (VipProductInfo.Product product : mProductInfo.list) {
//                    if (PRODUCT_TYPE_ALL.equalsIgnoreCase(product.type)) {
//                        priceArray[0] = product.price;
//                    } else if (PRODUCT_TYPE_YEAR.equalsIgnoreCase(product.type)) {
//                        priceArray[1] = product.price;
//                    } else if (PRODUCT_TYPE_MONTH.equalsIgnoreCase(product.type)) {
//                        priceArray[2] = product.price;
//                    } else if (PRODUCT_TYPE_DAY.equalsIgnoreCase(product.type)) {
//                        priceArray[3] = product.price;
//                    }
//                }
//
//                mPayPriceTv.setText(getString(R.string.price_format, priceArray[0]));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        if (UserAgent.getInstance(this).isVipUser()) {
            mIsVipTv.setText(R.string.vip_renew_desc);
            mVipDesTv.setVisibility(View.GONE);
        }

    }

    private List<VipProductInfo.Product> vipProducts;

    private void getProduct() {
        showLoading("加载中");
        new ApiServiceDelegate(this).getProduct(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                mLoadingDialog.dismiss();
                if (response.isSuccessful()) {
                    String body = response.body();
                    Type type = new TypeToken<BaseResponse<VipProductInfo>>() {
                    }.getType();
                    BaseResponse<VipProductInfo> productResp = GsonUtil.gson2Bean(body, type);
                    if (productResp == null) {
                        return;
                    }
                    if (productResp.getCode().equals(ResponseCode.CODE_SUCCESS)) {
                        vipProducts = productResp.getData().list;
                        setVipProducts();
                    } else {
                        Toast.makeText(getContext(), productResp.getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                mLoadingDialog.dismiss();
                StatAgent.onEvent(VipActivity.this, UmengStat.API_FAIL, "name", "product");
                Toast.makeText(VipActivity.this, getString(com.xqb.user.R.string.error_network), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setVipProducts() {
        mLoadingDialog.dismiss();
        if (vipProducts == null || vipProducts.size() == 0) {
            ToastUtil.show(this, "商品列表异常");
            finish();
            return;
        }
        vipProducts.get(0).isChosen = true;
        mProductType = vipProducts.get(0).type;
        mProductTitle = vipProducts.get(0).title;
        adapter = new VipAdapter(vipProducts);
        lvVip.setAdapter(adapter);
        lvVip.setOnItemClickListener((adapterView, view, i, l) -> {
            adapter.choose(i);
            mProductTitle = vipProducts.get(i).title;
            mProductType = vipProducts.get(i).type;
        });
    }

    private void setWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());
//        webView.loadUrl("https://wxpay.wxutil.com/mch/pay/h5.v2.php");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.vip_all_layout:
//                clearSelected();
//                clearMark();
//                mAllLayout.setBackgroundResource(R.drawable.bg_vip_level_sel);
//                if (priceArray != null && priceArray.length > 0) {
//                    mPayPriceTv.setText(getString(R.string.price_format, priceArray[0]));
//                }
//                mProductType = PRODUCT_TYPE_ALL;
//                mProductTitle = mProductInfo.list.get(0).title;
//                break;
            case R.id.vip_goto_pay:
            case R.id.vip_tv_goto_pay:
                toPay();
                StatAgent.onEvent(this, UmengStat.PAY_CLICK);
                break;
            case R.id.buy_tip_tv:
                startActivity(new Intent(VipActivity.this, BuyTipActivity.class));
                break;
            case R.id.vip_layout_pay_wx:
                payChannelType = PAY_CHANNEL_WXPAY;
                changePayChannel();
                break;
            case R.id.vip_layout_pay_ali:
                payChannelType = PAY_CHANNEL_ALIPAY;
                changePayChannel();
                break;
        }
    }

    private void changePayChannel() {
        if (payChannelType.equals(PAY_CHANNEL_WXPAY)) {
            rbWx.setChecked(true);
            rbAli.setChecked(false);
            tvPay.setBackgroundColor(getResources().getColor(R.color.wxpay_green));
        } else {
            rbWx.setChecked(false);
            rbAli.setChecked(true);
            tvPay.setBackgroundColor(getResources().getColor(R.color.dot_blue));
        }
    }

    private void toPay() {
        if (payChannelType.equals("zhifubao")) {
            if (!VCore.get().isOutsideInstalled("com.eg.android.AlipayGphone")) {
                ToastUtil.show(this, "请先安装支付宝");
                return;
            }
        } else {
            if (!VCore.get().isOutsideInstalled("com.tencent.mm")) {
                ToastUtil.show(this, "请先安装微信");
                return;
            }
        }
        if (TextUtils.isEmpty(mProductType)) {
            ToastUtil.show(this, "请稍候");
            return;
        }
        UserInfo userInfo = UserAgent.getInstance(this).getUserInfo();
        if (userInfo == null) {
            ToastUtil.show(this, "请先登录");
            LoginActivity.start(this);
            return;
        }

        isPaying = true;
        showLoading("正在生成订单");
//        webView.setVisibility(View.VISIBLE);
//        String url = "http://double.yuekenet.com/api/buy";
//        UserInfo userInfo = UserAgent.getInstance(this).getUserInfo();
//        String verionCode = String.valueOf(StatUtils.getVersionCode(this));
//        String nonce = String.valueOf(new Random().nextInt(10000000));
//        params = "token=" + userInfo.token + "&user_id=" + userInfo.userId + "&type=" + mProductType
//                + "&mhtOrderName=" + mProductTitle + "&mhtOrderDetail=" + mProductTitle + "&payType=" + payChannelType
//                + "&outputType=6" + "&app_ver_code=" + verionCode + "&nonce=" + nonce;
//        webView.postUrl(url, EncodingUtils.getBytes(params, "BASE64"));

        GetMessage gm = new GetMessage();
        gm.execute();

    }


    @Override
    public void onIpaynowTransResult(ResponseParams responseParams) {
        String respCode = responseParams.respCode;
        String errorCode = responseParams.errorCode;
        String errorMsg = responseParams.respMsg;
        StringBuilder temp = new StringBuilder();
        if (respCode.equals("00")) {
            temp.append("交易状态:成功");
            new CommonDialog(this)
                    .setTitleId(R.string.notice)
                    .setMessage(R.string.vip_user_tip)
                    .setPositiveButton(R.string.OK, (dialogInterface, i) -> {
                        //
                    }).show();

            updateVipStatus();
            StatAgent.onEvent(this, UmengStat.PAY_SUCCESS, "type", mProductType);
            new ApiServiceDelegate(this).getUserInfo(true);
            return;
        }
        switch (respCode) {
            case "02":
                temp.append("交易状态:取消");
                break;
            case "01":
                temp.append("交易状态:失败").append("\n").append("错误码:").append(errorCode).append("原因:").append(errorMsg);
                break;
            case "03":
                temp.append("交易状态:未知").append("\n").append("原因:").append(errorMsg);
                break;
            default:
                temp.append("respCode=").append(respCode).append("\n").append("respMsg=").append(errorMsg);
                break;
        }
        Toast.makeText(this, "onIpaynowTransResult:" + temp.toString(), Toast.LENGTH_LONG).show();
//        ToastUtil.show(this, this.getString(R.string.pay_fail));
        StatAgent.onEvent(this, UmengStat.PAY_FAIL, "reason", errorMsg);

    }

    public class GetMessage extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            int outputType;
            if (payChannelType.equals("zhifubao")) {
                //支付宝
                outputType = 1;
                return new ApiServiceDelegate(getContext()).buy(mProductType, mProductTitle, mProductTitle, payChannelType, outputType);
            } else {
                //微信
                outputType = 2;
                String buyResponse = new ApiServiceDelegate(getContext()).buy(mProductType, mProductTitle, mProductTitle, payChannelType, outputType);
                return buyResponse;
            }

        }

        protected void onPostExecute(String requestMessage) {
            if (TextUtils.isEmpty(requestMessage)) {
                mLoadingDialog.dismiss();
                ToastUtil.show(getContext(), "购买失败");
                return;
            }

            String s = UrlEncodeUtils.urlDecode(requestMessage);
//            Log.d("--------------", "request msg = " + s);

            mLoadingDialog.dismiss();
            if (payChannelType.equals("zhifubao")) {
                WebView webView = new WebView(getContext());
                setWebView(webView);
                webView.loadUrl(requestMessage);
            } else {
//                Log.i("--mango--", "wechatPay resp = " + requestMessage);
                WechatPay wechatPay = new Gson().fromJson(requestMessage, WechatPay.class);
                if (null != wechatPay && wechatPay.getCode() == 1) {
                    PayReq req = new PayReq();
                    WechatPay.Data data = wechatPay.getData();
                    //req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
                    req.appId = data.getAppid();
                    req.partnerId = data.getPartnerid();
                    req.prepayId = data.getPrepayid();
                    req.nonceStr = data.getNoncestr();
                    req.timeStamp = data.getTimestamp();
                    req.packageValue = data.getPackageName();
                    req.sign = data.getSign();
                    req.extData = "app data"; // optional
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                } else {
                    if (wechatPay != null) {
                        ToastUtil.show(getContext(), wechatPay.getMsg());
                    } else {
                        ToastUtil.show(getContext(), "购买失败");
                    }
                }
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(s));
//                startActivity(intent);
            }

//            webView.setVisibility(View.VISIBLE);
//            webView.loadUrl(requestMessage);
//            webView.loadData(requestMessage, "text/html", "UTF-8") ;

        }
    }

    private void showLoading(String msg) {
        mLoadingDialog.setLoadingMsg(msg);
        mLoadingDialog.show();
    }

    private void updateVipStatus() {
        mIsVipTv.setText(getString(R.string.vip_user));
        mVipDesTv.setVisibility(View.GONE);
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            checkPayInfo(url);
            // 如下方案可在非微信内部WebView的H5页面中调出微信支付
            if (url.startsWith("weixin://") || url.startsWith("alipay://") || url.startsWith("alipays://")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.getVisibility() == View.VISIBLE) {
            webView.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (mLoadingDialog != null) {
//            if (mLoadingDialog.isShowing()) {
//                mLoadingDialog.dismiss();
//            }
//        }
        if (isPaying) {
            showLoading("请稍候");
            new ApiServiceDelegate(VipActivity.this).getOrderStatus()
                    .subscribe(new ResourceObserver<OrderStatusResp>() {
                        @Override
                        public void onNext(OrderStatusResp orderStatusResp) {
                            if (orderStatusResp.getCode() == 1) {
                                refreshUserInfo();
                            } else {
                                mLoadingDialog.dismiss();
                                isPaying = false;
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mLoadingDialog.dismiss();
                            isPaying = false;
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }

    private void refreshUserInfo() {
        new ApiServiceDelegate(VipActivity.this).getUserInfoForResult(true)
                .subscribe(new ResourceObserver<UserInfo>() {
                    @Override
                    public void onNext(UserInfo userInfo) {
                        ToastUtil.show(VipActivity.this, "购买完成");
                        isPaying = false;
                        mLoadingDialog.dismiss();
                        finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        isPaying = false;
                        finish();
                        mLoadingDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        isPaying = false;
                    }
                });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d("--mango--", "onPayFinish, errCode = " + resp.errCode);

        mLoadingDialog.dismiss();
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

        }
    }
}
