package com.ft.mapp.dialog;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatDialog;

import com.ft.mapp.R;
import com.ft.mapp.home.activity.WebViewActivity;
import com.ft.mapp.utils.ActFillUtils;
import com.ft.mapp.utils.ShopAppUtil;
import com.xqb.user.net.engine.AdAgent;
import com.xqb.user.net.engine.UserAgent;

public class ActDialog extends AppCompatDialog implements View.OnClickListener {
    private ImageView ivMain;
    private ImageView ivClose;

    public ActDialog(Context context) {
        this(context, "");
    }

    public ActDialog(Context context, String from) {
        super(context, R.style.MyDialogStyleBottom);
        setContentView(R.layout.dialog_act);
        initView();
        setCanceledOnTouchOutside(false);
        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void initView() {
        ivMain = findViewById(R.id.dialog_act_iv);
        ivClose = findViewById(R.id.dialog_act_iv_close);
        ivClose.setOnClickListener(this);

        ActFillUtils.fillAd(ivMain, AdAgent.loadHomeDialogAct());

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_act_iv_close) {
            closeAnim();
        }
    }

    private void closeAnim(){
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f,Animation.RELATIVE_TO_SELF,1.0f,Animation.RELATIVE_TO_SELF,1.0f);
        scaleAnimation.setDuration(300);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivClose.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        ivMain.startAnimation(scaleAnimation);

    }

    @Override
    public void show() {
        super.show();
        ivClose.setVisibility(View.VISIBLE);
    }
}
