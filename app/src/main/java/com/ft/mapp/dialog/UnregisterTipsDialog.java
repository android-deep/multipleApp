package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;
import com.ft.mapp.listener.OnDialogListener;


public class UnregisterTipsDialog extends AppCompatDialog implements View.OnClickListener {

    private OnDialogListener mListener;

    public UnregisterTipsDialog(Context context) {
        this(context, R.style.VBDialogTheme);
    }

    public UnregisterTipsDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_unregister);

        fullWindow();
        findViewById(R.id.dialog_unregister_tv_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_unregister_tv_ok).setOnClickListener(this);
    }

    private void fullWindow() {
        if (getWindow() != null) {
//            getWindow().setLayout((int) (ScreenUtil.getScreenWidth(getContext())*0.8), ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setMessage(String msg) {
//        mMsgTv.setText(msg);
    }

    public UnregisterTipsDialog setOnDialogListener(OnDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_unregister_tv_cancel) {
            if (mListener != null) {
                mListener.onCancel();
            }
            dismiss();
        } else if (v.getId() == R.id.dialog_unregister_tv_ok) {
            if (mListener != null) {
                mListener.onOk();
            }
            dismiss();
        }
    }
}
