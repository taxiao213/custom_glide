package com.custom.glide.load_data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.custom.glide.model.Value;
import com.custom.glide.utils.Tool;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 加载本地 或者 网络请求图片
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class LoadDataManager implements ILoadData, Runnable {
    private final String TAG = getClass().getSimpleName();
    private String path;
    private ResponseListener responseListener;

    @Override
    public Value loadResource(String path, Context context, ResponseListener responseListener) {
        this.path = path;
        this.responseListener = responseListener;

        Tool.checkNotEmpty(path);
        Uri uri = Uri.parse(path);
        if (uri != null) {
            String scheme = uri.getScheme();
            if ("HTTP".equalsIgnoreCase(scheme) || "HTTPS".equalsIgnoreCase(scheme)) {
                // 加载 网络图片
                ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
                threadPoolExecutor.execute(this);
            } else {
                if (!TextUtils.isEmpty(path)) {
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    Value value = Value.getInstance();
                    value.setBitmap(bitmap);
                    // 回调成功
                    Log.d(TAG, " LoadDataManager 从本地获取到bitmap");
                    responseListener.responseSuccess(value);
                }
            }
        }
        return null;
    }

    @Override
    public void run() {
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            httpURLConnection = (HttpURLConnection) urlConnection;
            httpURLConnection.setReadTimeout(5000);
            final int responseCode = httpURLConnection.getResponseCode();
            if (HttpURLConnection.HTTP_OK == responseCode) {
                inputStream = httpURLConnection.getInputStream();
                final Bitmap bitmap = Tool.getIOBitmap(inputStream, null, false);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Value value = Value.getInstance();
                        value.setBitmap(bitmap);
                        responseListener.responseSuccess(value);
                    }
                });
            } else {
                // 失败 切换主线程
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        responseListener.responseException(new IllegalStateException("请求失败 请求码:" + responseCode));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    e.printStackTrace();
                    Log.d(TAG, "run: 关闭 inputStream.close(); e:" + e.getMessage());
                }
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }
}
