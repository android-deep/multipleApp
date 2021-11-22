package com.ft.mapp.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Switch;

import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.utils.AppSharePref;
import com.jaeger.library.StatusBarUtil;

import androidx.annotation.Nullable;


public class PluginTiktokActivity extends VActivity {

    private Switch mTikSwitch;

    public static void launchTikTokSetting(Activity activity) {
        Intent intent = new Intent(activity, PluginTiktokActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.layout_tiktok_plugin);

        initViews();
        initData();
    }

    private void initData() {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        mTikSwitch = findViewById(R.id.tik_switch);
        mTikSwitch.setChecked(AppSharePref.getInstance(this).getBoolean(AppSharePref.KEY_TIK_PLUGIN_ENABLE));
        mTikSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            AppSharePref.getInstance(this).putBoolean(AppSharePref.KEY_TIK_PLUGIN_ENABLE, isChecked);
        });

    }
}
