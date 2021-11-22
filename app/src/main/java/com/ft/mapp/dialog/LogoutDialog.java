package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ft.mapp.R;
import com.ft.mapp.listener.OnDialogListener;

import androidx.appcompat.app.AppCompatDialog;


public class LogoutDialog extends AppCompatDialog implements View.OnClickListener {

    private TextView mMsgTv;
    private OnDialogListener mLisenter;

    public LogoutDialog(Context context) {
        this(context, R.style.VBDialogTheme);
    }

    public LogoutDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.layout_logout);

        fullWindow();
        mMsgTv = findViewById(R.id.logout_title_tv);
        findViewById(R.id.logout_button).setOnClickListener(this);
        findViewById(R.id.cancel_button).setOnClickListener(this);
    }

    private void fullWindow() {
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setMessage(String msg) {
        mMsgTv.setText(msg);
    }

    public LogoutDialog setOnDialogLisenter(OnDialogListener lisenter) {
        this.mLisenter = lisenter;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.cancel_button) {
            if (mLisenter != null) {
                mLisenter.onCancel();
            }
            dismiss();
        } else if (v.getId() == R.id.logout_button) {
            if (mLisenter != null) {
                mLisenter.onOk();
            }
            dismiss();
        }
    }
}
