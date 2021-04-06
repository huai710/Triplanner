package com.shashipage.triplanner;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Search_map extends AppCompatActivity implements
        OnMapReadyCallback {
    /**********************************************
     permission
     **********************************************/
    //所需要申請的權限數組
    private static final String[][] permissionsArray = new String[][]{
            {Manifest.permission.ACCESS_FINE_LOCATION, "定位"},
            {Manifest.permission.ACCESS_COARSE_LOCATION, "定位2"}
    };
    private List<String> permissionsList = new ArrayList<String>();
    //申請權限後的返回碼
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    //    ========================================

    private GoogleMap map;
    private String longitude,latitude,attraction_name,attraction_address,attraction_picture,phone_number,tbd1;
    private float lng,lat;
    private Toolbar toolbar;
    private MenuItem menu_addPlan,menu_addPlanMyStory,menu_logIn,menu_logOut,menu_GPS_ON,menu_GPS_OFF,menu_back,menu_end;
    private static String[] mapType = {
            "街道圖",
            "衛星圖",
            "地形圖",
            "混合圖",
            "開啟路況",
            "關閉路況"};
    static LatLng VGPS = new LatLng(24.172127, 120.610313);
    float currentZoom = 12;
    private Spinner mSpnMapType;
    double dLat, dLon;
    private LocationManager locationManager;
    private String provider=null; // 提供資料
    private final String TAG = "tcnr01=>";
    private int icosel=0; //圖示旗標
    private BitmapDescriptor image_des;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkRequiredPermission(this); //  檢查SDK版本, 確認是否獲得權限.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
//        u_checkgps();  //檢查GPS是否開啟
        setupViewComponent();
    }

    private void setupViewComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        mSpnMapType = (Spinner) this.findViewById(R.id.spnMapType);
        icosel = 0;  //設定圖示初始值
        // -------mSpnMapType--------
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (int i = 0; i < mapType.length; i++)
            adapter.add(mapType[i]);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnMapType.setAdapter(adapter);
        //-----------設定ARGB 透明度----
//        mSpnMapType.setPopupBackgroundDrawable(new ColorDrawable(0x00FFFFFF)); //全透明
        mSpnMapType.setPopupBackgroundDrawable(new ColorDrawable(0x80FFFFFF)); //50%透明
//        # ARGB依次代表透明度（alpha）、紅色(red)、綠色(green)、藍色(blue)
//        100% — FF       95% — F2        90% — E6        85% — D9
//        80% — CC        75% — BF        70% — B3        65% — A6
//        60% — 99        55% — 8C        50% — 80        45% — 73
//        40% — 66        35% — 59        30% — 4D        25% — 40
//        20% — 33        15% — 26        10% — 1A         5% — 0D         0% — 00
        //---------------
        mSpnMapType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL); // 道路地圖。
                        break;
                    case 1:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE); // 衛星空照圖
                        break;
                    case 2:
                        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN); // 地形圖
                        break;
                    case 3:
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID); // 道路地圖混合空照圖
                        break;
                    case 4:
                        map.setTrafficEnabled(true); //開啟路況
                        break;
                    case 5:
                        map.setTrafficEnabled(false); //關閉路況
                        break;
                }
                setMapLocation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setMapLocation() {
        dLat = lat; // 南北緯
        dLon = lng; // 東西經

//        int a=0;
        double dLat = Double.parseDouble(latitude);    // 南北緯
        double dLon = Double.parseDouble(longitude);    // 東西經
        String vtitle = tbd1+" "+attraction_name;
        //--- 設定所選位置之當地圖示 ---//
        image_des = BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_RED); //使用系統水滴

        VGPS = new LatLng(dLat, dLon);
        // --- 設定自訂義infowindow ---//
        map.setInfoWindowAdapter(new CustomInfoWindowAdapter());
//        map.setOnMarkerClickListener(this);
        // map.setOnInfoWindowClickListener(this);
        // map.setOnMarkerDragListener(this);
        // --- 根據所選位置項目顯示地圖/標示文字與圖片 ---//

        map.addMarker(new MarkerOptions()
                .position(VGPS)
                .title(vtitle)
                .snippet("座標:" + dLat + "," + dLon)
                .infoWindowAnchor(0.5f, 0.9f)
                .icon(image_des));// 顯示圖標文字


        map.moveCamera(CameraUpdateFactory.newLatLngZoom(VGPS, currentZoom));
        onCameraChange(map.getCameraPosition());
//        map.setOnMyLocationButtononClickListener(this);
//        map.setOnMyLocationButtonClickListener(this);
    }

    private void onCameraChange(CameraPosition cameraPosition) {

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //從search_content傳過來的值
//        longitude,latitude,attraction_name,attraction_address,attraction_picture,phone_number,tbd1
        Bundle bundle=getIntent().getExtras();
        attraction_name=bundle.getString("attraction_name");//景點名稱
        attraction_address=bundle.getString("attraction_address");//景點地址
        attraction_picture=bundle.getString("attraction_picture");//景點圖片
        phone_number=bundle.getString("phone_number");//電話
        tbd1=bundle.getString("tbd1");//景點縣市
        longitude=bundle.getString("longitude");//景點東經
        latitude=bundle.getString("latitude");//景點北緯
        //將接收到的經緯度從String->Float
        lng=Float.parseFloat(longitude);//東經String->Float
        lat=Float.parseFloat(latitude);//北緯String->Float
        //map的基本元件
        map = googleMap;
        // 開啟 Google Map 拖曳功能
        map.getUiSettings().setScrollGesturesEnabled(true);
        // 右下角的導覽及開啟 Google Map功能
        map.getUiSettings().setMapToolbarEnabled(true);

        //左上角顯示指北針，要兩指旋轉才會出現
        map.getUiSettings().setCompassEnabled(true);

        //右下角顯示縮放按鈕的放大縮小功能
        map.getUiSettings().setZoomControlsEnabled(true);
    }
    //    @Override
//    public boolean onMyLocationButtonClick() {
//        Toast.makeText(getApplicationContext(), "返回GPS目前位置", Toast.LENGTH_LONG).show();
//        return false;
//    }
    //----------------------------------------------------------------------
//    /* 檢查GPS 是否開啟 */
//    private boolean initLocationProvider() {
//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            provider = LocationManager.GPS_PROVIDER;
//            return true;
//        }
//        return false;
//    }
//    //-------提示使用者開啟GPS定位------------------------------------
//    private void u_checkgps() {
//        // 取得系統服務的LocationManager物件
//        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        // 檢查是否有啟用GPS
//        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            // 顯示對話方塊啟用GPS
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("定位管理")
//                    .setMessage("GPS目前狀態是尚未啟用.\n"
//                            + "請問你是否現在就設定啟用GPS?")
//                    .setPositiveButton("啟用", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // 使用Intent物件啟動設定程式來更改GPS設定
//                            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(i);
//                        }
//                    })
//                    .setNegativeButton("不啟用", null).create().show();
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
    //----------------檢查SDK版本, 確認是否獲得權限.------------------------------------------------
    private void checkRequiredPermission(final Activity activity) { //
//        String permission_check= String[i][0] permission;
        for (int i = 0; i < permissionsArray.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissionsArray[i][0]) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permissionsArray[i][0]);
            }
        }
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);
        }
    }
    /*** request需要的權限*/
    private void requestNeededPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
    }

    /*** ***********************************
     *  所需要申請的權限數組
     * ************************************/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), permissionsArray[i][1] + "權限申請成功!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "權限被拒絕： " + permissionsArray[i][1], Toast.LENGTH_LONG).show();
                        //------------------
                        // 這邊是照官網說法，在確認沒有權限的時候，確認是否需要說明原因
                        // 需要的話就先顯示原因，在使用者看過原因後，再request權限
                        //-------------------
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            Util.showDialog(this, R.string.dialog_msg1, android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestNeededPermission();
                                }
                            });
                        } else {
                            // 否則就直接request
                            requestNeededPermission();
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //-----生命週期

    @Override
    protected void onStart() {
        super.onStart();
//        checkRequiredPermission(this);     //  檢查SDK版本, 確認是否獲得權限.
//        if (initLocationProvider()) {
////            nowaddress();
//        } else {
//            Toast.makeText(getApplicationContext(),"GPS未開啟，請先開啟定位!!",Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    //-----副程式
    //===========自訂義indfowindow class======================
    private class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        @Override

        public View getInfoWindow(Marker marker) {
            // 依指定layout檔，建立地標訊息視窗View物件
            // --------------------------------------------------------------------------------------
            // 單一框
            // View infoWindow=
            // getLayoutInflater().inflate(R.layout.custom_info_window,
            // null);
            // 有指示的外框
            View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_content, null);
            infoWindow.setAlpha(0.8f); //透明度
            // ----------------------------------------------
            // 顯示地標title
            TextView title = ((TextView) infoWindow.findViewById(R.id.title));
            String[] ss = marker.getTitle().split("#");
            title.setText(ss[0]);
            // 顯示地標snippet
            TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
            snippet.setText(marker.getSnippet());

            //--測試--
            TextView tel = ((TextView) infoWindow.findViewById(R.id.tel));
            TextView addr = ((TextView) infoWindow.findViewById(R.id.addr));
            tel.setText(phone_number);
            addr.setText(attraction_address);

            // 顯示圖片
            ImageView imageview = ((ImageView) infoWindow.findViewById(R.id.content_ico));
            Glide.with(getApplicationContext())
                    .load(attraction_picture)
                    .placeholder(R.drawable.logo_triplanner)
                    .error(R.drawable.logo_triplanner)
                    .into(imageview);



            return infoWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {

            //按下去會做的事情，ex:導航等
            Toast.makeText(getApplicationContext(), "getInfoContents", Toast.LENGTH_LONG).show();
            return null;
        }
    }
}