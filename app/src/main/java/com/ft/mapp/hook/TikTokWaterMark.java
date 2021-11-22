package com.ft.mapp.hook;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ft.mapp.R;
import com.ft.mapp.VApp;
import com.ft.mapp.delegate.IPlugin;
import com.ft.mapp.delegate.MultiPlugin;
import com.ft.mapp.listener.SimpleDownloadListener;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.CommonUtil;
import com.fun.vbox.client.VClient;
import com.fun.vbox.client.core.VCore;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import andhook.lib.xposed.XC_MethodHook;
import andhook.lib.xposed.XposedHelpers;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class TikTokWaterMark {
    private static final String TAG = "TikTok";

    private static HashMap<String, FloatView> sMapFloatView = new HashMap<>();
    private static WeakReference<FloatView> sCurFloatView;

    private static class FloatView extends IPlugin {
        View toolView;
        String downloadUrl;

        @Override
        public View onCreateView(Activity activity, ViewGroup container) {
            toolView = LayoutInflater.from(VCore.get().getContext())
                    .inflate(R.layout.float_tiktok, null);
            ImageView downloadIv = toolView.findViewById(R.id.tik_download_iv);

            downloadIv.setOnClickListener(view1 -> {
                if (TextUtils.isEmpty(downloadUrl)) {
                    return;
                }
                Log.i(TAG,downloadUrl);
                if (ActivityCompat.checkSelfPermission(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    return;
                }
                final ProgressDialog dialog = new ProgressDialog(activity);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setMessage("Downloading");
                dialog.show();

                final File file =
                        new File(CommonUtil.getCameraPath(), CommonUtil.md5(downloadUrl) + ".mp4");
                DownloadTask task = new DownloadTask
                        .Builder(downloadUrl, file)
                        .setPassIfAlreadyCompleted(false)
                        .setMinIntervalMillisCallbackProcess(80)
                        .setAutoCallbackToUIThread(true)
                        .build();
                task.enqueue(new SimpleDownloadListener() {
                    private long totalOffset;
                    private long totalLength;

                    @Override
                    public void downloadFromBreakpoint(@NonNull DownloadTask task,
                                                       @NonNull BreakpointInfo info) {
                        totalLength = Objects.requireNonNull(task.getInfo()).getTotalLength();
                    }

                    @Override
                    public void fetchStart(@NonNull DownloadTask task, int blockIndex,
                                           long contentLength) {
                        totalLength = Objects.requireNonNull(task.getInfo()).getTotalLength();
                    }

                    @Override
                    public void fetchProgress(@NonNull DownloadTask task, int blockIndex,
                                              long increaseBytes) {
                        totalOffset = Objects.requireNonNull(task.getInfo()).getTotalOffset();
                        if (totalLength == 0L) {
                            return;
                        }
                        long progress = (totalOffset * 100 / totalLength);
                        dialog.setProgress((int) progress);
                        dialog.setMessage("Downloading：" + progress + "%");
                    }

                    @Override
                    public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause,
                                        @Nullable Exception realCause) {
                        if (cause == EndCause.COMPLETED) {
                            Toast.makeText(VCore.get().getContext(), "已保存到相册中",
                                    Toast.LENGTH_LONG).show();

                            dialog.dismiss();
                            FloatView.this.show(true);

                            Uri uri = Uri.fromFile(file);
                            VCore.get().getContext().sendBroadcast(
                                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                        } else if (cause == EndCause.ERROR) {
                            dialog.dismiss();
                            Toast.makeText(VCore.get().getContext(), "Download Failed!!!",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

            });
            return toolView;
        }

        @Override
        public boolean onPreCheck(Activity activity) {
            return true;
        }

        public void setDownloadUrl(String url) {
            downloadUrl = url;
        }
    }

    public static void handleLoadPackage() {
        if (!AppSharePref.getInstance(VApp.getApp()).getBoolean(AppSharePref.KEY_TIK_PLUGIN_ENABLE)) {
            return;
        }

        String pkgName = VClient.get().getCurrentPackage();
        if (!"com.ss.android.ugc.trill".equals(pkgName)
                && !"com.ss.android.ugc.aweme".equals(pkgName)
                && !"com.ss.android.ugc.aweme.lite".equals(pkgName)
                && !"com.zhiliaoapp.musically".equals(pkgName)) {
            return;
        }

        ClassLoader classLoader = VClient.get().getClassLoader();
        final String className1 = "com.ss.android.ugc.aweme.feed.model.Aweme";
        final String methodName1 = "getStatus";
        final String methodName2 = "shouldAddClientWaterMark";
        final String fieldName1 = "inReviewing";
        final String fieldName2 = "selfSee";

        try {
            XposedHelpers.findAndHookMethod(className1, classLoader, methodName1,
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            String stackTrace = Log.getStackTraceString(new Throwable());
                            if (stackTrace.contains(methodName2)) {
                                Log.e(TAG, "remove Watermark suc ");
                                try {
                                    Object awemeStatus = param.getResult();
                                    XposedHelpers.setObjectField(awemeStatus, fieldName1, true);
                                    XposedHelpers.setObjectField(awemeStatus, fieldName2, true);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(TAG, "method not contained ");
                            }
                        }
                    });
        } catch (Throwable e) {
            Log.e(TAG, "error", e);
        }

        final String className1_video = "com.ss.android.ugc.aweme.feed.model.Video";
        final String methodName_addres = "getPlayAddr";
        final String methodName_selected = "onPageSelected";
        final String methodName_available = "onSurfaceTextureAvailable";
        final String fieldName_d = "downloadAddr";
        final String fieldName_p = "playAddr";
        final String fieldName_list = "urlList";

        try {
            XposedHelpers.findAndHookMethod(className1_video, classLoader, methodName_addres,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                String stackTrace = Log.getStackTraceString(new Throwable());

                                if (stackTrace.contains(methodName_selected) ||
                                        (stackTrace.contains(methodName_available) &&
                                                stackTrace.contains("getProperPlayAddr"))) {
                                    //Log.e(TAG, "remove watermark work", new Exception());

                                    Object downloadAddr = XposedHelpers
                                            .getObjectField(param.thisObject, fieldName_d);
                                    Object playAddr = XposedHelpers
                                            .getObjectField(param.thisObject, fieldName_p);

                                    Object playUrlList =
                                            XposedHelpers.getObjectField(playAddr, fieldName_list);
                                    //Object downUrlList = XposedHelpers.getObjectField(downloadAddr, fieldName_list);

                                    if (playUrlList instanceof List) {
                                        List list = (List) playUrlList;
//                                        Log.e(TAG, "list:" + list);
                                        if (sCurFloatView != null) {
                                            FloatView view = sCurFloatView.get();
                                            view.setDownloadUrl(list.get(0).toString());
                                        }
                                    }
                                    //Log.e(TAG, "downUrlList:" + downUrlList);

                                    XposedHelpers.setObjectField(downloadAddr, fieldName_list,
                                            playUrlList);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "  hook  methodName_addres failed !! ", e);
                            }
                        }
                    });

            XposedHelpers.findAndHookMethod(Activity.class, "onResume",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            Activity act = (Activity) param.thisObject;
                            String key = act.getClass().hashCode() + "";
                            Log.e(TAG, "onResume:" + act.getClass().getCanonicalName() + " key:" +
                            key);
                            FloatView floatView = sMapFloatView.get(key);
                            if (floatView == null) {
                                floatView = new FloatView();
                                sMapFloatView.put(key, floatView);
                                floatView.showNow(act);
                            }
                            sCurFloatView = new WeakReference<>(floatView);
                        }
                    });

            XposedHelpers.findAndHookMethod(Activity.class, "onDestroy",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            Activity act = (Activity) param.thisObject;
                            String key = act.getClass().hashCode() + "";
                            Log.e(TAG, "onDestroy:" + act.getClass().getCanonicalName() + " key:" +
                                    key);
                            FloatView floatView = sMapFloatView.get(key);
                            if (floatView != null) {
                                floatView.destroy(act);
                                sMapFloatView.remove(key);
                            }
                        }
                    });
        } catch (Throwable e) {
            Log.e(TAG, "error", e);
        }
    }
}