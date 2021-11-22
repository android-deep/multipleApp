package com.ft.mapp.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ft.mapp.R;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.utils.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import androidx.annotation.Nullable;

public class FeedbackActivity extends VActivity {

    public static void gotoFeedback(Activity activity) {
        Intent intent = new Intent(activity, FeedbackActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setColorNoTranslucent(this,getResources().getColor(R.color.colorAccent));
        setContentView(R.layout.activity_feedback);
        joinQQ();

    }

    /**
     * 跳转QQ聊天界面
     */
    public void joinQQ() {
        try {
            //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=" + 1569436108;//uin是发送过去的qq号码
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.show(getActivity(), "请检查是否安装QQ");
        }
    }


}
