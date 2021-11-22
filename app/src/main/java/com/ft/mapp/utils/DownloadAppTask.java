package com.ft.mapp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.ft.mapp.dialog.CustomDialog;

import org.apache.http.conn.ssl.SSLSocketFactory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadAppTask extends AsyncTask<String, Integer, File> {

    private Context context;

    private ProgressDialog pd; //进度条对话框

    public DownloadAppTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载更新");
        pd.setProgressNumberFormat("%1d kb/%2d kb");//进度和总数
        pd.setCancelable(false);
        pd.show();
        super.onPreExecute();
    }

    @Override
    protected File doInBackground(String[] objects) {
        String path = objects[0];
        String verUrl = objects[1];
        String verNew = objects[2];
        HttpURLConnection conn = null;
        File file = null;
        //如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            URL url = null;
            try {
                url = new URL(path);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            try {
                if (url.getProtocol().toLowerCase().equals("https")) {
                    HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
                    https.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    conn = https;
                } else {
                    conn = (HttpURLConnection) url.openConnection();
                }
                conn.setConnectTimeout(10000);
                //获取到文件的大小
                int count = conn.getContentLength();
                InputStream is = conn.getInputStream();
                file = new File(Environment.getExternalStorageDirectory(), "multi." + verNew + ".apk");//下载到本地的app名
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                    //获取当前下载量
                    publishProgress(total, count);

                }
                fos.close();
                bis.close();
                is.close();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        } else {
            return null;
        }
        return file;
    }

    @Override
    protected void onProgressUpdate(Integer[] values) {
        pd.setProgress(values[0] / 1024);
        pd.setMax(values[1] / 1024);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(File file) {
        if (file != null) {
            //安装apk
            Intent intent = new Intent();
            //执行动作
            intent.setAction(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 24) {//判读版本是否在7.0以上
                //7.0以上需要通过FileProvider获取uri
                Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            } else {
                //执行的数据类型，此处Android应为android，否则造成安装不了
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            context.startActivity(intent);
            ((AppCompatActivity) context).finish();
        } else {
            pd.cancel();
//            pd.dismiss();
            // 下载app失败
//            AlertDialog dialog = new AlertDialog.Builder(context).setTitle("提示")
//                    .setMessage("应用下载失败")
//                    .setCancelable(false)
//                    .setPositiveButton("确定",
//                            (dialog1, which) -> dialog1.dismiss()).create();
            CustomDialog customDialog = new CustomDialog(context);
            customDialog.show();
            customDialog.setMessage("应用下载失败");
        }
    }
}