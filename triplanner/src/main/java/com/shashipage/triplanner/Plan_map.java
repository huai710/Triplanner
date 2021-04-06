package com.shashipage.triplanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Plan_map extends AppCompatActivity implements OnMapReadyCallback {
    private TextView maptext;
    private MenuItem menu_addPlan,menu_addPlanMyStory,menu_logIn,menu_logOut,menu_GPS_ON,menu_GPS_OFF,menu_back,menu_end;
    private Toolbar toolbar;

    private LocationManager manager;
    private static final String[][] permissionsArray = new String[][]{
            {Manifest.permission.ACCESS_COARSE_LOCATION, "取得位置"},
            {Manifest.permission.ACCESS_FINE_LOCATION, "取得精確位置"}
    };
    private List<String> permissionsList = new ArrayList<String>();
    //申請權限後的返回碼
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private GoogleMap map;
    static LatLng VGPS = new LatLng(24.172127, 120.610313);
    int mapzoom = 12;
    private float colorDay[] = {BitmapDescriptorFactory.HUE_RED,BitmapDescriptorFactory.HUE_ORANGE,BitmapDescriptorFactory.HUE_YELLOW,BitmapDescriptorFactory.HUE_GREEN,BitmapDescriptorFactory.HUE_CYAN,BitmapDescriptorFactory.HUE_BLUE,BitmapDescriptorFactory.HUE_VIOLET};
    private static String[][][] locations = {
            {{"中區職訓", "24.172127,120.610313"},
                    {"東海大學路思義教堂", "24.179051,120.600610"},
                    {"台中公園湖心亭", "24.144671,120.683981"},
                    {"秋紅谷", "24.1674900,120.6398902"},
                    {"台中火車站", "24.136829,120.685011"},
                    {"國立科學博物館", "24.1579361,120.6659828"},
                    {"我家附近的全家", "24.160931901208286,120.69183219447908"}},
            {{"中區職訓", "24.172127,120.610313"},
                    {"東海大學路思義教堂", "24.179051,120.600610"}},
            {{"國立科學博物館", "24.1579361,120.6659828"},
                    {"我家附近的全家", "24.160931901208286,120.69183219447908"}},
            {{"中區職訓", "24.172127,120.610313"},
                    {"秋紅谷", "24.1674900,120.6398902"}},
            {{"中區職訓", "24.172127,120.610313"},
                    {"東海大學路思義教堂", "24.179051,120.600610"},
                    {"台中公園湖心亭", "24.144671,120.683981"},
                    {"秋紅谷", "24.1674900,120.6398902"},
                    {"台中火車站", "24.136829,120.685011"},
                    {"國立科學博物館", "24.1579361,120.6659828"},
                    {"我家附近的全家", "24.160931901208286,120.69183219447908"}}
    };

    private static String[] mapType = {
            "街道圖",
            "衛星圖",
            "地形圖",
            "混合圖",
            "開啟路況",
            "關閉路況"};
    private Spinner mSpnLocation,mSpnMapType;
    private int icosel=0;
    private double dLat,dLon;
    private BitmapDescriptor image_des;
    private float currentZoom = 14.0f;
    private int planId;
    private Spinner map_d1,map_d2;
    private int dnum;
    private int DBversion = DbHelper.VERSION;;
    private ArrayAdapter<String> Dadapter;
    String DB_File="astray.db";
    private ArrayList<ArrayList<String[]>> locationlist = new ArrayList();
    private int[] dPoints;
//    private boolean switchDay=true;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_map);
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //------------設定MapFragment-----------------------------------
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.plan_map_map);
        mapFragment.getMapAsync(this);
        //-------------------------------------------------------
        u_checkgps();  //檢查GPS是否開啟
        setupViewComponent();
    }

    private void u_checkgps() {

        // 檢查是否有啟用GPS
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 顯示對話方塊啟用GPS
            AlertDialog.Builder openGPS = new AlertDialog.Builder(this);
            openGPS.setTitle("定位管理")
                    .setMessage("GPS尚未啟用.\n" + "請問現在是否設定啟用GPS?")
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

    }

    private void setupViewComponent() {

        Intent it =getIntent();
        maptext = findViewById(R.id.plan_map_text);
        planId = it.getIntExtra("planid",-1);
        map_d1= findViewById(R.id.plan_map_d1);
        map_d2= findViewById(R.id.plan_map_d2);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        // 指定事件處理物件
        map_d1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadDayPoints(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void loadDayPoints(final int b_d) {
//        switchDay = true;
        map.clear();
        Dadapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item);
        if(b_d==0){
            maptext.setText("全部");
            int nn = 0;
            for (int i = 1;i<=dnum;i++){
                nn += getPointList(i,nn);
            }
        }
        else{
            maptext.setText("第"+(b_d)+"天");
            getPointList(b_d);
        }
        Dadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        map_d2.setAdapter(Dadapter);
        map_d2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (switchDay){
//                    switchDay = false;
//                    return;
//                }

                String[] sLocation = locations[b_d][position][1].split(",");
                dLat = Double.parseDouble(sLocation[0]); // 南北緯
                dLon = Double.parseDouble(sLocation[1]); // 東西經
                CameraPosition camPos = new CameraPosition.Builder()
                        .target(new LatLng(dLat,dLon))
                        .zoom(14)
                        .build();
                CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
                map.animateCamera(camUpd3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    private void getPointList(int b_d) {
        DbHelper db=new DbHelper(getApplicationContext(),DB_File,null,DBversion);
        int pointN=0;
        if(planId!=-1){
            while(true){
                String[] getPoint = db.DB_ReadPoint08(planId,pointN,b_d);
                if(getPoint==null) break;
                locations[b_d][pointN][0] = getPoint[0];
                locations[b_d][pointN][1] = getPoint[2];
                pointN++;
                Dadapter.add(getPoint[0]);

            }
            showloc(b_d);
        }
    }
    private int getPointList(int b_d,int b_nn) {
        DbHelper db=new DbHelper(getApplicationContext(),DB_File,null,DBversion);
        int pointN=0;
        if(planId!=-1){
            while(true){
                String[] getPoint = db.DB_ReadPoint08(planId,pointN,b_d);
                if(getPoint==null) break;
                locations[0][pointN+b_nn][0] = getPoint[0];
                locations[0][pointN+b_nn][1] = getPoint[2];
                pointN++;
                Dadapter.add(getPoint[0]);

            }
            showloc(0);
        }
        return pointN;
    }

    private void loadDays() {
        if(planId != -1){
            DbHelper db=new DbHelper(getApplicationContext(),DB_File,null,DBversion);
            String[] getPlan = db.DB_ReadPlan08(planId);

            if(getPlan[0].length() != 0 || getPlan[0] != ""){
                dnum = Integer.parseInt(getPlan[0]);
            }else{
                dnum = 3;
            }
//            dnum = Integer.parseInt(getPlan[0]);
        }
        else{dnum = 3;}
        locations = new String[dnum+1][100][3];
        dPoints = new int[dnum+1];
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                Plan_map.this, android.R.layout.simple_spinner_item);
        adapter.add("全部");
        for (int i = 0; i < dnum; i++) adapter.add("第"+(i+1)+"天");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        map_d1.setAdapter(adapter);
        loadDayPoints(0);
    }

    private void showloc(int b_d) {
        dPoints[b_d] = 0;
        for(int i=0;i<locations[b_d].length;i++){
            if (locations[b_d][i][0]==null) break;
            dPoints[b_d]++;
            String[] sLocation = locations[b_d][i][1].split(",");
            dLat = Double.parseDouble(sLocation[0]); // 南北緯
            dLon = Double.parseDouble(sLocation[1]); // 東西經
            String vtitle = locations[b_d][i][0];
            switch (icosel){
                case 0:
//                    image_des = BitmapDescriptorFactory
//                            .defaultMarker(colorDay[(b_d-1)%7]); // 使用橘色系統水滴
                                        image_des = BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE); // 使用橘色系統水滴
                    break;
                case 1:
                    // 運用巨集
                    String idName = "t" + String.format("%02d", i );
                    int resID = getResources().getIdentifier(idName, "drawable",
                            getPackageName());
                    image_des = BitmapDescriptorFactory.fromResource(resID);// 使用照片
                    break;
            }
            //------------------------------------------
            VGPS = new LatLng(dLat, dLon);// 更新成欲顯示的地圖座標
            map.addMarker(new MarkerOptions().position(VGPS).title(vtitle).snippet("座標: "+dLat+","+dLon).icon(image_des)
            );
        }
    }


    private void u_menu_plan_map1() {
        menu_addPlan.setVisible(false);
        menu_addPlanMyStory.setVisible(false);
//        menu_logIn.setVisible(false);
//        menu_logOut.setVisible(false);
        menu_GPS_ON.setVisible(false);
        menu_GPS_OFF.setVisible(false);
        menu_back.setVisible(true);
        menu_end.setVisible(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main,menu);
        menu_addPlan=menu.findItem(R.id.menu_addPlan);
        menu_addPlanMyStory=menu.findItem(R.id.menu_addPlanMyStory);
//        menu_logIn=menu.findItem(R.id.menu_logIn);
//        menu_logOut=menu.findItem(R.id.menu_logOut);
        menu_GPS_ON=menu.findItem(R.id.menu_GPS_ON);
        menu_GPS_OFF=menu.findItem(R.id.menu_GPS_OFF);
        menu_back=menu.findItem(R.id.menu_back);
        menu_end=menu.findItem(R.id.menu_end);
        u_menu_plan_map1();
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
//        mUiSettings = map.getUiSettings();//
//        開啟 Google Map 拖曳功能
        map.getUiSettings().setScrollGesturesEnabled(true);
//        右下角的導覽及開啟 Google Map功能
        map.getUiSettings().setMapToolbarEnabled(true);
//        左上角顯示指北針，要兩指旋轉才會出現
        map.getUiSettings().setCompassEnabled(true);
//        右下角顯示縮放按鈕的放大縮小功能
        map.getUiSettings().setZoomControlsEnabled(true);
        // --------------------------------
        map.addMarker(new MarkerOptions().position(VGPS).title("中區職訓"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        //----------取得定位許可-----------------------
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //----顯示我的位置ICO-------
            map.setMyLocationEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(), "GPS定位權限未允許", Toast.LENGTH_LONG).show();
        }
        //---------------------------------------------
        loadDays();
    }
}
