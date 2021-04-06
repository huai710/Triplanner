package com.shashipage.triplanner;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Welcome extends AppCompatActivity {
    String TAG = "TAG=>";
    private ImageView img_welcome;
    private Handler handler = new Handler();
    private long startTime;
    private DbHelper dbHper;
    private static final String DB_FILE = "astray.db";
    public static final int VERSION = DbHelper.VERSION;
    private static final String[] DB_TABLE = new String[7];
    private ArrayList<String> recSet;
    private String sqlctl;
    //..................................
    //所需要申請的權限數組
    private static final String[][] permissionsArray = new String[][]{
            {Manifest.permission.ACCESS_FINE_LOCATION, "定位"}};
    private List<String> permissionsList = new ArrayList<String>();
    //申請權限後的返回碼
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    private static final int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private boolean Authority=false;
    private String google_login_id = "";



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enableStrictMode(this);
        //..............................權限讀取  可以使用別人的app
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //.........................
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        setupViewCompoent();
    }

    //**************************************
    private void enableStrictMode(Context context) {
        //-------------抓取遠端資料庫設定執行續------------------------------
        StrictMode.setThreadPolicy(new
                StrictMode.
                        ThreadPolicy.
                        Builder().
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
    //**************************************

    private void setupViewCompoent() {
        DB_TABLE[0]="user_details";
        DB_TABLE[1]="itinerary01";
        DB_TABLE[2]="itinerary02";
        DB_TABLE[3]="memory01";
        DB_TABLE[4]="memory02";
        DB_TABLE[5]="attraction";
        DB_TABLE[6]="favorite";
        //.....給圖片效果
        img_welcome=(ImageView)findViewById(R.id.img_welcome);// 選擇的圖片
        Animation animation= AnimationUtils.loadAnimation(Welcome.this, R.anim.anim_alpha_in);//動化效果
        img_welcome.startAnimation(animation);
        startTime = System.currentTimeMillis();
        checkRequiredPermission(this);
        initDB();
//        handler.postDelayed(updateTimer, 300);
    }

    private void initDB() {

        if (dbHper == null) {
            dbHper = new DbHelper(this, DB_FILE, null, VERSION);
        }
        MyAsyncTask openDB = new MyAsyncTask();
        openDB.execute();


        //recSet = dbHper.getRecSet(); //重新載入SQLite
//        dbHper.close();
    }


    @Override
    protected void onStop() {
        super.onStop();
//        handler.removeCallbacks(updateTimer); //關閉執行續
        dbHper.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        handler.removeCallbacks(updateTimer); //關閉執行續
        dbHper.close();

    }

    @Override
    protected void onResume() {
        super.onResume();
        dbHper = new DbHelper(this, DB_FILE, null, VERSION);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
    ///...........................................................

    /*** request需要的權限*/
    private void requestNeededPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
    }
    //    所需要申請的權限數組
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getApplicationContext(), permissionsArray[i][1] + "權限申請成功!", Toast.LENGTH_LONG).show();
                        Authority=true;
                    } else {
                        Toast.makeText(getApplicationContext(), "權限被拒絕：" + permissionsArray[i][1], Toast.LENGTH_LONG).show();
//------------------
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                            Util.showDialog(this, R.string.dialog_msg1, android.R.string.ok, new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
                            requestNeededPermission();

//                                }
//                            });
                        } else {
// 否則就直接request

                            Authority=true;
                            requestNeededPermission();
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //=================================================
    private void checkRequiredPermission(final Activity activity) {
// String permission_check= String[i][0] permission;
        for (int i = 0; i < permissionsArray.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissionsArray[i][0]) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permissionsArray[i][0]);
            }
        }
        if (permissionsList.size() != 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new
                    String[permissionsList.size()]), REQUEST_CODE_ASK_PERMISSIONS);

        }else{
            startTime = System.currentTimeMillis();
            Authority=true;
//            handler.postDelayed(updateTimer, 300);
        }
    }

    @Override
    protected void onRestart() {
        checkRequiredPermission(this);
        super.onRestart();
    }

    class MyAsyncTask extends AsyncTask<Void,Void,Void> {

        // TODO implement doInBackground

        @Override
        protected Void doInBackground(Void... voids) {
            try{

//            recSet = dbHper.getRecSet(DB_TABLE[5]); //重新載入SQLite
                // --------------------------------------------------------
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }

            google_login_id = dbHper.find11("google_login_id");
            dbHper.dbmysql("memory02");
            dbHper.dbmysql("attraction",true);
            dbHper.dbmysql("itinerary02");
            if(google_login_id!=""){
                String sqlControl = "SELECT * FROM favorite WHERE google_login_id ="
                        + google_login_id;
                ArrayList<String> nameValuePairs2 = new ArrayList<>();
                nameValuePairs2.add(sqlControl);
                try {
                    dbHper.dbmysql("itinerary01",google_login_id);
                    dbHper.dbmysql("memory01",google_login_id);
                    dbHper.dbmysql("favorite",google_login_id);
//                    dbHper.dbmysql("public_home");
//                    dbHper.dbmysql("public_attr");
                } catch (Exception e) {
//            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            Intent it=new Intent();
            it.setClass(Welcome.this, PlanStory_Card.class);
            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(it);
        }
    }
}
