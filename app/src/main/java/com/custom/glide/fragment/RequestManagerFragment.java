package com.custom.glide.fragment;


import android.annotation.SuppressLint;
import android.app.Fragment;

import com.custom.glide.load.RequestTargetEngine;

/**
 * 管理生命周期
 * A view-less {@link android.app.Fragment}
 * Activity fragment(android.app.Fragment)
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
@SuppressLint("ValidFragment")
public class RequestManagerFragment extends Fragment {
    private LifecycleCallback lifecycleCallback;

    @SuppressLint("ValidFragment")
    public RequestManagerFragment(LifecycleCallback lifecycleCallback) {
        this.lifecycleCallback = lifecycleCallback;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideInitAction();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideStopAction();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lifecycleCallback != null) {
            lifecycleCallback.glideRecycleAction();
        }
    }
}
