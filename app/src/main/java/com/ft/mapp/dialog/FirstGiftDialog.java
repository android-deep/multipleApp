package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.util.UmengStat;

import java.util.concurrent.TimeUnit;

import jonathanfinerty.once.Once;

public class FirstGiftDialog extends AppCompatDialog implements View.OnClickListener {
    private ImageView iv;
    private FrameLayout layoutLoading;
    private Context mContext;
    private String tag;

    public FirstGiftDialog(Context context, String tag) {
        super(context, R.style.VBDialogTheme);
        setContentView(R.layout.dialog_first_gift);
        this.mContext = context;
        this.tag = tag;
        initView();

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setCanceledOnTouchOutside(false);
    }

    private void initView() {
        iv = findViewById(R.id.dialog_gift_iv);
        findViewById(R.id.dialog_gift_tv_cancel).setOnClickListener(this);
        findViewById(R.id.dialog_gift_tv_receive).setOnClickListener(this);
        layoutLoading = findViewById(R.id.first_gift_loading);
        iv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_gift_tv_cancel:
                StatAgent.onEvent(getContext(), UmengStat.VIP_CANCEL, "name", tag + "_" + UserAgent.getInstance(getContext()).userType());
                dismiss();
                break;
            case R.id.dialog_gift_tv_receive:
                if (!Once.beenDone(TimeUnit.SECONDS, 3, "receive_vip")) {
                    if (onReceiveGiftListener != null) {
                        onReceiveGiftListener.receive();
                    }
                    StatAgent.onEvent(getContext(), UmengStat.VIP_RECEIVE_CLICK, "name", tag + "_" + UserAgent.getInstance(getContext()).userType());
                    Once.markDone("receive_vip");
                }
                break;
        }
    }

    public void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private OnReceiveGiftListener onReceiveGiftListener;

    public void setOnReceiveGiftListener(OnReceiveGiftListener onReceiveGiftListener) {
        this.onReceiveGiftListener = onReceiveGiftListener;
    }

    public interface OnReceiveGiftListener {
        void receive();
    }

}
