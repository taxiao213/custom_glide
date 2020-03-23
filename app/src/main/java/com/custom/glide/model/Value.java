package com.custom.glide.model;

import android.graphics.Bitmap;
import android.util.Log;

import com.custom.glide.utils.Tool;

/**
 * 对Bitmap的封装
 * TODO 包括使用次数计数，使用完成时回调，释放资源
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Value {
    private final String TAG = getClass().getSimpleName();

    private Bitmap bitmap;
    private String key;
    // 使用计数
    private int count;
    // 单利模式
    private static Value value;
    // 不在使用时的回调
    private ValueCallback valueCallback;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public static Value getInstance() {
        if (null == value) {
            synchronized (Value.class) {
                if (null == value) {
                    value = new Value();
                }
            }
        }
        return value;
    }

    public void setValueCallback(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    // TODO 使用一次就加1
    public void useAction() {
        Tool.checkNotEmpty(bitmap);
        if (bitmap.isRecycled()) {
            Log.d(TAG, "useAction: 已经被回收了");
            return;
        }
        count++;
        Log.d(TAG, "useAction: 加一 count:" + count);
    }

    // TODO 使用完成（不使用） 就 减一
    //  * count -- <= 0  不再使用了
    public void nonUseAction() {
        count--;
        if (count <= 0 && valueCallback != null) {
            // 回调
            valueCallback.valueNonUseListener(key, value);
        }
        Log.d(TAG, "nonUseAction: 减一 count:" + count);
    }

    // TODO Bitmap回收
    public void recycleBitmap() {
        if (count > 0) {
            Log.d(TAG, "recycleBitmap: 引用计数大于0，证明还在使用中，不能去释放...");
            return;
        }
        if (bitmap.isRecycled()) {
            Log.d(TAG, "recycleBitmap: bitmap已经被回收...");
            return;
        }
        bitmap.recycle();
        value = null;
        System.gc();
    }
}
