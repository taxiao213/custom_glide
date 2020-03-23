package com.custom.glide.load_data;

import android.content.Context;

import com.custom.glide.model.Value;

/**
 * 加载外部资源接口
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public interface ILoadData {
    // 加载外部资源的行为
    Value loadResource(String path, Context context, ResponseListener responseListener);
}
