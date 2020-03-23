package com.custom.glide.load_data;

import com.custom.glide.model.Value;

/**
 * 网络加载回调
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public interface ResponseListener {
    public void responseSuccess(Value value);

    public void responseException(Exception e);

}
