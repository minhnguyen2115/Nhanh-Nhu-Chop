package com.quang.minh.nhanhnhuchop.main;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.quang.minh.nhanhnhuchop.R;

public class Logo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        final Intent it = new Intent(Logo.this, Home.class);
        CountDownTimer cd = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startActivity(it);
                overridePendingTransition(R.anim.translate_enter, R.anim.translate_exit);
            }
        };
        cd.start();


    }
}
