package com.custom.glide.cache.disk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.custom.glide.cache.pool.BitmapPool;
import com.custom.glide.load.Glide;
import com.custom.glide.model.Value;
import com.custom.glide.utils.Tool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 磁盘缓存的封装
 * 来源：https://github.com/JakeWharton/DiskLruCache
 * <p>
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class DiskLruCacheImpl {
    private final String TAG = getClass().getSimpleName();
    // 磁盘缓存的的目录
    private final String DISKLRU_CACHE_DIR = "disk_lru_cache_dir";
    // 我们的版本号，一旦修改这个版本号，之前的缓存失效
    private final int APP_VERSION = 1;
    // 通常情况下都是1
    private final int VALUE_COUNT = 1;
    // 使用者可以自己去配置 100M
    private final int MAX_SIZE = 1024 * 1024 * 100;

    private DiskLruCache diskLruCache;

    public DiskLruCacheImpl(Context context) {
        File file = context.getDir(DISKLRU_CACHE_DIR, Context.MODE_PRIVATE);
        try {
            diskLruCache = DiskLruCache.open(file, APP_VERSION, VALUE_COUNT, MAX_SIZE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO put 存入Value
    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        OutputStream outputStream = null;
        DiskLruCache.Editor edit = null;
        try {
            edit = diskLruCache.edit(key);
            // index 不能大于 VALUE_COUNT
            outputStream = edit.newOutputStream(0);
            // 把bitmap写入到outputStream
            Bitmap bitmap = value.getBitmap();
            if (bitmap != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                // Aborts this edit. This releases the edit lock so another edit may be
                // 中止此编辑。 这会释放编辑锁，因此可能需要进行其他编辑
                if (edit != null) {
                    edit.abort();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                Log.e(TAG, "put: editor.abort(); e:" + ex.getMessage());
            }
        } finally {
            try {
                if (edit != null) {
                    edit.commit();
                }
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "put: editor.commit(); e:" + e.getMessage());
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "put: outputStream.close(); e:" + e.getMessage());
                }
            }

        }
    }

    // TODO get 获取Value
    public Value get(String key, BitmapPool bitmapPool) {
        InputStream inputStream = null;
        try {
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            // 判断快照不为null的情况下，在去读取操作
            if (snapshot != null) {
                // index 不能大于 VALUE_COUNT
                inputStream = snapshot.getInputStream(0);
                Bitmap bitmap = Tool.getIOBitmap(inputStream, bitmapPool, true);

                Value value = Value.getInstance();
                value.setKey(key);
                value.setBitmap(bitmap);
                return value;
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "get: IOException; e:" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get: inputStream.close(); e:" + e.getMessage());
                }
            }
        }
        return null;
    }
}
