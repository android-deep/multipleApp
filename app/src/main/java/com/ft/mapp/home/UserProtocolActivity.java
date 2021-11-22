package com.ft.mapp.home;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ScrollView;

import androidx.annotation.Nullable;

import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.jaeger.library.StatusBarUtil;

public class UserProtocolActivity extends VActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.activity_user_protocol);
        ScrollView scrollView = findViewById(R.id.scroll_view);
        scrollView.smoothScrollTo(0,0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
