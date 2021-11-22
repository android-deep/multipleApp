package com.ft.mapp.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ft.mapp.BuildConfig;;
import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.jaeger.library.StatusBarUtil;
import com.xqb.user.bean.UserInfo;
import com.xqb.user.net.engine.UserAgent;

import java.util.Locale;

import androidx.annotation.Nullable;

public class AboutActivity extends VActivity implements View.OnClickListener {

    private TextView mUseridTv;
    private int mSecretNumber = 0;
    private static final long MIN_CLICK_INTERVAL = 500;
    private long mLastClickTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.activity_about);
        TextView versionTv = findViewById(R.id.about_app_verion);
        versionTv.setText(String.format(Locale.getDefault(), "%s V%s",
                getString(R.string.app_name), BuildConfig.VERSION_NAME));

        mUseridTv = findViewById(R.id.about_uuid_tv);

        findViewById(R.id.about_privacy_tv).setOnClickListener(this);
        findViewById(R.id.about_icon).setOnClickListener(this);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int vId = v.getId();
        if (vId == R.id.about_privacy_tv) {
            Intent intent1 = new Intent(this, PrivacyPolicyActivity.class);
            intent1.putExtra("action", getString(R.string.service_privacy_policy));
            startActivity(intent1);
        } else if (vId == R.id.about_icon) {
            checkDebug();
        }

    }

    private void checkDebug() {
        long currentClickTime = SystemClock.uptimeMillis();
        long elapsedTime = currentClickTime - mLastClickTime;
        mLastClickTime = currentClickTime;

        if (elapsedTime < MIN_CLICK_INTERVAL) {
            ++mSecretNumber;
            if (5 == mSecretNumber) {
                UserInfo userInfo = UserAgent.getInstance(this).getUserInfo();
                if (userInfo != null && !TextUtils.isEmpty(userInfo.userId)) {
                    mUseridTv.setText(getString(R.string.user_id, userInfo.userId));
                    mUseridTv.setVisibility(View.VISIBLE);
                }
            }
        } else {
            mSecretNumber = 0;
        }
    }
}
