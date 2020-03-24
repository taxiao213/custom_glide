package com.custom.glide.load;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.custom.glide.fragment.RequestManagerFragment;
import com.custom.glide.fragment.SupportRequestManagerFragment;

/**
 * 管理生命周期 通过加载Fragment实现
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class RequestManager {
    private final String TAG = getClass().getSimpleName();
    private final String FRAGMENT_ACTIVITY_NAME = "Fragment_Activity_NAME";
    private final int NEXT_HANDLER_MSG = 213;
    private Context requestManagerContext;
    FragmentActivity fragmentActivity;
    private static RequestTargetEngine requestTargetEngine;

    // 无生命周期  因为Application无法管理
    public RequestManager(Context context) {
        this.requestManagerContext = context;
        initRequestTargetEngine();
    }

    // 有生命周期
    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        this.fragmentActivity = fragmentActivity;
        initRequestTargetEngine();
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (fragment == null) {
            // Fragment的生命周期与requestTargetEngine关联起来了
            fragment = new SupportRequestManagerFragment(requestTargetEngine);
            // 添加到 supportFragmentManager fragmentManager.beginTransaction().add.. Handler
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }
        // 发送一次Handler
        // 我们的Android基于Handler消息的，LAUNCH_ACTIVITY，为了让我们的fragment，不要再排队中，为了下次可以取出来
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
        Log.d(TAG, "RequestManager: FragmentActivity" + fragment);
    }

    // 有生命周期
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;
        initRequestTargetEngine();
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (fragment == null) {
            // Fragment的生命周期与requestTargetEngine关联起来了
            fragment = new RequestManagerFragment(requestTargetEngine);
            // 添加到 supportFragmentManager fragmentManager.beginTransaction().add.. Handler
            fragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }
        // 发送一次Handler
        // 我们的Android基于Handler消息的，LAUNCH_ACTIVITY，为了让我们的fragment，不要再排队中，为了下次可以取出来
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
        Log.d(TAG, "RequestManager: Activity" + fragment);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // Fragment fragment2 = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
            // Log.d(TAG, "Handler: fragment2" + fragment2); // 有值 ： 不在排队中，所以有值
            return false;
        }
    });

    private void initRequestTargetEngine() {
        if (requestTargetEngine == null) {
            requestTargetEngine = new RequestTargetEngine(requestManagerContext);
        }
        Log.d(TAG, "initRequestTargetEngine: requestTargetEngine " + requestTargetEngine);
    }

    // 拿到要显示的图片路径
    public RequestTargetEngine load(String path) {
        // load 时移除Handler
        mHandler.removeMessages(NEXT_HANDLER_MSG);
        // 加载资源
        requestTargetEngine.load(path);
        return requestTargetEngine;
    }
}
