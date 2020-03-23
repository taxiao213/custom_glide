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
 * 管理生命周期 通过加载一个Fragment实现
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class RequestManager {
    private final String TAG = getClass().getSimpleName();
    private final String FRAGMENT_ACTIVITY_NAME = "Fragment_Activity_NAME";
    private final int NEXT_HANDLER_MSG = 995465;
    private Context requestManagerContext;
    FragmentActivity fragmentActivity;
    private RequestTargetEngine requestTargetEngine;

    {
        initRequestTargetEngine();
    }

    // 无生命周期  因为Application无法管理
    public RequestManager(Context context) {
        this.requestManagerContext = context;

    }

    // 有生命周期
    public RequestManager(FragmentActivity fragmentActivity) {
        this.requestManagerContext = fragmentActivity;
        this.fragmentActivity = fragmentActivity;
//        initRequestTargetEngine();
        FragmentManager supportFragmentManager = fragmentActivity.getSupportFragmentManager();
        Fragment fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (fragment == null) {
            fragment = new SupportRequestManagerFragment();
            // 添加到 supportFragmentManager
            supportFragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }
        // 发送一次Handler
        // 我们的Android基于Handler消息的，LAUNCH_ACTIVITY，为了让我们的fragment，不要再排队中，为了下次可以取出来
        // TODO: 2020/3/23 值是不是随意添加就行
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);

        // TODO 测试
        Fragment fragment2 = supportFragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        Log.d(TAG, "RequestManager: fragment2" + fragment2); // null ： @3 还在排队中，还没有消费
    }

    // 有生命周期
    public RequestManager(Activity activity) {
        this.requestManagerContext = activity;
//        initRequestTargetEngine();
        android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        android.app.Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
        if (fragment == null) {
            fragment = new RequestManagerFragment();
            // 添加到 supportFragmentManager
            fragmentManager.beginTransaction().add(fragment, FRAGMENT_ACTIVITY_NAME).commitAllowingStateLoss();
        }
        // 发送一次Handler
        // 我们的Android基于Handler消息的，LAUNCH_ACTIVITY，为了让我们的fragment，不要再排队中，为了下次可以取出来
        mHandler.sendEmptyMessage(NEXT_HANDLER_MSG);
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            // TODO 测试
            Fragment fragment2 = fragmentActivity.getSupportFragmentManager().findFragmentByTag(FRAGMENT_ACTIVITY_NAME);
            Log.d(TAG, "Handler: fragment2" + fragment2); // 有值 ： 不在排队中，所以有值
            return false;
        }
    });

    private void initRequestTargetEngine() {
        if (requestTargetEngine == null) {
            requestTargetEngine = new RequestTargetEngine();
        }
    }

    // 拿到要显示的图片路径
    public RequestTargetEngine load(String path) {
        // 移除Handler
        mHandler.removeMessages(NEXT_HANDLER_MSG);
        // 加载资源
        requestTargetEngine.load(path, requestManagerContext);
        return requestTargetEngine;
    }
}
