package com.example.dicationary;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/*
作者：蒋文云

主要用到的技术：
       1.sql数据库(实现 增、删、改、查)
       2.volley实现一言获取
       3.Glide加载背景图片,swiperefreshlayout下拉刷新
       4.双适配器，实现布局切换
       5.查询，采取的是Filter过滤器

       差不多就是这些技术，当然那些小功能和细节处理，就不说了.

       注，目前版本为：云签1.0
        该项目还有全新的布局和更多功能，就在下一次作业：云签2.0
 */


//logo界面,像微信和QQ每次都会有这个样的logo界面
public class LogoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.activity_logo);

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        //我们一般都是用Thread 或 AsyncTask ，这次就使用另一种TimerTask,实现记时操作
        Timer timer=new Timer();
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                startActivity(new Intent(LogoActivity.this, MainActivity.class));
                finish();
            }
        };timer.schedule(timerTask,2000);
    }
}