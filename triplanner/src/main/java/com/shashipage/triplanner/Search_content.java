package com.shashipage.triplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class Search_content extends AppCompatActivity implements View.OnClickListener {
    private TextView search_content_attraction_name,search_content_attraction_address;
    private String attraction_id,longitude,latitude,phone_number,tbd1;
    private ImageButton search_content_favorites,search_content_location,search_content_map;
    private ImageView circleImgView;
    private String img01;
    private Intent intent = new Intent();

    private MenuItem menu_addPlan,menu_addPlanMyStory,menu_logIn,menu_logOut,menu_GPS_ON,menu_GPS_OFF,menu_back,menu_end;
    private Toolbar toolbar;

    //======SQLite================
    private static final String DB_FILE = "astray.db";//資料庫名稱
    private static final String DB_TABLE = "favorite";//資料表名稱
    private static final int DBversion = DbHelper.VERSION;//版本
    private DbHelper dbHper;
    private String t_attraction_id,t_attraction_name,t_attraction_address,t_longitude,t_latitude,google_login_id;//名稱、地址、東經、北緯
    private String msg;
    private String t001,t002;
    //==================================
    private static final int RC_GET_TOKEN = 9002;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_content);
        initDB();//查詢資料庫是否有表，若無則新建一個表
        setupComponent();
    }
    //查詢資料庫是否有表，若無則新建一個表
    private void initDB() {
        if (dbHper == null) {
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
        }
    }

    private void setupComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        search_content_attraction_name=(TextView)findViewById(R.id.search_content_attraction_name);//景點名稱
        search_content_attraction_address=(TextView)findViewById(R.id.search_content_attraction_address);//景點地址
        circleImgView=(ImageView)findViewById(R.id.circleImgView_search_content);//景點照片
        search_content_favorites=(ImageButton)findViewById(R.id.search_content_favorites);//加入我的收藏
        search_content_location=(ImageButton)findViewById(R.id.search_content_location);//android地圖
        search_content_map=(ImageButton)findViewById(R.id.search_content_map);//跳轉到Google Map
        //監聽 search_content_favorites、search_content_map
        search_content_favorites.setOnClickListener(this);
        search_content_location.setOnClickListener(this);
        search_content_map.setOnClickListener(this);
        //接值(來自search值)
        Bundle bundle=this.getIntent().getExtras();
        attraction_id=bundle.getString("attraction_id");
        t001=bundle.getString("attraction_name");
        t002=bundle.getString("attraction_address");
        img01=bundle.getString("attraction_picture");
        search_content_attraction_name.setText(t001);
        search_content_attraction_address.setText(t002);
        //判斷是否有圖片
        if(img01.length()!=0){
//            Picasso.get().
//                    load(img01)
//                    .placeholder(R.drawable.astary_logo)
//                    .error(R.drawable.img_ns)
//                    .into(circleImgView);
            img01= utf8Togb2312(img01).replace("http://", "https://");
            Glide.with(getApplicationContext())
                    .load(img01)
                    .placeholder(R.drawable.logo_triplanner)
                    .error(R.drawable.logo_triplanner)
                    .into(circleImgView);
        }else{
            circleImgView.setImageResource(R.drawable.logo_triplanner);
        }
        longitude=bundle.getString("longitude");
        latitude=bundle.getString("latitude");
        phone_number=bundle.getString("phone_number");
        tbd1=bundle.getString("tbd1");
        //======查詢====
        t_attraction_id=attraction_id.trim();//trim頭尾空白剪掉
        if(t_attraction_id.length() !=0){
            String rec=dbHper.findRec01sc(t_attraction_id);
            if(rec !=null ){
                search_content_favorites.setImageResource(android.R.drawable.btn_star_big_on);
                search_content_favorites.setEnabled(false);//按鈕關閉
            }
        }
        //=============
    }
    private String utf8Togb2312(String img01) {
        String r_data = "";
        try {
            for (int i = 0; i < img01.length(); i++) {
                char ch_word = img01.charAt(i);
//            下面這段代碼的意義是:只對中文進行轉碼
                if (ch_word + "".getBytes().length > 1 && ch_word != ':' && ch_word != '/') {
                    r_data = r_data + java.net.URLEncoder.encode(ch_word + "", "utf-8");
                } else {
                    r_data = r_data + ch_word;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
//            System.out.println(r_data);
        }
        return r_data;
    }

    private void mysql_insert() {
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(google_login_id);
        nameValuePairs.add(t_attraction_id);
        nameValuePairs.add(t_attraction_name);
        nameValuePairs.add(t_attraction_address);
        nameValuePairs.add(t_longitude);
        nameValuePairs.add(t_latitude);
        nameValuePairs.add(img01);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //------------真正執行新增MySQL--------------------
        String result = DBConnector.executeFavoriteInsert(nameValuePairs);
        //-----------------------------------------------
    }

    //呼叫到別人的class
    @Override
    public void onClick(View v) {
        //........................................................................
        DbHelper userdb = new DbHelper(Search_content.this, "astray.db", null, DbHelper.VERSION);
        if(userdb.status11()){//有登入
            userdb.close();
            switch(v.getId()){
                case R.id.search_content_favorites:
                    google_login_id = dbHper.find11("google_login_id");
                    //==新增=======
                    t_attraction_id=attraction_id.trim();//trim頭尾空白剪掉
                    t_attraction_name=search_content_attraction_name.getText().toString().trim();//trim頭尾空白剪掉
                    t_attraction_address=search_content_attraction_address.getText().toString().trim();
                    t_longitude=longitude.trim();
                    t_latitude=latitude.trim();

                    Long rowID=dbHper.insertRec01sc(google_login_id,t_attraction_id,t_attraction_name,t_attraction_address,t_longitude,t_latitude,img01);
                    if(rowID !=-1){
                        msg="新增記錄成功，目前共有"+dbHper.recCount01sc()+"筆";
                        mysql_insert();
                    }
                    Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
                    //=====查詢==========
                    //attraction_id設為不空白，故用attraction_id查詢
                    if(t_attraction_id.length() !=0){
                        String rec=dbHper.findRec01sc(t_attraction_id);
                        if(rec !=null ){
                            search_content_favorites.setImageResource(android.R.drawable.btn_star_big_on);
                            search_content_favorites.setEnabled(false);//按鈕關閉
                        }
                    }
                    //==================
                    break;
                case R.id.search_content_location:
                    intent.setClass(Search_content.this, Search_map.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("longitude",longitude);//東經
                    bundle.putString("latitude",latitude);//北緯
                    bundle.putString("attraction_name",t001);//名稱
                    bundle.putString("attraction_address",t002);//地址
                    bundle.putString("attraction_picture",img01);//圖片
                    bundle.putString("phone_number",phone_number);//電話
                    bundle.putString("tbd1",tbd1);//縣市
//                    bundle.putString("attraction_name",t001);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.search_content_map:
//                "http://maps.google.com/maps?f=d&saddr=24.1702553493987, 120.60971242383611&daddr=24.152556932907594, 120.72936039609428&hl=tw"
                    String location="http://maps.google.com/maps?f=d&saddr=24.1702553493987, 120.60971242383611&daddr="+latitude+"," +longitude+"&hl=tw";
                    Uri uri = Uri.parse(location);
                    Intent it = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(it);
                    break;
            }

        }else{
            userdb.close();
            MyAlertDialog aldDial = new MyAlertDialog(Search_content.this);
            aldDial.setTitle("請先登入");
            aldDial.setMessage("此功能需登入後才能使用");
            aldDial.setIcon(R.drawable.logo_triplanner);
            aldDial.setCancelable(true); //返回鍵關閉
            aldDial.setButton(BUTTON_POSITIVE, "登入", aldBtListener);
            //  aldDial.setButton(BUTTON_NEGATIVE, "取消", aldBtListener);
            aldDial.show();
        }
    }


    //----------------------------------
    private void u_menu_sc1() {
        menu_addPlan.setVisible(false);
        menu_addPlanMyStory.setVisible(false);
//        menu_logIn.setVisible(false);
//        menu_logOut.setVisible(false);
        menu_GPS_ON.setVisible(false);
        menu_GPS_OFF.setVisible(false);
        menu_back.setVisible(true);
        menu_end.setVisible(false);
    }
    //關閉返回建
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
    //有menu的時候一定要加的系統方法
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu_addPlan=menu.findItem(R.id.menu_addPlan);
        menu_addPlanMyStory=menu.findItem(R.id.menu_addPlanMyStory);
//        menu_logIn=menu.findItem(R.id.menu_logIn);
//        menu_logOut=menu.findItem(R.id.menu_logOut);
        menu_GPS_ON=menu.findItem(R.id.menu_GPS_ON);
        menu_GPS_OFF=menu.findItem(R.id.menu_GPS_OFF);
        menu_back=menu.findItem(R.id.menu_back);
        menu_end=menu.findItem(R.id.menu_end);

        u_menu_sc1();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_back:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //.............................

    //登入後改變
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GET_TOKEN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Loginuser.userdata(account,this);
        } catch (ApiException e) {
            //   Log.w(TAG, "handleSignInResult:error", e);
        }
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        GoogleSignInClient mGoogleSignInClient = Loginuser.loginmod(this);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_TOKEN);

    }


    //.............................
    private DialogInterface.OnClickListener aldBtListener=new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {

            switch (which){
                case BUTTON_POSITIVE :
                    getIdToken();
                    break;
            }
        }
    };

}
