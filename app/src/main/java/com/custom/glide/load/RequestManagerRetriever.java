package com.custom.glide.load;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

/**
 * 管理生命周期 通过加载一个Fragment实现
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class RequestManagerRetriever {

    @NonNull
    public RequestManager get(@NonNull Context context) {
        return new RequestManager(context);
    }

    @NonNull
    public RequestManager get(@NonNull FragmentActivity fragmentActivity) {
        return new RequestManager(fragmentActivity);
    }

    @NonNull
    public RequestManager get(@NonNull Activity activity) {
        return new RequestManager(activity);
    }
}
