package com.quang.minh.nhanhnhuchop.main;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.quang.minh.nhanhnhuchop.R;

public class Rules extends AppCompatActivity implements View.OnClickListener {
    private Button btQuayLai, btBatDau;
    MediaPlayer thele_mp3;
    ImageView img_clock;
    CountDownTimer countDownTimer;
    Dialog dialog;
    int i = 1 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);
        dialog();
        btBatDau.setOnClickListener(this);
        thele_mp3 = MediaPlayer.create(Rules.this, R.raw.the_le);
        btQuayLai.setOnClickListener(this);
        if(Home.check_nhac_nen==1) {
            thele_mp3.start();
        }
    }

    public void dialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_rules);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        btBatDau = (Button) dialog.findViewById(R.id.bt_bat_dau);
        btQuayLai = (Button) dialog.findViewById(R.id.bt_quay_lai);
        //img_clock = (ImageView) dialog.findViewById(R.id.img_clock);
        //img_clock.setVisibility(View.INVISIBLE);
        dialog.show();
//        countDownTimer = new CountDownTimer(3500,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                img_clock.setVisibility(View.VISIBLE);
//                countDownTimer  = new CountDownTimer(2500,2500) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        countDownTimer = new CountDownTimer(7000,2000) {
//                            @Override
//                            public void onTick(long millisUntilFinished) {
//                                i++;
//                                img_clock.setImageLevel(i);
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                Intent it = new Intent(Rules.this, Play.class);
//                                startActivity(it);
//                                finish();
//                            }
//                        }.start();
//                    }
//                }.start();
//            }
//        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_bat_dau :{
                if(thele_mp3.isPlaying())
                    thele_mp3.stop();
                dialog.dismiss();
                Intent it = new Intent(Rules.this, Play.class);
                startActivity(it);
                //countDownTimer.cancel();
                finish();
                break;
            }
            case R.id.bt_quay_lai : {
                if(thele_mp3.isPlaying()==true)
                    thele_mp3.stop();
                dialog.dismiss();
                Intent it = new Intent(Rules.this, Home.class);
                startActivity(it);
                //countDownTimer.cancel();
                finish();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {

    }
}
