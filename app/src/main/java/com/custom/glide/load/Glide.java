package com.custom.glide.load;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;


/**
 * 缓存加载类
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Glide {
    private static volatile Glide glide;
    public static Context mContext;
    private RequestManagerRetriever requestManagerRetriever;

    public Glide(RequestManagerRetriever requestManagerRetriever) {
        this.requestManagerRetriever = requestManagerRetriever;
    }

    @NonNull
    public static RequestManager with(@NonNull Context context) {
        return getRetriever(context).get(context);
    }

    /**
     * Begin a load with Glide that will be tied to the given {@link android.app.Activity}'s lifecycle
     * and that uses the given {@link Activity}'s default options.
     *
     * @param activity The activity to use.
     * @return A RequestManager for the given activity that can be used to start a load.
     */
    @NonNull
    public static RequestManager with(@NonNull Activity activity) {
        return getRetriever(activity).get(activity);
    }

    /**
     * Begin a load with Glide that will tied to the give
     * {@link android.support.v4.app.FragmentActivity}'s lifecycle and that uses the given
     * {@link android.support.v4.app.FragmentActivity}'s default options.
     *
     * @param activity The activity to use.
     * @return A RequestManager for the given FragmentActivity that can be used to start a load.
     */
    @NonNull
    public static RequestManager with(@NonNull FragmentActivity activity) {
        return getRetriever(activity).get(activity);
    }

    @NonNull
    private static RequestManagerRetriever getRetriever(@Nullable Context context) {
        mContext = context;
        return Glide.get(context).getRequestManagerRetriever();
    }

    /**
     * Get the singleton.
     * Glide 是 new出来的 -- 转变
     *
     * @return the singleton
     */
    @NonNull
    public static Glide get(@NonNull Context context) {
        if (glide == null) {
            synchronized (Glide.class) {
                if (glide == null) {
                    glide = new Glide(new GlideBuild().build());
                }
            }
        }
        return glide;
    }

    /**
     * Internal method.
     */
    @NonNull
    public RequestManagerRetriever getRequestManagerRetriever() {
        return requestManagerRetriever;
    }
}
