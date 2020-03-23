package com.custom.glide.test;

import com.custom.glide.utils.Tool;

/**
 * Created by A35 on 2020/3/23
 * Email:yin13753884368@163.com
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 */
public class Test {
    public static void main(String[] args) {
//        String ss ="https://cn.bing.com/sa/simg/hpb/LaDigue_EN-CA1115245085_1920x1080.jpg";
        String ss ="http://img.mp.sohu.com/q_mini,c_zoom,w_640/upload/20170731/4c79a1758a3a4c0c92c26f8e21dbd888_th.jpg";

        String ss1 = Tool.sha256BytesToHex(ss.getBytes());
        System.out.println(ss1);

        String ss2 = Tool.getSHA256StrJava(ss);
        System.out.println(ss2);
    }
}
