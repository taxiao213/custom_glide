package com.custom.glide.cache.pool;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.custom.glide.utils.Tool;

import java.util.TreeMap;

/**
 * Bitmap 复用池 复用内存
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class LruBitmapPool extends LruCache<Integer, Bitmap> implements BitmapPool {
    private final String TAG = getClass().getSimpleName();
    // 对Integer来说，其自然排序就是数字的升序 ; 对String来说，其自然排序就是按照字母表排序
    private TreeMap<Integer, Bitmap> treeMap = new TreeMap<>();

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public LruBitmapPool(int maxSize) {
        super(maxSize);
    }

    @Override
    public void put(Bitmap bitmap) {
        Tool.checkNotEmpty(bitmap);
        // TODO 复用的条件1 bitmap.isMutable()
        if (!bitmap.isMutable()) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
                Log.d(TAG, "put: 复用的条件1 Bitmap.ismutable 是false，条件不满足，不能复用 添加..." + bitmap);
                return;
            }
        }
        // TODO 复用的条件2 如果添加复用的Bitmap大小，大于Lru MaxSize 就不复用
        int bitmapByteSize = Tool.getBitmapByteSize(bitmap);
        if (bitmapByteSize > maxSize()) {
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            Log.d(TAG, "put: 复用的条件2 Bitmap.Size大于LruMaxSize，条件不满足，不能复用 添加...");
            return;
        }
        // 添加到 Lru Cahce中去
        put(bitmapByteSize, bitmap);
        // 保存到 TreeMap 是为了筛选
        treeMap.put(bitmapByteSize, null);
        Log.d(TAG, "put: 添加到复用池了....");
    }

    @Override
    public Bitmap get(int width, int height, Bitmap.Config config) {
        if (treeMap.isEmpty()) {
            Log.d(TAG, "treeMap:为空");
            return null;
        }
        // config为null 默认给 Bitmap.Config.ARGB_8888
        int bitmapByteSize = Tool.getBitmapByteSize(width, height, config);
        // TODO ceilingKey 获得 getSize这么大的key，同时还可以获得 比 getSize还要大的key
        Integer key = treeMap.ceilingKey(bitmapByteSize);// 获得 getSize这么大的key，同时还可以获得 比 getSize还要大的key
        if (key == null) {
            Log.d(TAG, "treeMap:找不到 保存的key");
            return null; // 如果找不到 保存的key，就直接返回null，无法复用
        }
        // 找出来的key 小于等于 （getSize * 2）
        if (key <= (bitmapByteSize * 2)) {
            Bitmap resultBitmap = remove(key);
            Log.d(TAG, "get: 从复用池获取:" + resultBitmap);
            return resultBitmap;
        }
        return null;
    }

    @Override
    protected int sizeOf(Integer key, Bitmap value) {
        //  return super.sizeOf(key, value);
        return Tool.getBitmapByteSize(value);
    }

    @Override
    protected void entryRemoved(boolean evicted, Integer key, Bitmap oldValue, Bitmap newValue) {
        // super.entryRemoved(evicted, key, oldValue, newValue);
        // 把treeMap 里面的给移除
        treeMap.remove(key);
    }
}
