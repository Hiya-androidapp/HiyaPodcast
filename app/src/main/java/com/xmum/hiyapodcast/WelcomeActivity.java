package com.xmum.hiyapodcast;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import java.util.Timer;
import java.util.TimerTask;

public class WelcomeActivity extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        startMainActivity();
    }

    private void startMainActivity(){

        TimerTask delayTask = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(WelcomeActivity.this,MainActivity.class);
                startActivity(mainIntent);
                WelcomeActivity.this.finish();
            }
        };
        Timer timer = new Timer();
        timer.schedule(delayTask,1000);//延时两秒执行 run 里面的操作
    }
}
