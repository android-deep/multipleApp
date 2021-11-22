package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;

public class CustomDialog extends AppCompatDialog implements View.OnClickListener {
    private TextView tvOk;
    private TextView tvMessage;
    private Context mContext;

    public CustomDialog(Context context) {
        super(context, R.style.VBDialogTheme);
        setContentView(R.layout.dialog_custom);
        this.mContext = context;

        initView();

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        tvOk = findViewById(R.id.custom_btn_cancel);
        tvMessage = findViewById(R.id.custom_tv_message);
        tvOk.setOnClickListener(this);
    }

    public void setMessage(String message){
        tvMessage.setText(message);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

}
