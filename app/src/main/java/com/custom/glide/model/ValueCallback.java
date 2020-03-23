package com.custom.glide.model;

/**
 * 专门给Value，不再使用了，的回调接口
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public interface ValueCallback {

    /**
     * 监听的方法（Value不再使用了）
     *
     * @param key
     * @param value
     */
    public void valueNonUseListener(String key, Value value);
}
