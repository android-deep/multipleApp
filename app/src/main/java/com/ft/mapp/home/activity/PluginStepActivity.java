package com.ft.mapp.home.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.MenuItem;
import android.widget.EditText;

import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import androidx.annotation.Nullable;

public class PluginStepActivity extends VActivity {
    public static final int REQUEST_CODE = 1005;
    String mPkg;

    public static void start(Activity activity, String pkg) {
        Intent intent = new Intent(activity, PluginStepActivity.class);
        intent.putExtra("pkg", pkg);
        activity.startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.layout_plugin_step);

        Intent intent = getIntent();
        if (intent != null) {
            mPkg = intent.getStringExtra("pkg");
        }
        initViews();
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
        EditText editText = findViewById(R.id.editStepTimes);
        editText.setFilters(new InputFilter[]{new NumRangeInputFilter()});
        int stepTimes = AppSharePref.getInstance(PluginStepActivity.this).getInt(mPkg +
                        "_stepTimes",
                10);
        String s = String.valueOf(stepTimes);
        editText.setText(s);
        editText.setSelection(s.length());
        findViewById(R.id.btnSet).setOnClickListener(v -> {
            int times = Integer.parseInt(editText.getText().toString());
            AppSharePref.getInstance(PluginStepActivity.this).putInt(mPkg + "_stepTimes",
                    times);
            setResult(RESULT_OK);
            ToastUtil.show(this,"保存成功");
            finish();
        });
    }
}
