package com.custom.glide.cache;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.custom.glide.model.Value;
import com.custom.glide.utils.Tool;

/**
 * 内存缓存 使用 Lru算法
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class MemoryCache extends LruCache<String, Value> {

    private boolean shoudonRemove;

    private MemoryCacheCallback memoryCacheCallback;

    // 设置监听 被动移除时使用
    public MemoryCache(int maxSize, MemoryCacheCallback memoryCacheCallback) {
        super(maxSize);
        this.memoryCacheCallback = memoryCacheCallback;
    }

    /**
     * 手动移除
     *
     * @param key
     * @return
     */
    public Value shoundonRemove(String key) {
        shoudonRemove = true;
        Value remove = remove(key);
        shoudonRemove = false;
        return remove;
    }

    @Override
    protected int sizeOf(@NonNull String key, @NonNull Value value) {
        Bitmap bitmap = value.getBitmap();
        // 最开始的时候
        // int result = bitmap.getRowBytes() * bitmap.getHeight();

        // API 12  3.0
        // result = bitmap.getByteCount(); // 在bitmap内存复用上有区别 （所属的）

        // API 19 4.4
        // result = bitmap.getAllocationByteCount(); // 在bitmap内存复用上有区别 （整个的）
        return Tool.getBitmapByteSize(bitmap);
    }

    /**
     * 被移除时监听
     * 1.重复的key
     * 2.最少使用的元素会被移除
     *
     * @param evicted
     * @param key
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void entryRemoved(boolean evicted, String key, Value oldValue, Value newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
        // !shoudonRemove == 被动的
        if (memoryCacheCallback != null && !shoudonRemove) {
            memoryCacheCallback.entryRemovedMemoryCache(key, oldValue);
        }
    }
}
