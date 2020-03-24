package com.custom.glide;


import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.custom.glide.load.Glide;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private ImageView iv;
    //    private String path = "http://img.mp.sohu.com/q_mini,c_zoom,w_640/upload/20170731/4c79a1758a3a4c0c92c26f8e21dbd888_th.jpg";
    private String path = Environment.getExternalStorageDirectory() + File.separator + "22222.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View btLoad = findViewById(R.id.bt_load);
        View btTest = findViewById(R.id.bt_test);
        iv = findViewById(R.id.iv);
        btLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(MainActivity.this)
                        .load(path)
                        .into(iv);
            }
        });
    }
}
