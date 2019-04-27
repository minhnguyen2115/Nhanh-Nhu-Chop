package com.quang.minh.nhanhnhuchop.main;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.model.ShareVideo;
import com.facebook.share.model.ShareVideoContent;
import com.facebook.share.widget.ShareDialog;
import com.quang.minh.nhanhnhuchop.R;
import com.quang.minh.nhanhnhuchop.database.database;
import com.quang.minh.nhanhnhuchop.model.player;
import com.quang.minh.nhanhnhuchop.model.question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Play extends AppCompatActivity {
    private ImageButton imgbt_hint;
    private TextView tvDiem, tv_thoiGian, tvCauHoi, tvHint, tv_number_hint, tvCau_So, tvDapAn_1,
            tvDapAn_2, tvDapAn_3, tvDapAn_4;
    private Button bt_bo_qua;
    private ImageView img_1, img_2, img_3, img_4, img_5, img_6, img_7, img_8, img_9, img_10;
    private View screen_play;
    private String url_get = "http://192.168.1.4:8080/nhanhNhuChop/getData.php";
    private String url_post = "http://192.168.1.4:8080/nhanhNhuChop/insertData.php";
    ArrayList<question> questions_array;
    ArrayList<player> players_array;
    ArrayList<ImageView> img_array;
    ArrayList<TextView> tv_array;
    Adapter adapter;
    Dialog dialog_share;

    Bitmap bitmap_screen;
    ShareDialog shareDialog;

    int diem = 100;
    int time = 120;
    int true_case;
    int dap_an;
    int check = 0;
    int number_hint =5;
    CountDownTimer countDownTimer;
    MediaPlayer play_mp3, true_answer_mp3, wrong_answer_mp3, share_mp3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        init();
        setEnable(true);
        getData_from_database();
        if(isOnline()==true) {
            getData_from_server(url_get);
        }
        Collections.shuffle(questions_array);
        setData(0);
        Log.d("arr1", questions_array.size()+"");
        countDown();
    }

    public void init(){
        screen_play = (View) findViewById(R.id.screen_play);
        imgbt_hint = (ImageButton) findViewById(R.id.imgHint);
        bt_bo_qua = (Button) findViewById(R.id.bt_bo_qua);
        tvDiem = (TextView) findViewById(R.id.tvDiem);
        tv_thoiGian = (TextView) findViewById(R.id.tvThoiGian);
        tvCauHoi = (TextView) findViewById(R.id.tvCauHoi);
        tvHint = (TextView) findViewById(R.id.tvHint);
        tv_number_hint = (TextView) findViewById(R.id.number_hint);
        tvCau_So = (TextView) findViewById(R.id.tvCau_So);
        tvDapAn_1 = (TextView) findViewById(R.id.tvDapAn_1);
        tvDapAn_2 = (TextView) findViewById(R.id.tvDapAn_2);
        tvDapAn_3 = (TextView) findViewById(R.id.tvDapAn_3);
        tvDapAn_4 = (TextView) findViewById(R.id.tvDapAn_4);
        img_1 = (ImageView) findViewById(R.id.img_1);
        img_2 = (ImageView) findViewById(R.id.img_2);
        img_3 = (ImageView) findViewById(R.id.img_3);
        img_4 = (ImageView) findViewById(R.id.img_4);
        img_5 = (ImageView) findViewById(R.id.img_5);
        img_6 = (ImageView) findViewById(R.id.img_6);
        img_7 = (ImageView) findViewById(R.id.img_7);
        img_8 = (ImageView) findViewById(R.id.img_8);
        img_9 = (ImageView) findViewById(R.id.img_9);
        img_10 = (ImageView) findViewById(R.id.img_10);
        tvCau_So.setVisibility(View.VISIBLE);

        questions_array = new ArrayList<>();

        shareDialog = new ShareDialog(this);

        img_array = new ArrayList<>();
        img_array.add(img_1);
        img_array.add(img_2);
        img_array.add(img_3);
        img_array.add(img_4);
        img_array.add(img_5);
        img_array.add(img_6);
        img_array.add(img_7);
        img_array.add(img_8);
        img_array.add(img_9);
        img_array.add(img_10);

        tv_array = new ArrayList<>();
        tv_array.add(tvDapAn_1);
        tv_array.add(tvDapAn_2);
        tv_array.add(tvDapAn_3);
        tv_array.add(tvDapAn_4);

        play_mp3 = MediaPlayer.create(Play.this, R.raw.play);
        if(Home.check_nhac_nen==1){
            play_mp3.start();
            play_mp3.setLooping(true);
        }
    }

    private void getData_from_server(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i<response.length(); i++){
                    try {
                        JSONObject jo = response.getJSONObject(i);
                        questions_array.add(new question(jo.getInt("ID"), jo.getString("QUESTION"),
                                jo.getString("ANSWER1"), jo.getString("ANSWER2"), jo.getString("ANSWER3"),
                                jo.getString("ANSWER4"), jo.getInt("RESULT"), jo.getString("HINT")));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Collections.shuffle(questions_array);
//                setData(0);
//                Log.d("arr3", questions_array.size()+"");
            }
        },
            new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void postData(String url_post){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_post
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Cursor cursor = Home.database.getData("SELECT * FROM Account");
                while(cursor.moveToNext()){
                    params.put("id_player", cursor.getString(0));
                    params.put("name_player", cursor.getString(1));
                    params.put("score_player", diem+"");
                    Log.d("diem", cursor.getString(0));
                    Log.d("diem", cursor.getString(1));
                    Log.d("diem", diem+"");
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void getData_from_database(){
        Cursor cursor = Home.database.getData("SELECT * FROM Question");
        while(cursor.moveToNext()){
            questions_array.add(new question(cursor.getInt(0), cursor.getString(1),
                    cursor.getString(2), cursor.getString(3), cursor.getString(4),
                    cursor.getString(5), cursor.getInt(6), cursor.getString(7)));
            Collections.shuffle(questions_array);
        }
    }

    public void exit(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(false);
        Button btThoat = (Button) dialog.findViewById(R.id.bt_thoat);
        Button btQuayLai = (Button) dialog.findViewById(R.id.bt_quay_lai);
        dialog.show();
        btQuayLai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if(play_mp3.isPlaying())
                    play_mp3.stop();
                countDownTimer.cancel();
                finish();
                if(Home.check_nhac_nen==1){
                    Home.home_mp3 = MediaPlayer.create(Play.this, R.raw.home);
                    Home.home_mp3.start();
                    Home.home_mp3.setLooping(true);
                }
            }
        });
    }

    public void setData(final int i){
        if(i == img_array.size()){
            countDownTimer.cancel();
            if(play_mp3.isPlaying())
                play_mp3.stop();

            screen_play.setDrawingCacheEnabled(false);
            screen_play.setDrawingCacheEnabled(true);
            //screen_play.buildDrawingCache(true);

            bitmap_screen = screen_play.getDrawingCache();
            dialog_chia_se();
        }
        else {
            setEnable(true);
            tvCau_So.setText("Câu số " + (i + 1));
            tvCauHoi.setText(questions_array.get(i).getQuestion());
            tvDapAn_1.setText("A. " + questions_array.get(i).getAnswer1());
            tvDapAn_2.setText("B. " + questions_array.get(i).getAnswer2());
            tvDapAn_3.setText("C. " + questions_array.get(i).getAnswer3());
            tvDapAn_4.setText("D. " + questions_array.get(i).getAnswer4());
            dap_an = questions_array.get(i).getResult();
            switch (dap_an) {
                case 1:
                    true_case = R.id.tvDapAn_1;
                    break;
                case 2:
                    true_case = R.id.tvDapAn_2;
                    break;
                case 3:
                    true_case = R.id.tvDapAn_3;
                    break;
                case 4:
                    true_case = R.id.tvDapAn_4;
                    break;
            }
            imgbt_hint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(number_hint > 0) {
                        number_hint = number_hint - 1;
                        tv_number_hint.setText(number_hint+"");
                        tvHint.setText(questions_array.get(i).getHint());
                        imgbt_hint.setEnabled(false);
                    }
                    else {
                        imgbt_hint.setEnabled(false);
                    }
                }
            });
            bt_bo_qua.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    diem =diem - 5;
                    img_array.get(check).setBackgroundResource(R.drawable.exclamation);
                    check = check +1;
                    tvDiem.setText(diem+"");
                    tvHint.setText("");
                    imgbt_hint.setEnabled(true);
                    setData(i + 1);
                }
            });
        }
    }

    public void chon_dap_an(final View view) {
        if (view.getId() == true_case){
            tv_array.get(dap_an-1).setBackgroundResource(R.drawable.answer_true);
            if(Home.check_am_thanh==1){
                true_answer_mp3 = MediaPlayer.create(Play.this, R.raw.true_answer);
                true_answer_mp3.start();
            }
            setEnable(false);
            diem =diem + 10;
            tvDiem.setText(diem+"");
            img_array.get(check).setBackgroundResource(R.drawable.true_icon);
            //Toast.makeText(Play.this, "Chọn đúng", Toast.LENGTH_SHORT).show();
            check = check +1 ;
            CountDownTimer cdt = new CountDownTimer(2000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    tv_array.get(dap_an-1).setBackgroundResource(R.drawable.answer_shape);
                    tvHint.setText("");
                    imgbt_hint.setEnabled(true);
                    setData(check);
                }
            }.start();
        }
        else {
            switch (view.getId()) {
                case R.id.tvDapAn_1:
                    tvDapAn_1.setBackgroundResource(R.drawable.answer_false);
                    break;
                case R.id.tvDapAn_2:
                    tvDapAn_2.setBackgroundResource(R.drawable.answer_false);
                    break;
                case R.id.tvDapAn_3:
                    tvDapAn_3.setBackgroundResource(R.drawable.answer_false);
                    break;
                case R.id.tvDapAn_4:
                    tvDapAn_4.setBackgroundResource(R.drawable.answer_false);
                    break;
            }
            tv_array.get(dap_an-1).setBackgroundResource(R.drawable.answer_true);
            if(Home.check_am_thanh==1){
                wrong_answer_mp3 = MediaPlayer.create(Play.this, R.raw.wrong_answer);
                wrong_answer_mp3.start();
            }
            setEnable(false);
            diem = diem -10;
            tvDiem.setText(diem+"");
            img_array.get(check).setBackgroundResource(R.drawable.false_icon);
            //Toast.makeText(Play.this, "Chọn sai", Toast.LENGTH_SHORT).show();
            check = check +1 ;
            CountDownTimer cdt = new CountDownTimer(2000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    switch (view.getId()) {
                        case R.id.tvDapAn_1:
                            tvDapAn_1.setBackgroundResource(R.drawable.answer_shape);
                            break;
                        case R.id.tvDapAn_2:
                            tvDapAn_2.setBackgroundResource(R.drawable.answer_shape);
                            break;
                        case R.id.tvDapAn_3:
                            tvDapAn_3.setBackgroundResource(R.drawable.answer_shape);
                            break;
                        case R.id.tvDapAn_4:
                            tvDapAn_4.setBackgroundResource(R.drawable.answer_shape);
                            break;
                    }
                    tv_array.get(dap_an-1).setBackgroundResource(R.drawable.answer_shape);
                    tvHint.setText("");
                    imgbt_hint.setEnabled(true);
                    setData(check);
                }
            }.start();
        }
    }

    public void setEnable(Boolean tv){
        tvDapAn_1.setEnabled(tv);
        tvDapAn_2.setEnabled(tv);
        tvDapAn_3.setEnabled(tv);
        tvDapAn_4.setEnabled(tv);
        bt_bo_qua.setEnabled(tv);
    }

    public void countDown(){
        time = 120000;
        final SimpleDateFormat dinhDangGio = new SimpleDateFormat("mm:ss");
        tv_thoiGian.setText(dinhDangGio.format(time/1000));
        countDownTimer = new CountDownTimer(time,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                time = time - 1000;
                tv_thoiGian.setText(dinhDangGio.format(time));
            }
            @Override
            public void onFinish() {
                tv_thoiGian.setText(dinhDangGio.format(time-1000));
                if(play_mp3.isPlaying())
                    play_mp3.stop();
                dialog_chia_se();
            }
        }.start();
    }

    public void dialog_chia_se(){
        dialog_share = new Dialog(this);
        dialog_share.setContentView(R.layout.dialog_share);
        dialog_share.setCanceledOnTouchOutside(false);
        dialog_share.setCancelable(false);
        dialog_share.show();
        postData(url_post);
        share_mp3 = MediaPlayer.create(Play.this, R.raw.share);
        if(Home.check_nhac_nen==1){
            share_mp3.start();
            share_mp3.setLooping(true);
        }
        final TextView tv_diem_dialog = (TextView) dialog_share.findViewById(R.id.tv_diem);
        Button bt_trang_chu = (Button) dialog_share.findViewById(R.id.bt_trang_chu);
        Button bt_chia_se = (Button) dialog_share.findViewById(R.id.bt_chia_se);
        Button bt_choi_lai = (Button) dialog_share.findViewById(R.id.bt_choi_lai);
        tv_diem_dialog.setText(diem+"");
        bt_trang_chu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(share_mp3.isPlaying())
                    share_mp3.stop();
                dialog_share.dismiss();
                if(Home.check_nhac_nen==1) {
                    Home.home_mp3 = MediaPlayer.create(Play.this, R.raw.home);
                    Home.home_mp3.start();
                    Home.home_mp3.setLooping(true);
                }
                finish();
            }
        });
        bt_choi_lai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(share_mp3.isPlaying()) {
                    share_mp3.stop();
                }
                dialog_share.dismiss();
                if(Home.check_nhac_nen==1){
                    play_mp3 = MediaPlayer.create(Play.this, R.raw.play);
                    play_mp3.start();
                    play_mp3.setLooping(true);
                }
                diem = 100;
                tvDiem.setText(diem+"");
                tvHint.setText("");
                number_hint =5;
                tv_number_hint.setText(number_hint+"");
                int count;
                for (count = 0; count < check; count++) {
                    img_array.get(count).setBackgroundResource(0);
                }
                check = 0;
                countDown();
                Collections.shuffle(questions_array);
                setData(check);
            }
        });
        bt_chia_se.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharePhoto sharePhoto = new SharePhoto.Builder().setBitmap(bitmap_screen).build();
                SharePhotoContent sharePhotoContent = new SharePhotoContent.Builder().addPhoto(sharePhoto).build();
                shareDialog.show(sharePhotoContent);
            }
        });
    }

    @Override
    public void onBackPressed() {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_exit);
            dialog.setCanceledOnTouchOutside(false);
            Button btThoat = (Button) dialog.findViewById(R.id.bt_thoat);
            Button btQuayLai = (Button) dialog.findViewById(R.id.bt_quay_lai);
            dialog.show();
            btQuayLai.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            btThoat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    if(play_mp3.isPlaying())
                        play_mp3.stop();
                    countDownTimer.cancel();
                    finish();
                    if(Home.check_nhac_nen==1){
                        Home.home_mp3 = MediaPlayer.create(Play.this, R.raw.home);
                        Home.home_mp3.start();
                        Home.home_mp3.setLooping(true);
                    }
                }
            });
    }

    private Boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if(ni != null && ni.isConnected()) {
            return true;
        }
        return false;
    }

}
