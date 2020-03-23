package com.custom.glide.cache.pool;

import android.graphics.Bitmap;

/**
 *  Bitmap复用内存，防止内存抖动
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public interface BitmapPool {
    /**
     * 加入到Bitmap内存复用池
     * @param bitmap
     */
    void put(Bitmap bitmap);

    /**
     * 从Bitmap内存复用池里面取出来
     * @param width
     * @param height
     * @param config
     * @return
     */
    Bitmap get(int width, int height, Bitmap.Config config);

}
