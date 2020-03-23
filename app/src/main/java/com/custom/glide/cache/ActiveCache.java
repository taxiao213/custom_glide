package com.custom.glide.cache;

import com.custom.glide.model.Value;
import com.custom.glide.model.ValueCallback;
import com.custom.glide.utils.Tool;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 活动缓存
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class ActiveCache {
    // 容器存储
    private HashMap<String, WeakReference<Value>> map = new HashMap<>();
    // 回调
    private ValueCallback valueCallback;
    // 目的：为了监听这个弱引用 是否被回收了
    private ReferenceQueue<Value> queue;
    // 是否关闭线程
    private boolean isCloseThread;
    // 是否手动移除
    private boolean isShoudonRemove;

    private Thread thread;

    public ActiveCache(ValueCallback valueCallback) {
        this.valueCallback = valueCallback;
    }

    /**
     * TODO 获取Value
     *
     * @param key
     * @return
     */
    public Value get(String key) {
        WeakReference<Value> valueWeakReference = map.get(key);
        if (valueWeakReference != null) {
            return valueWeakReference.get();
        }
        return null;
    }

    /**
     * TODO 添加 活动缓存
     *
     * @param key
     * @return
     */
    public void put(String key, Value value) {
        Tool.checkNotEmpty(key);
        // 绑定监听  （Value没有被使用了，就会发起这个监听，给外界业务需要来使用）
        value.setValueCallback(valueCallback);
        // 存储
        map.put(key, new CustomWeakReference(value, getQueue(), key));
    }

    /**
     * TODO 移除 活动缓存
     *
     * @param key
     * @return
     */
    public Value remove(String key) {
        isShoudonRemove = true;
        WeakReference<Value> remove = map.remove(key);
        //  还原 目的是为了 让 GC自动移除 继续工作
        isShoudonRemove = false;
        if (remove != null) {
            return remove.get();
        }
        return null;
    }

    /**
     * TODO 释放 关闭线程
     */
    public void closeThread() {
        isCloseThread = true;
        if (thread != null) {
            thread.interrupt(); // 中断线程
            try {
                thread.join(TimeUnit.SECONDS.toMillis(5)); // 线程稳定安全 停止下来
                if (thread.isAlive()) { // 证明线程还是没有结束
                    throw new IllegalStateException("活动缓存中 关闭线程 线程没有停止下来...");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        map.clear();
        System.gc();
    }

    /**
     * 监听弱引用 成为弱引用的子类  为什么要成为弱引用的子类（目的：为了监听这个弱引用 是否被回收了）
     */
    public class CustomWeakReference extends WeakReference {
        public String key;

        public CustomWeakReference(Object referent, ReferenceQueue queue, String key) {
            super(referent, queue);
            this.key = key;
        }
    }

    /**
     * 目的：为了监听这个弱引用 是否被回收了
     * queue.remove() 会阻塞线程
     *
     * @return
     */
    public ReferenceQueue<Value> getQueue() {
        if (queue == null) {
            queue = new ReferenceQueue<>();
            // 监听这个弱引用 是否被回收了
            thread = new Thread() {
                @Override
                public void run() {
                    if (!isCloseThread) {
                        while (true) {
                            try {
                                // 阻塞式的方法,被动调用queue.remove()，进行回收
                                Reference<? extends Value> remove = queue.remove();
                                CustomWeakReference customWeakReference = (CustomWeakReference) remove;
                                if (map != null && !map.isEmpty()) {
                                    // 移除容器 !isShoudonRemove：为了区分手动移除 和 被动移除
                                    map.remove(customWeakReference.key);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            thread.start();
        }
        return queue;
    }

}
