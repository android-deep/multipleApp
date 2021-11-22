package com.ft.mapp.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;
import com.ft.mapp.utils.ActFillUtils;
import com.xqb.user.net.engine.AdAgent;

public class TutorialsDialog extends AppCompatDialog implements View.OnClickListener {
    private TextView tvClose;

    public TutorialsDialog(Context context) {
        super(context, R.style.MyDialogStyleBottom);
        setContentView(R.layout.dialog_tutorials);
        initView();
        setCanceledOnTouchOutside(false);
        if (getWindow() != null) {
            getWindow().setGravity(Gravity.CENTER);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void initView() {
        tvClose = findViewById(R.id.dialog_tutorials_tv_close);
        tvClose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_tutorials_tv_close) {
            dismiss();
        }
    }
}
