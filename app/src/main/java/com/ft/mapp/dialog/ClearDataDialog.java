package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;
import com.ft.mapp.listener.OnDialogListener;
import com.xqb.user.bean.VersionBean;


public class ClearDataDialog extends AppCompatDialog implements View.OnClickListener {

    private TextView tvVersion;
    private TextView tvDesc;
    private TextView btnEnsure;
    private TextView btnCancel;
    private OnDialogListener mListener;

    public ClearDataDialog(Context context) {
        this(context, R.style.VBDialogTheme);
    }

    public ClearDataDialog(Context context, int theme) {
        super(context, theme);
        setContentView(R.layout.dialog_clear_data);

        fullWindow();
//        tvVersion = findViewById(R.id.dialog_update_tv_version);
//        tvDesc = findViewById(R.id.update_tv_desc);
        btnEnsure = findViewById(R.id.clear_data_btn_ensure);
        btnCancel = findViewById(R.id.clear_data_btn_cancel);
        btnEnsure.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
//        if (force){
            setCancelable(true);
//            btnCancel.setVisibility(View.GONE);
//        }
    }

    private void fullWindow() {
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public ClearDataDialog setOnDialogListener(OnDialogListener listener) {
        this.mListener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.clear_data_btn_cancel) {
            dismiss();
        } else if (v.getId() == R.id.clear_data_btn_ensure) {
            if (mListener != null) {
                mListener.onOk();
            }
            dismiss();
        }
    }
}
