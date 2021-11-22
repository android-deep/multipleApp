package com.ft.mapp.home.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ft.mapp.R;
import com.ft.mapp.utils.ToastUtil;
import com.jaeger.library.StatusBarUtil;
import com.xqb.user.bean.VersionBean;
import com.xqb.user.net.engine.UserAgent;


public class FaqDetailActvity extends AppCompatActivity {
    public static final int ACTIVITY_TEXT = 1001;
    public static final int ACTIVITY_IMG = 1002;
    public static final int ACTIVITY_MIXTURE = 1003;
    public static final String ACTIVITY_TYPE = "ACTIVITY_TYPE";
    public static final String ACTIVITY_TITLE = "ACTIVITY_TITLE";
    public static final String ACTIVITY_CONTENT = "ACTIVITY_CONTENT";
    public static final String ACTIVITY_SRCNAME = "ACTIVITY_SRCNAME";
    private TextView tvTitle;
    private TextView tvSrc;
    private ImageView ivSrc;
    private String content;
    private String srcName;
    private TextView tvlockAccount;
    private TextView tvContactQQ;
    private TextView tvContactQQValue;
    private VersionBean versionBean;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorAccent));
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int type = bundle.getInt(ACTIVITY_TYPE);
        String title = bundle.getString(ACTIVITY_TITLE);
        if (type == ACTIVITY_TEXT) {
            setContentView(R.layout.activity_faq_detail_text);
            content = bundle.getString(ACTIVITY_CONTENT);
            tvSrc = findViewById(R.id.tv_src);
            tvlockAccount = findViewById(R.id.tv_lock_account);
            tvSrc.setText(Html.fromHtml(content));
            if (title.equals("分身是否会导致封号？")) {
                tvlockAccount.setVisibility(View.VISIBLE);
            }
        } else if (type == ACTIVITY_IMG) {
            setContentView(R.layout.activity_faq_detail_img);
            ivSrc = findViewById(R.id.iv_src);
            srcName = bundle.getString(ACTIVITY_SRCNAME);
            if (srcName.equals("faq_form1")) {
                ivSrc.setImageDrawable(getResources().getDrawable(R.drawable.faq_form));
            }
        } else if (type == ACTIVITY_MIXTURE) {
            setContentView(R.layout.activity_faq_detail_mixture);
            ivSrc = findViewById(R.id.iv_src);
            tvSrc = findViewById(R.id.tv_src);
            content = bundle.getString(ACTIVITY_CONTENT);
            srcName = bundle.getString(ACTIVITY_SRCNAME);
            tvSrc.setText(Html.fromHtml(content));
            if (srcName.equals("faq_form2")) {
                ivSrc.setImageDrawable(getResources().getDrawable(R.drawable.faq_form2));
            }
        }
        tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(title);
        tvContactQQ = findViewById(R.id.tv_contact_qq);
        tvContactQQValue = findViewById(R.id.tv_contact_qq_value);
        initContactView();
    }

    private void initContactView() {
        versionBean = UserAgent.getInstance(this).getVersionBean();
        if (versionBean != null && !TextUtils.isEmpty(versionBean.kefu)) {
            tvContactQQValue.setVisibility(View.VISIBLE);
            tvContactQQ.setVisibility(View.VISIBLE);
            tvContactQQValue.setText(versionBean.kefu);
        } else {
            tvContactQQValue.setVisibility(View.GONE);
            tvContactQQ.setVisibility(View.GONE);
        }
    }

    public void toLeanLockAccountActivity(View view) {
        Intent intent = new Intent(FaqDetailActvity.this, LearnLockAccoutActivity.class);
        startActivity(intent);
    }

    public void toQQContact(View view){
        try {
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + versionBean.kefu;
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(FaqDetailActvity.this, "请检查是否安装QQ");
        }
    }

}
