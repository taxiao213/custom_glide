package com.custom.glide.load;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.custom.glide.cache.ActiveCache;
import com.custom.glide.cache.MemoryCache;
import com.custom.glide.cache.MemoryCacheCallback;
import com.custom.glide.cache.disk.DiskLruCacheImpl;
import com.custom.glide.cache.pool.LruBitmapPool;
import com.custom.glide.fragment.LifecycleCallback;
import com.custom.glide.load_data.LoadDataManager;
import com.custom.glide.load_data.ResponseListener;
import com.custom.glide.model.Key;
import com.custom.glide.model.Value;
import com.custom.glide.model.ValueCallback;
import com.custom.glide.utils.Tool;

/**
 * 请求加载引擎
 * 实现活动缓存回调，内存缓存回调，生命周期回调，网络请求结果回调
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class RequestTargetEngine implements ValueCallback, MemoryCacheCallback, LifecycleCallback, ResponseListener {
    private final String TAG = getClass().getSimpleName();
    private Context context;
    private String path;
    private String key; // 加密后的 ac037ea49e34257dc5577d1796bb137dbaddc0e42a9dff051beee8ea457a4668
    private ImageView imageView; // 显示的目标
    private ActiveCache activeCache;// 活动缓存
    private MemoryCache memoryCache;// 内存缓存
    private DiskLruCacheImpl diskLruCache;// 磁盘缓存
    private LruBitmapPool lruBitmapPool;// 复用池
    // 使用者可以自己去配置 60M
    private final int MAX_SIZE = 1024 * 1024 * 60;

    public RequestTargetEngine() {
        if (activeCache == null) {
            activeCache = new ActiveCache(this);
        }
        if (memoryCache == null) {
            memoryCache = new MemoryCache(MAX_SIZE, this);
        }
        if (diskLruCache == null) {
            diskLruCache = new DiskLruCacheImpl();
        }
        if (lruBitmapPool == null) {
            lruBitmapPool = new LruBitmapPool(MAX_SIZE);
        }
    }

    // 拿到要显示的图片路径
    public void load(String path, Context context) {
        this.path = path;
        this.context = context;
        this.key = new Key(path).getKey();
    }

    // 加载资源
    public void into(ImageView imageView) {
        this.imageView = imageView;
        Tool.checkNotNull(imageView);
        // 加载资源在主线程
        Tool.assertMainThread();

        // TODO 加载资源 --》 缓存 ---》网络/SD/ 加载资源 成功后 --》资源 保存到缓存中 >>>
        Value value = checkAction();
        if (value != null) {
            // 使用完成（不使用） 就 减一
            value.nonUseAction();
            imageView.setImageBitmap(value.getBitmap());
        }
    }

    // TODO 加载资源 --》 缓存 ---》网络/SD/ 加载资源 成功后 --》资源 保存到缓存中 >>>
    private Value checkAction() {
        Value value = null;
        // TODO 第一步，判断活动缓存是否有资源，如果有资源 就返回， 否则就继续往下找
        if (activeCache != null) {
            value = activeCache.get(key);
            if (value != null) {
                // 使用一次就加1
                value.useAction();
                Log.d(TAG, "cacheAction: 本次加载是在(活动缓存)中获取的资源>>>");
                return value;
            }
        }

        // TODO 第二步，从内存缓存中去找，如果找到了，内存缓存中的元素 “移动” 到 活动缓存， 然后再返回
        if (memoryCache != null) {
            value = memoryCache.get(key);
            if (value != null) {
                // 内存缓存 手动移除
                memoryCache.shoundonRemove(key);
                // 把内存缓存中的元素 加入到活动缓存中
                activeCache.put(key, value);
                // 使用一次就加1
                value.useAction();
                Log.d(TAG, "cacheAction: 本次加载是在(内存缓存)中获取的资源>>>");
                return value;
            }
        }

        // TODO 第三步，从磁盘缓存中去找，如果找到了，把磁盘缓存中的元素 加入到 活动缓存中
        if (diskLruCache != null) {
            value = diskLruCache.get(key, lruBitmapPool);
            if (value != null) {
                // 把磁盘缓存中的元素 加入到活动缓存中
                activeCache.put(key, value);
                // 使用一次就加1
                value.useAction();
                Log.d(TAG, "cacheAction: 本次加载是在(磁盘缓存)中获取的资源>>>");
                return value;
            }
        }

        // TODO 第四步，真正的去加载外部资源了， 去网络上加载/去SD本地上加载
        value = new LoadDataManager().loadResource(path, context, this);
        return value;
    }

    /**
     * 保存到缓存中
     *
     * @param key
     * @param value
     */
    private void saveCahce(String key, Value value) {
        Log.d(TAG, "saveCahce: >>>>>>>>>>>>>>>>>>>>>> 加载外部资源成功后，保存到缓存中 key:" + key + " value:" + value);
        value.setKey(key);
        if (diskLruCache != null) {
            // 保存到磁盘缓存中
            diskLruCache.put(key, value);
        }
    }

    @Override
    public void valueNonUseListener(String key, Value value) {
        // 活动缓存 不使用的时候 加入内存缓存
        if (!TextUtils.isEmpty(key) && value != null && memoryCache != null) {
            memoryCache.put(key, value);
        }
    }

    /**
     * 内存缓存发出的
     * Lru最少使用的元素会被移除
     *
     * @param key
     * @param oldValue
     */
    @Override
    public void entryRemovedMemoryCache(String key, Value oldValue) {
        // 加入复用池
        if (!TextUtils.isEmpty(key) && oldValue != null && lruBitmapPool != null) {
            lruBitmapPool.put(oldValue.getBitmap());
        }
    }

    // 生命周期回调
    @Override
    public void glideInitAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经开启了 初始化了....");
    }

    // 生命周期回调
    @Override
    public void glideStopAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 已经停止中 ....");
    }

    // 生命周期回调
    @Override
    public void glideRecycleAction() {
        Log.d(TAG, "glideInitAction: Glide生命周期之 进行释放操作 缓存策略释放操作等 >>>>>> ....");
        if (activeCache != null) {
            activeCache.closeThread();
        }
    }

    // 网络请求加载成功
    @Override
    public void responseSuccess(Value value) {
        if (value != null) {
            saveCahce(key, value);
            imageView.setImageBitmap(value.getBitmap());
        }
    }

    // 网络请求加载失败
    @Override
    public void responseException(Exception e) {
        Log.d(TAG, "responseException: e " + e.getMessage());
    }
}
