package com.ft.mapp.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xqb.user.net.engine.StatAgent;
import com.ft.mapp.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xqb.user.util.UmengStat;

import java.util.Locale;

import androidx.appcompat.app.AppCompatDialog;

public class ShareDialog extends AppCompatDialog implements View.OnClickListener {
    private Context mContext;
    private String mFrom;
    private String url = "https://app.fntmob.com/";

    public ShareDialog(Context context) {
        this(context, "");
    }

    public ShareDialog(Context context, String from) {
        super(context, R.style.MyDialogStyleBottom);
        setContentView(R.layout.dialog_share);
        this.mContext = context;
        this.mFrom = from;
        initView();

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getWindow().setGravity(Gravity.BOTTOM);
        }
    }

    private void initView() {
        findViewById(R.id.dialog_share_wx).setOnClickListener(this);
        findViewById(R.id.dialog_share_qq).setOnClickListener(this);
        findViewById(R.id.dialog_share_more).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_share_wx) {
            shareToWeChat(SHARE_MEDIA.WEIXIN);
        } else if (v.getId() == R.id.dialog_share_qq) {
            shareToWeChat(SHARE_MEDIA.QQ);
        } else if (v.getId() == R.id.dialog_share_more) {
            shareOther();
        }
    }

    /**
     * https://blog.csdn.net/m0_37711172/article/details/80065280\
     * https://juejin.im/post/5c09e118f265da61483b6939
     */

    private void shareToWeChat(SHARE_MEDIA media) {

        new ShareAction((Activity) mContext)
                .setPlatform(media)
                .withMedia(getShareContent())
                .setCallback(new UMShareListener() {
                    @Override
                    public void onStart(SHARE_MEDIA share_media) {
                        StatAgent.onEvent(mContext, UmengStat.SHARE_START, "from", mFrom);
                    }

                    @Override
                    public void onResult(SHARE_MEDIA share_media) {
                        StatAgent.onEvent(mContext, UmengStat.SHARE_SUCCESS, "from", mFrom);
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media) {
                    }
                })
                .share();

    }

    private UMWeb getShareContent() {
        UMWeb web = new UMWeb(url);
        web.setTitle(mContext.getString(R.string.share_title));//标题
//        web.setThumb(new UMImage(mContext, "https://pp.myapp.com/ma_icon/0/icon_53909054_1576913234/96"));  //缩略图
        web.setThumb(new UMImage(mContext, "http://app.fntmob.com/website/images/logo.png"));  //缩略图
        web.setDescription(mContext.getString(R.string.share_content));//描述
        return web;
    }


    private void shareOther() {
        String content = String.format(Locale.getDefault(), "%s%s",
                mContext.getString(R.string.share_content),
                "\n"+url);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getString(R.string.share_title));
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("sms_body", content);
        intent.setType("text/plain");

        mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.invite_friends)));
        StatAgent.onEvent(mContext, UmengStat.SHARE_CLICK);
    }
}
