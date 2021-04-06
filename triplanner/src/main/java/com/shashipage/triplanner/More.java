package com.shashipage.triplanner;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class More extends AppCompatActivity  {

    //申請權限後的返回碼
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;


    private Handler handler=new Handler();
    private Button  login_in_de;


    private TextView mStatusTextView;

    private CircleImgView img;
    private ImageView login_im01;

    private Toolbar toolbar;

    private static final int DBversion = DbHelper.VERSION;

    private LocationManager manager;
    private TextView weatherData;
    private ImageView weatherimg;
    private String bestgps;
    private Location currentLocation;
    // 更新位置頻率的條件
    int minTime = 5000; // 毫秒
    float minDistance = 5; // 公尺
    public static String BaseUrl = "https://api.openweathermap.org/";
    public static String AppId = "061c278a75dd6da235e83d5cde7b80e3";
    public static String lat = "24.170768202109116";
    public static String lon = "120.61011226844714";
    public static String lang = "zh_tw";
    private String iconurl;
    private Geocoder geocoder;
    private String areaname;
    private View login_Introduction;
    private View login_Introduction_title;
    private View login_user;
    private MenuItem menu_addPlan;
    private MenuItem menu_addPlanMyStory;
    private MenuItem menu_logIn;
    private MenuItem menu_logOut;
    private MenuItem menu_GPS_ON;
    private MenuItem menu_GPS_OFF;
    private MenuItem menu_back;
    private MenuItem menu_end;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStricMode(this);
        u_checkgps();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.more);

        setupViewCompoent();

    }
    private void enableStricMode(Context context) {
        //-------------抓取遠端資料庫設定執行續------------------------------
        StrictMode.setThreadPolicy(new
                StrictMode.
                        ThreadPolicy.Builder().
                detectDiskReads().
                detectDiskWrites().
                detectNetwork().
                penaltyLog().
                build());
        StrictMode.setVmPolicy(
                new
                        StrictMode.
                                VmPolicy.
                                Builder().
                        detectLeakedSqlLiteObjects().
                        penaltyLog().
                        penaltyDeath().
                        build());
    }
    ///........................................................

    private void setupViewCompoent() {
        ///......................weather
//        weatherData = (TextView) findViewById(R.id.weather_status);
//        weatherimg = (ImageView) findViewById(R.id.login_im01);
        //..........................................

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

//        login_in_de=(Button)findViewById(R.id.login_Introduction_btn);//相關資訊關閉紐
//        login_in_de.setOnClickListener(this);

        login_Introduction=findViewById(R.id.login_Introduction);//相關視窗
        login_Introduction_title=findViewById(R.id.login_Introduction_title);
        mStatusTextView =(TextView) findViewById(R.id.status);//登入狀態

        login_im01=(ImageView)findViewById(R.id.login_im01);// 選擇的圖片
//        handler.postDelayed(animationtime, 2000);//動畫循環時間

        //..............登入user
        login_user=findViewById(R.id.login_user);
        img = (CircleImgView) findViewById(R.id.google_icon);
        mStatusTextView = findViewById(R.id.status);
        headerLayout();

    }


//    private Runnable animationtime=new Runnable() {
//        @Override
//        public void run() {//動畫的序
//            Animation animation= AnimationUtils.loadAnimation(More.this, R.anim.anim_alpha_in);//動化效果
//            login_im01.startAnimation(animation);
//            //           handler.postDelayed(this, 8000);//動畫循環時間
//        }
//    };

//    @Override
//    public void onClick(View v) {
//
//        switch (v.getId()){
//            case R.id.login_Introduction_btn:
//                findViewById(R.id.login_weather).setVisibility(View.VISIBLE);
//                getCurrentData();
//                break;
//        }
//    }


    //...............................................................................


    protected void onDestroy(){//關閉
        super.onDestroy();
//        handler.removeCallbacks(animationtime);//動畫的序
        this.finish();
    }

    @Override
    public void onBackPressed() {//返回建
        //  super.onBackPressed();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu_addPlan = menu.findItem(R.id.menu_addPlan);
        menu_addPlanMyStory = menu.findItem(R.id.menu_addPlanMyStory);
//        menu_logIn = menu.findItem(R.id.menu_logIn);
//        menu_logOut = menu.findItem(R.id.menu_logOut);
        menu_GPS_ON = menu.findItem(R.id.menu_GPS_ON);
        menu_GPS_OFF = menu.findItem(R.id.menu_GPS_OFF);
        menu_back = menu.findItem(R.id.menu_back);
        menu_end = menu.findItem(R.id.menu_end);
        u_menu_planStory_1();
        return true;
    }
//    //.................................
private void u_menu_planStory_1() {
    menu_addPlan.setVisible(false);
    menu_addPlanMyStory.setVisible(false);
//    menu_logIn.setVisible(false);
//    menu_logOut.setVisible(false);
    menu_GPS_ON.setVisible(false);
    menu_GPS_OFF.setVisible(false);
    menu_back.setVisible(true);
    menu_end.setVisible(false);
}
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case  R.id.menu_back://menu返回
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    ///..................weather
    private void u_checkgps() {//取定位
        // 取得系統服務的LocationManager物件
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // 檢查是否有啟用GPS
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 顯示對話方塊啟用GPS
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("定位管理")
                    .setMessage("GPS目前狀態是尚未啟用.\n"
                            + "請問你是否現在就設定啟用GPS?")
                    .setPositiveButton("啟用", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 使用Intent物件啟動設定程式來更改GPS設定
                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("不啟用", null).create().show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
// 取得最佳的定位提供者
        Criteria criteria = new Criteria();
        bestgps = manager.getBestProvider(criteria, true);
        try {
            if (bestgps != null) { // 取得快取的最後位置,如果有的話
                currentLocation = manager.getLastKnownLocation(bestgps);
                manager.requestLocationUpdates(bestgps, minTime, minDistance, listener);
            } else { // 取得快取的最後位置,如果有的話
                currentLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        minTime, minDistance, listener);
            }
        } catch (SecurityException e) {
//            Log.e(TAG, "GPS權限失敗..." + e.getMessage());
        }
        updatePosition(); // 更新位置
    }


    //     建立定位服務的傾聽者物件
    private final LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            currentLocation = location;
            updatePosition();
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    // 更新現在的位置
    private void updatePosition() {
//        if (currentLocation == null) {
//            output.setText("取得定位資訊中...");
//        } else {
//            output.setText(getLocationInfo(currentLocation));//自訂義函數
//        }
        if (currentLocation == null) {
            lat = "24.170768202109116";
            lon = "120.61011226844714";
//            lat = "0.0";
//            lon = "0.0";
        } else {
            lat = Double.toString(currentLocation.getLatitude());
            lon = Double.toString(currentLocation.getLongitude());
        }
    }
//    private void getCurrentData() {
//        //********設定轉圈圈進度對話盒*****************************
//
//        final ProgressDialog pd = new ProgressDialog(this);
//
//        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        pd.setTitle("Internet");
//        pd.setMessage("Loading.........");
//        pd.setIndeterminate(false);
//        pd.show();
////***************************************************************
///*
//        Retrofit 是一套由 Square 所開發維護，將 RESTful API 寫法規範和模組化的函式庫。
//        底層也使用他們的 okHttp ，okHttp 用法參考 okHttp 章節。
//        Retrofit 預設回傳的處理器是現代化 API 最流行的 JSON，如果你要處理別的要另外實作 Converter。
//        如果需要實作 Server 驗證，建議做好另外接上 okHttpClient 去設 Interceptor。
//        在 Retrofit 1.9.0 的 Interceptor 中能做的有限。
//*/
////***************************************************************
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BaseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        WeatherService service = retrofit.create(WeatherService.class);
//        Call<WeatherResponse> call = service.getCurrentWeatherData(lat, lon, lang, AppId);
//
//        call.enqueue(new Callback<WeatherResponse>() {
//            @Override
//            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
//                if (response.code() == 200) {
//                    WeatherResponse weatherResponse = response.body();
//                    assert weatherResponse != null;
//                    String stringBuilder = getString(R.string.country) +
//                            weatherResponse.sys.country +
//                            "\n" +
//                            getString(R.string.areaname) +
//                            weatherResponse.name +
//                            "\n" +
//                            getString(R.string.Temperature) +
//// ---------------    K°凱氏轉攝氏C°-------------------
//                            (int) (Float.parseFloat("" + weatherResponse.main.temp) - 273.15) + "C°" +
//                            "\n" +
//                            getString(R.string.Temperature_Min) +
//                            (int) (Float.parseFloat("" + weatherResponse.main.temp_min) - 273.15) + "C°" +
//                            "\n" +
//                            getString(R.string.Temperature_Max) +
//                            (int) (Float.parseFloat("" + weatherResponse.main.temp_max) - 273.15) + "C°" +
//                            "\n" +
//                            getString(R.string.Humidity) +
//                            weatherResponse.main.humidity +
//                            "\n" +
//                            getString(R.string.Pressure) +
//                            weatherResponse.main.pressure;
//                    weatherData.setText(stringBuilder);  //描述
//                    //====填入座標==============
////                    weatherLat.setText(getString(R.string.weather_lat) + (lat));
////                    weatherLon.setText(getString(R.string.weather_lon) + (lon));
//                    //====轉換中文地名==============
////                    weatherName.setText(getString(R.string.weather_name) + tranlocationName(lat, lon));
//                    //======抓取 Internet 圖片==================
//                    int b_id = weatherResponse.weather.get(0).id;
//                    String b_main = weatherResponse.weather.get(0).main;
//                    String b_description = weatherResponse.weather.get(0).description;
//                    String b_icon = weatherResponse.weather.get(0).icon;
//                    iconurl = "https://openweathermap.org/img/wn/" + b_icon + "@4x.png";
//
//                    Glide.with(More.this)
//                            .load(iconurl)
//                            .placeholder(R.drawable.triplanner_logo_release)
//                            .into(weatherimg);
//                    pd.cancel();
////////-----------------------------------------------------------
//// **********************************************************************************
//                }
//            }
//            @Override
//            public void onFailure(Call<WeatherResponse> call, Throwable t) {
//                weatherData.setText(t.getMessage());
//            }
//        });
//    }
    //.....................................
    public void headerLayout(){

        DbHelper dbHper = new DbHelper(this, "astray.db", null, DBversion);
        String u_email=dbHper.find11("email");
        String u_name=dbHper.find11("nickname");
        String  u_pic=dbHper.find11("profile_picture");
        boolean login_status = dbHper.status11();
        if(login_status){
            login_user.setVisibility(View.VISIBLE);
           mStatusTextView.setText( "暱稱:"+u_name+"\n"+"Email:"+u_email);
        }else{
           login_user.setVisibility(View.GONE);
            img.setImageResource(R.drawable.logo_triplanner);
        }
        Uri user_uri;
        if(u_pic!=null){
            user_uri=Uri.parse(u_pic);
        }else{
            img.setImageResource(R.drawable.logo_triplanner);
            img.invalidate();
            user_uri=null;
        }
        if(user_uri!=null){
            Glide.with(this)
                    .load(user_uri)
                    .placeholder(R.drawable.logo_triplanner)
                    .into(img);
        }
        dbHper.close();
    }
}