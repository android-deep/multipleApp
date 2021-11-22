package com.ft.mapp.home;

import android.os.Bundle;

import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.jaeger.library.StatusBarUtil;

import androidx.annotation.Nullable;

public class BuyTipActivity extends VActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.activity_buy_tip);
    }


}
