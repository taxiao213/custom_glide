package com.custom.glide.fragment;

/**
 * 管理生命周期
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public interface LifecycleCallback {
    // 生命周期初始化了
    public void glideInitAction();

    // 生命周期 停止了
    public void glideStopAction();

    // 生命周期 释放 操作了
    public void glideRecycleAction();
}
