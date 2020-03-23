package com.custom.glide.model;

import com.custom.glide.utils.Tool;

/**
 * 缓存存取的key
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Key {
    private String key;

    /**
     * 处理值：sha256（https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg）
     * {@link Tool#getSHA256StrJava}
     * 处理后：ac037ea49e34257dc5577d1796bb137dbaddc0e42a9dff051beee8ea457a4668
     * <p>
     * {@link Tool#sha256BytesToHex}
     * 处理后：68747470733a2f2f636e2e62696e672e636f6d2f73612f73696d672f6870622f4c6144696775655f454e2d4341313131353234353038355f3139323078313038302e6a7067
     *
     * @param key
     */
    public Key(String key) {
        this.key = Tool.getSHA256StrJava(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
