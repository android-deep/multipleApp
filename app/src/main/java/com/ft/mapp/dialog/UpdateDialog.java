package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;
import com.ft.mapp.listener.OnDialogListener;
import com.xqb.user.bean.VersionBean;


public class UpdateDialog extends AppCompatDialog implements View.OnClickListener {

    private TextView tvVersion;
    private TextView tvDesc;
    private TextView btnEnsure;
    private TextView btnCancel;
    private OnDialogListener mListener;
    private boolean force;

    public UpdateDialog(Context context, boolean force) {
        this(context,force, R.style.VBDialogTheme);
    }

    public UpdateDialog(Context context, boolean force, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_update);

        fullWindow();
        tvVersion = findViewById(R.id.dialog_update_tv_version);
        tvDesc = findViewById(R.id.update_tv_desc);
        btnEnsure = findViewById(R.id.update_btn_ensure);
        btnCancel = findViewById(R.id.update_btn_cancel);
        btnEnsure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        if (force){
            setCancelable(false);
            btnCancel.setVisibility(View.GONE);
        }
    }

    private void fullWindow() {
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public void setInfo(VersionBean versionBean) {
        tvVersion.setText(versionBean.version);
        tvDesc.setText(versionBean.description);
    }

    public UpdateDialog setOnDialogListener(OnDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.update_btn_cancel) {
            if (mListener != null) {
                mListener.onCancel();
            }
            dismiss();
        } else if (v.getId() == R.id.update_btn_ensure) {
            if (mListener != null) {
                mListener.onOk();
            }
            dismiss();
        }
    }
}
