package com.ft.mapp.widgets;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ft.mapp.R;
import com.ft.mapp.home.adapters.FakeIconAdapter;
import com.ft.mapp.home.models.AppData;
import com.ft.mapp.home.models.AppInfo;
import com.ft.mapp.home.models.FakeIconModel;
import com.ft.mapp.utils.CommonUtil;

import java.util.ArrayList;

public class PopupFakeApp extends PopupWindow implements FakeIconAdapter.OnFakeIconChosenListener {

    private Context mContext;

    private RecyclerView rvIcon;
    private ImageView ivCur;
    private EditText etFakeTitle;
    private TextView tvCancel;
    private TextView tvSubmit;
    private View layoutCustom;

    private ArrayList<FakeIconModel> icons = new ArrayList<>();

    private FakeIconAdapter fakeIconAdapter;

    private AppData appData;
    private String fakeIconStr;

    public PopupFakeApp(Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View mPopView = inflater.inflate(R.layout.popup_fake, null);
        this.setContentView(mPopView);
        bindViews(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.setAnimationStyle(R.style.AnimBottom);
        ColorDrawable dw = new ColorDrawable(0x20000000);
        this.setBackgroundDrawable(dw);

        generateIcon();
    }

    private void bindViews(View view) {
        rvIcon = view.findViewById(R.id.fake_rv_icon);
        ivCur = view.findViewById(R.id.fake_iv_cur);
        etFakeTitle = view.findViewById(R.id.fake_et_fake_title);
        layoutCustom = view.findViewById(R.id.fake_layout_custom);
        tvCancel = view.findViewById(R.id.fake_tv_cancel);
        tvSubmit = view.findViewById(R.id.fake_tv_submit);

        tvSubmit.setOnClickListener(v -> {
            fakeAppOperateListener.onSubmit(etFakeTitle.getText().toString().trim(), fakeIconStr, Integer.parseInt(appData.getAppId() + "" + appData.getUserId()));
            dismiss();
        });
        view.findViewById(R.id.fake_iv_clear).setOnClickListener(v->etFakeTitle.setText(""));
        tvCancel.setOnClickListener(v -> dismiss());
        layoutCustom.setOnClickListener(v -> fakeAppOperateListener.choosePic());
    }

    private void generateIcon() {
        int[] iconArray = CommonUtil.getFakeIcons();
        for (int res : iconArray) {
            icons.add(new FakeIconModel(res));
        }
//        icons.get(0).setChosen(true);
        fakeIconAdapter = new FakeIconAdapter(icons);
        fakeIconAdapter.setOnFakeIconChosenListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rvIcon.setLayoutManager(layoutManager);
        rvIcon.setAdapter(fakeIconAdapter);
    }

    public void setCurIv(String path) {
        if (!TextUtils.isEmpty(path)) {
            fakeIconStr = path;
            ivCur.setImageDrawable(CommonUtil.loadFakeIconDrawable(path));
        }
    }

    public void showLocation(View view, AppData app) {
//        showAsDropDown(view, dip2px(mContext, -50), dip2px(mContext, -50));
        showAtLocation(view, Gravity.BOTTOM, 0, 0);
        appData = app;
        ivCur.setImageDrawable(CommonUtil.getAppFakeIcon(appData));
        etFakeTitle.setHint("默认：" + app.getName());
        String appFakeName = CommonUtil.getAppFakeName(appData);
        etFakeTitle.setText(appFakeName);
        etFakeTitle.setSelection(appFakeName.length());
        FakeIconModel fakeIconModel = new FakeIconModel(false, -1);
        fakeIconModel.setImgDrawable(app.getIcon());
        icons.add(0, fakeIconModel);
        fakeIconAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFakeChosen(int index) {
        fakeIconStr = String.valueOf(index - 1);

        if (index == 0) {
            ivCur.setImageDrawable(appData.getIcon());
        } else {
            ivCur.setImageResource(icons.get(index).getImgRes());
        }
    }

    private FakeAppOperateListener fakeAppOperateListener;

    public void setFakeAppOperateListener(FakeAppOperateListener fakeAppOperateListener) {
        this.fakeAppOperateListener = fakeAppOperateListener;
    }

    public interface FakeAppOperateListener {
        void onSubmit(String fakeTitle, String fakeIcon, int appId);

        void choosePic();
    }

}