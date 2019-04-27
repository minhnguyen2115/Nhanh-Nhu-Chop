package com.quang.minh.nhanhnhuchop.main;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.quang.minh.nhanhnhuchop.R;
import com.quang.minh.nhanhnhuchop.database.database;
import com.quang.minh.nhanhnhuchop.model.player;
import com.quang.minh.nhanhnhuchop.model.player_adapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Home extends AppCompatActivity {
    private ProfilePictureView prof;
    private LoginButton loginButton;
    private TextView tv_name_acc_facebook, tv_all;
    private Button btChoiNgay;
    private player_adapter adapter;
    private ArrayList<player> player_list;
    private String url = "http://192.168.1.4:8080/nhanhNhuChop/getPlayer.php";
    public static MediaPlayer home_mp3;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static database database;
    String name = "";
    String id = "" ;
    int login = 0;
    int insert_data = 0;
    public static int check_am_thanh = 1 , check_nhac_nen = 1;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init();
        set_visible(View.INVISIBLE);
        find_hashkey();
        setSharedPreferences();
        database = new database(this, "Note.sqlite",null,1);
        Log.d("login", login+"");
//        Log.d("insert", insert_data+"");
        setInsert_data();
        set_Login_Logout_Button();

        //database.queryData("DROP TABLE Account");

        home_mp3 = MediaPlayer.create(this, R.raw.home);
        if(check_nhac_nen == 1){
            home_mp3.start();
            home_mp3.setLooping(true);
        }
    }
    public void init(){
        btChoiNgay = (Button) findViewById(R.id.bt_choi_ngay);
        prof = (ProfilePictureView) findViewById(R.id.profile_picture);
        loginButton = (LoginButton) findViewById(R.id.login_button_facebook);
        tv_name_acc_facebook = (TextView) findViewById(R.id.tv_name_acc_facebook);
        tv_all = (TextView) findViewById(R.id.tv_all);
    }

    public void set_visible(int visible){
        tv_all.setVisibility(visible);
        tv_name_acc_facebook.setVisibility(visible);
        prof.setVisibility(visible);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void set_login_button(){
        callbackManager = CallbackManager.Factory.create();
        //        loginButton.setPublishPermissions(Arrays.asList("public_picture","email"));
        loginButton.setReadPermissions(Arrays.asList("public_profile, email"));
        database.queryData("CREATE TABLE IF NOT EXISTS Account(Id VARCHAR(200) PRIMARY KEY, Name VARCHAR(200))");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                set_visible(View.VISIBLE);

                GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("JSON", response.getJSONObject().toString());
                        try {
                            name = object.getString("name");
                            id = object.getString("id");
                            prof.setProfileId(object.getString("id"));
                            tv_name_acc_facebook.setText(name);
                            //Bitmap bmp = BitmapFactory.decodeResource(getResources(), prof);
//                            BitmapDrawable bitmapDrawable = (BitmapDrawable) prof.getBackground();
//                            Bitmap bitmap = bitmapDrawable.getBitmap();
//                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                            byte[] profile = byteArrayOutputStream.toByteArray();

                            database.queryData("INSERT INTO Account VALUES('"+id+"','"+name+"')");
                            login = 1;
                            editor = sharedPreferences.edit();
                            editor.putInt("login", login);
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameter = new Bundle();
                parameter.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameter);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }


//    @Override
//    protected void onStart() {
//        LoginManager.getInstance().logOut();
//        LoginManager.getInstance().getAuthType();
//        database.queryData("DELETE FROM Account");
//        super.onStart();
//    }

    public void choi_ngay(View view){
        btChoiNgay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(home_mp3.isPlaying())
                    home_mp3.stop();
                Intent it = new Intent(Home.this, Rules.class);
                startActivity(it);
            }
        });
    }

    public void cai_dat(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_cai_dat);
        dialog.show();
        TextView tv_alarm = (TextView) dialog.findViewById(R.id.tv_alarm);
        ImageView img_close = (ImageView) dialog.findViewById(R.id.close);
        CheckBox cb_nhac_nen = (CheckBox) dialog.findViewById(R.id.cb_nhac_nen);
        CheckBox cb_am_thanh = (CheckBox) dialog.findViewById(R.id.cb_am_thanh);
        Spinner spinner_alarm = (Spinner) dialog.findViewById(R.id.spinner_alarm);
        Spinner spinner_repeat = (Spinner) dialog.findViewById(R.id.spinner_repeat);
        cb_nhac_nen.setChecked(sharedPreferences.getBoolean("check_nhac_nen", true));
        cb_am_thanh.setChecked(sharedPreferences.getBoolean("check_am_thanh", true));

        ArrayList<String> array_alarm = new ArrayList<>();
        ArrayList<String> array_repeat = new ArrayList<>();

        array_alarm.add("10:00 AM");
        array_alarm.add("11:00 AM");
        array_alarm.add("10:00 PM");
        array_alarm.add("11:00 PM");

        array_repeat.add("1 ngay");
        array_repeat.add("2 ngay");
        array_repeat.add("3 ngay");
        ArrayAdapter adapter_alarm = new ArrayAdapter(Home.this, android.R.layout.simple_spinner_dropdown_item, array_alarm);
        ArrayAdapter adapter_repeat = new ArrayAdapter(Home.this, android.R.layout.simple_spinner_dropdown_item, array_repeat);

        spinner_alarm.setAdapter(adapter_alarm);
        //adapter_alarm.setDropDownViewResource();
        spinner_alarm.setDropDownWidth(100);
        spinner_repeat.setAdapter(adapter_repeat);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        cb_am_thanh.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    check_am_thanh = 1;
                    editor = sharedPreferences.edit();
                    editor.putInt("id_am_thanh", check_am_thanh);
                    editor.putBoolean("check_am_thanh", true);
                    editor.commit();
                }
                else {
                    check_am_thanh = 0;
                    editor = sharedPreferences.edit();
                    editor.putInt("id_am_thanh", check_am_thanh);
                    editor.putBoolean("check_am_thanh", false);
                    editor.commit();
                }
            }
        });

        cb_nhac_nen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    check_nhac_nen =1;
                    home_mp3 = MediaPlayer.create(Home.this, R.raw.home);
                    home_mp3.start();
                    editor = sharedPreferences.edit();
                    editor.putInt("id_nhac_nen", check_nhac_nen);
                    editor.putBoolean("check_nhac_nen", true);
                    editor.commit();
                }
                else {
                    check_nhac_nen = 0;
                    if(home_mp3.isPlaying())
                        home_mp3.stop();
                    editor = sharedPreferences.edit();
                    editor.putInt("id_nhac_nen", check_nhac_nen);
                    editor.putBoolean("check_nhac_nen", false);
                    editor.commit();
                }
            }
        });
    }

    public void diem_cao(View view) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_diemcao);
        dialog.show();
        ImageView img_close = (ImageView) dialog.findViewById(R.id.close);
        ListView listView = (ListView) dialog.findViewById(R.id.listView);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        player_list = new ArrayList<>();
        adapter = new player_adapter(this, R.layout.player_line, player_list);
        listView.setAdapter(adapter);
////        Cursor cursor = database.getData("SELECT * FROM Account");
////        while(cursor.moveToNext()){
////            player_list.add(new player(1, cursor.getString(0), cursor.getString(1), 100));
////        }
        get_high_score(url);
        Log.d("size array", player_list.size()+"");
    }

    public void get_high_score(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i = 0; i< response.length(); i++){
                    try {
                        JSONObject jo = response.getJSONObject(i);
                        player_list.add(new player( i+1 , jo.getString("ID"), jo.getString("NAME"),
                                jo.getInt("SCORE")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    @Override
    public void onBackPressed() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_exit);
        dialog.setCanceledOnTouchOutside(false);
        Button btThoat = (Button) dialog.findViewById(R.id.bt_thoat);
        Button btQuayLai = (Button) dialog.findViewById(R.id.bt_quay_lai);
        TextView tvAll = (TextView) dialog.findViewById(R.id.tvAll);
        tvAll.setText("Bạn muốn thoát trò chơi?");
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
                finish();
                System.exit(0);
            }
        });
    }

    public void find_hashkey(){
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo("com.quang.minh.nhanhnhuchop", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }

    public void setSharedPreferences(){
        sharedPreferences = getSharedPreferences("note", MODE_PRIVATE);
        check_am_thanh = sharedPreferences.getInt("id_am_thanh",check_am_thanh);
        check_nhac_nen = sharedPreferences.getInt("id_nhac_nen",check_nhac_nen);
        login = sharedPreferences.getInt("login", login);
        insert_data = sharedPreferences.getInt("insert_data", insert_data);
    }

    public void set_Login_Logout_Button(){
        if(login == 0)
            set_login_button();
        else {
            set_visible(View.VISIBLE);
            Cursor cursor = database.getData("SELECT * FROM Account");
            while (cursor.moveToNext()) {
                id = cursor.getString(0);
                name = cursor.getString(1);
                prof.setProfileId(id);
                tv_name_acc_facebook.setText(name);
            }
        }
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                set_visible(View.INVISIBLE);
                database.queryData("DELETE FROM Account");
                login = 0;
                editor = sharedPreferences.edit();
                editor.putInt("login", login);
                editor.commit();
            }
        });
    }

    public void setInsert_data(){
        if(insert_data==0){
            database.queryData("CREATE TABLE IF NOT EXISTS Question(Id INT(11), question VARCHAR(200)" +
                    ", answer1 VARCHAR(200), answer2 VARCHAR(200), answer3 VARCHAR(200), answer4 VARCHAR(200)," +
                    "result INT(11), hint VARCHAR(200))");
            database.queryData("INSERT INTO Question VALUES(13, 'Sấm và sét, ta nhận biết điều nào trước?', 'Sấm', 'Sét', 'Cả 2', 'Không biết', 2, 'Vận tốc ánh sáng nhanh hơn vận tốc âm thanh')");
            database.queryData("INSERT INTO Question VALUES(14, 'Có một người leo lên núi cao, ông rớt xuống cái bịch hỏi ông có chết không?', 'Có chết', 'Không chết', 'Gãy chân', 'Bể đít', 2, 'Ông rớt cái bịch')");
            database.queryData("INSERT INTO Question VALUES(15, 'Đi thì đứng, đứng thì ngã là gì?', 'Xe đạp', 'Bàn chân', 'Em bé tập đi', 'Không biết', 1, 'Đoán xem')");
            database.queryData("INSERT INTO Question VALUES(16, 'Một thằng đổi tên thì nó tên là gì?', 'Long', 'Nam', 'Quân', 'Hải', 4, 'Thằng đổi => đồi thẳng => đồi không cong => còng không đôi => còng không hai => hài không cong => hài thẳng => thằng hải =)))')");
            database.queryData("INSERT INTO Question VALUES(17, 'Giơ tay chữ v là số mấy?', '5', '4', '3', '2', 1, 'Số la mã')");
            database.queryData("INSERT INTO Question VALUES(18, 'Có ông già lên núi ổng lấy bèo thấy một con cò gầy xơ xác, tại sao ổng về?', 'Ổng mắc ị', 'Ổng có việc bận', 'Ổng sợ con cò', 'Không có bèo', 4, 'Cò gầy => cò không béo')");
            database.queryData("INSERT INTO Question VALUES(19, 'Trên chiếc đồng hồ bằng đồng có bao nhiêu loại kim?', '2', '3', '4', '5', 3, 'Khim giờ, kim phút, kim giây, kim loại')");
            database.queryData("INSERT INTO Question VALUES(20, 'Con chó và con mèo con nào thông minh hơn?', 'Con chó', 'Con mèo', '2 con ngu như nhau', 'Không biết', 1, 'Ngu như... :))')");
            database.queryData("INSERT INTO Question VALUES(21, 'Có một bà đi chợ đi được 1 lúc bà thấy tấm bảng 130m. Hỏi bà thấy gì mà tức tốc chạy về?', 'Chó đuổi', 'Có bom', 'Đường còn ca quá', 'Có thằng nghiện', 2, 'Nhìn kỹ 130m')");
            database.queryData("INSERT INTO Question VALUES(22, 'Cầm gì càng lâu sẽ càng vững?', 'Cầm lòng', 'Cầm tiền', 'Cầm đồ', 'Cầm lái', 4, 'Đoán xem')");
            insert_data = 1;
            editor = sharedPreferences.edit();
            editor.putInt("insert_data", insert_data);
            editor.commit();
        }
//        else if(insert_data ==1){
//            database.queryData("DELETE FROM Question");
//            insert_data = 0;
//        }
    }
}
