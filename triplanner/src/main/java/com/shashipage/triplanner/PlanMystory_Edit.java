package com.shashipage.triplanner;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

//編輯旅遊回憶頁面
//1.從行程>旅遊回憶>按下新增回憶後到此
//讀取itinerary01取得已建立行程>RV顯示>選取某行程(寫入memory01)>依據選取條件讀取itinerary02>寫入memory02>RV顯示
// >回憶名稱更新memory01(OK)>隱私權設定監聽更新memory01(OK)>回憶文字設定監聽更新memory02(OK)
//2.從行程>旅遊回憶>點進回憶卡片>按下編輯回憶後到此
//將SHOW傳來的arrayList顯示RV>回憶名稱更新memory01(OK)>隱私權設定監聽更新memory01(OK)>回憶文字設定監聽更新memory02(OK)
public class PlanMystory_Edit extends AppCompatActivity {
    //layout設定
    private LinearLayout planmystory_edit_new, planmystory_edit;
    private RecyclerView planmystory_edit_planrv,planmystory_edit_rv;
    private Button finish;//預計刪掉
    private EditText name;
    private RadioGroup privacy;
    //RV
    private Intent intent = new Intent();
    private ArrayList mystory;
    private PlanMystory_Edit_Adapter planmystory_edit_adapter;
    private ArrayList<HashMap<String,String>> arrayList_new=new ArrayList<HashMap<String,String>>();
    private Planmystory_Edit_New_Adapter planmystory_edit_new_adapter;
    //DB
    private DbHelper DbHelper04;
    private static final String DB_FILE="astray.db";
    private static final int DBversion = DbHelper.VERSION;
    //SQL
    private Long memory_id;
    private String itinerary_id,itinerary_day;
    private String t_memory_name,t_memory_day,t_modify_time;
    private int privcySelect;
    private int t_privacy;
    private String t_memory_id;
    private String t_nth_day,t_attraction_id,t_stay_time,t_order_number,t_creation_time,t_memory_edit;
    //Menu
    private MenuItem menu_addPlan,menu_addPlanMyStory,menu_logIn,menu_logOut,menu_GPS_ON,menu_GPS_OFF,menu_back,menu_end;
    private Toolbar toolbar;
    //2021.01.30
    private String google_login_id;
    private String creation_nickname;
    private String creation_profile_picture;
    private String sqlctl;
    private String t_schedule_date;
    private String page_from;
    private String day="0";
    //2021.02.06
    private boolean edit_flag = false;//編輯狀態
    private Handler mHandler=new Handler();
    private ArrayList<HashMap<String, String>> arrayList= new ArrayList<>();
    private HashMap<String, String> hashMap=new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);//MySQL要加(官方版)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planmystory_edit);
        setupViewCompoent();
    }

    //***********************************************
    private void enableStrictMode(Context context) {
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
    //************************************************

    //判斷來源(新增或編輯)，顯示設定
    private void  setupViewCompoent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);
//-------------------------------------------------------------
        privacy=(RadioGroup)findViewById(R.id.planmystory_edit_privacy);//隱私權
        privacy.setOnCheckedChangeListener(privacyON);
        planmystory_edit_new = (LinearLayout) findViewById(R.id.planmystory_edit_new);//選行程Layout
        planmystory_edit = (LinearLayout) findViewById(R.id.planmystory_edit);//編輯Layout
        name = (EditText) findViewById(R.id.planmystory_edit_name);//回憶名稱
        planmystory_edit_rv = (RecyclerView) findViewById(R.id.planmystory_edit_rv);//編輯RV
        planmystory_edit_rv.setLayoutManager(new LinearLayoutManager(this));
        finish=(Button)findViewById(R.id.planmystory_edit_finish);//完成按鈕>>改完應該可以拿掉
        finish.setVisibility(View.INVISIBLE);
//-------------------------------------------------------------
        initDB();
        google_login_id=DbHelper04.find11("google_login_id");
        creation_nickname=DbHelper04.find11("nickname");
        creation_profile_picture=DbHelper04.find11("profile_picture");

        //-------------------------------------------------------------
        //判斷來源：1.PlanMystory_Show>直接編輯(帶入已編輯資料)2.PlanMyStory_Card>要新增(選行程,帶入行程資訊)
        intent = this.getIntent();
        page_from = intent.getStringExtra("pagefrom");
        //Show按下編輯回憶到此
//        try{//閃退無法解決再加(目前可以)
        if(page_from.equals("PlanMystory_Show")){//PlanMyStory_Card要新增來源，沒新增會閃退
            edit_flag=true;//2021.02.06
            planmystory_edit_new.setVisibility(View.INVISIBLE);
            planmystory_edit.setVisibility(View.VISIBLE);
            //        接收intent資料,將已編輯的回憶帶入
            mystory = intent.getStringArrayListExtra("mystory");
            String storyname = intent.getStringExtra("storyname");
            memory_id = Long.valueOf(intent.getIntExtra("memeory_id", 0));

            name.setText(storyname);

            //            Edit 設定recyclerview放入Adapter
            planmystory_edit_adapter = new PlanMystory_Edit_Adapter(makeData2());//建立外部class並帶入資料makeData2()
            planmystory_edit_rv.setAdapter(planmystory_edit_adapter);
//                finish.setOnClickListener(updateStoryON);//更新資料庫

            /**從PlanMystory_Edit_Adapter取得資料的點擊事件*/
            planmystory_edit_adapter.setOnButtonClick(new PlanMystory_Edit_Adapter.OnClick() {
                @Override
                public void OnButtonClick(HashMap<String, String> hashMap) {
                    //intent景點資訊頁面
                    //2021.02.06
                    Bundle bundle = new Bundle();
                    //2021.02.21(不太可能發生但如果發生至少不會閃退)
                    if (hashMap.get("PLACE") == null){//行程的
                        bundle.putString("attraction_id", "0");
                        bundle.putString("attraction_name", "範例景點");
                        bundle.putString("attraction_address", "地址");
                        bundle.putString("longitude", "0");
                        bundle.putString("latitude", "0");
                        bundle.putString("district", "地區");//地區
                        bundle.putString("phone_number", "電話");//電話
                        bundle.putString("attraction_picture","0");//圖片
                        bundle.putString("tbd1","縣市");//縣市
                    } else {
                        //2021.02.06
                        bundle.putString("attraction_id", hashMap.get("attraction_id"));
                        bundle.putString("attraction_name", hashMap.get("PLACE"));
                        bundle.putString("attraction_address", hashMap.get("attraction_address"));
                        bundle.putString("longitude", hashMap.get("longitude"));
                        bundle.putString("latitude", hashMap.get("latitude"));
                        bundle.putString("district", hashMap.get("district"));//地區
                        bundle.putString("phone_number", hashMap.get("phone_number"));//電話
                        bundle.putString("attraction_picture", hashMap.get("attraction_picture"));//圖片
                        bundle.putString("tbd1", hashMap.get("tbd1"));//縣市
                    }
                    intent.putExtras(bundle);
                    intent.setClass(PlanMystory_Edit.this, Search_content.class);
                    startActivity(intent);

                }
            });//Click
            planmystory_edit_adapter.setSaveEdit(new PlanMystory_Edit_Adapter.SaveEditListener() {
                @Override
                public void SaveEdit(int t_sub_memory_id, String t_memory_edit) {
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH)+1;
                    int day1 = c.get(Calendar.DAY_OF_MONTH);
                    String modify_time = year + "/" + month + "/" + day1;
                    DbHelper04.updateMemory02("sub_memory_id", t_sub_memory_id,t_memory_edit,modify_time);
                }
            });//memory_edit更新
        }else {
            //PlanMyStory_Card按下新增回憶到此，PlanMyStory_Card要新增來源，沒新增會閃退
            planmystory_edit_new.setVisibility(View.VISIBLE);
            planmystory_edit.setVisibility(View.INVISIBLE);
            planmystory_new();//其他動作都納入此方法以下執行
        }//else

//        }catch (Exception e){
//            Toast.makeText(getApplicationContext(),"請先建立行程",Toast.LENGTH_SHORT).show();
//        }
    }

    //建立DB
    private void initDB() {
        if (DbHelper04 == null) {
            DbHelper04 = new DbHelper(this, DB_FILE, null, DBversion);
        }
    }

    //判斷為編輯回憶後接收ArrayList帶入PlanMystory_Edit_Adapter()
    private ArrayList<HashMap<String, String>> makeData2() {
        ArrayList<HashMap<String, String>> arrayList2 = new ArrayList<>();

        arrayList2=mystory;

        return arrayList2;
    }//makeData2

    //判斷為新增回憶後執行的方法
    private void planmystory_new() {
        /**資料處理*/
        //        2021.02.06讀取資料Dialog
        final ProgressDialog proDlg=new ProgressDialog(this);
        proDlg.setTitle(R.string.planmystory_new_progresstitle);
        proDlg.setMessage("行程讀取中");
        proDlg.setIcon(R.drawable.ic_time);
        proDlg.setCancelable(false);
        proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        proDlg.setMax(100);
        proDlg.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                //2021.01.30讀取行程MySQL
                sqlctl = "SELECT * FROM itinerary01 WHERE google_login_id=\""+google_login_id+"\"ORDER BY itinerary_id ASC";
                ArrayList<String> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(sqlctl);
                try {
                    String result = DBConnector.executeQuery(nameValuePairs);
                    /**************************************************************************
                     * SQL 結果有多筆資料時使用JSONArray
                     * 只有一筆資料時直接建立JSONObject物件 JSONObject
                     * jsonData = new JSONObject(result);
                     **************************************************************************/
                    JSONArray jsonArray = new JSONArray(result);
                    proDlg.setMax(jsonArray.length());//2021.02.06
// -------------------------------------------------------
                    if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                        // 處理JASON 傳回來的每筆資料
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            HashMap<String, String> hashMap = new HashMap<>();
                            // ---(2) 使用固定已知欄位---------------------------
                            hashMap.put("itinerary_id",jsonData.getString("itinerary_id"));
                            hashMap.put("itinerary_name",jsonData.getString("itinerary_name"));
                            hashMap.put("itinerary_date",jsonData.getString("start_date") + "~" +jsonData.getString("finish_date"));
                            hashMap.put("itinerary_day",jsonData.getString("itinerary_day"));
                            arrayList_new.add(hashMap);
                            final int finalI = i;
                            mHandler.post(new Runnable() {//2021.02.06
                                @Override
                                public void run() {
                                    proDlg.setProgress(finalI);
                                }
                            });
                        }
                    } else {
                        return;
                    }
                } catch (Exception e) {
                }

                hander1.sendEmptyMessage(0); // 完成後發送處理消息
                proDlg.cancel();
            }
        }).start();

    }

    private Handler hander1=new Handler(){ //Handler處理線程與Activity之間的通信問題
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (arrayList_new!=null&&arrayList_new.size()>0){
                        //        設定recyclerview
                        planmystory_edit_planrv = (RecyclerView) findViewById(R.id.planmystory_edit_planrv);
                        planmystory_edit_planrv.setLayoutManager(new LinearLayoutManager(PlanMystory_Edit.this));
                        planmystory_edit_new_adapter = new Planmystory_Edit_New_Adapter();//建立內部class
                        planmystory_edit_planrv.setAdapter(planmystory_edit_new_adapter);
                    }
                    break;
            }
        }
    };

    //選擇想建立的行程後執行的方法
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void planmystory_new_edit() {
        //寫入memory01
        //2021.02.06
        DbHelper04.clearMemory01();                 // 匯入前,刪除所有SQLite資料
        edit_flag=true;
        //--------------------------------------------------------
        int t_memory_id_0 = 0;//新建尚未有memory_id先給預設值
        t_memory_name=name.getText().toString();
        t_privacy=0;//預設是0私人
        t_memory_day=itinerary_day;
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;
        int day1 = c.get(Calendar.DAY_OF_MONTH);
        t_creation_time=year+"/"+month+"/"+day1;
        t_modify_time=year+"/"+month+"/"+day1;
        memory_id=DbHelper04.insertMemory01(t_memory_id_0, itinerary_id,google_login_id,creation_nickname,creation_profile_picture,t_memory_name,t_privacy,t_memory_day,t_creation_time,t_modify_time);

        //        2021.02.06讀取資料Dialog
        final ProgressDialog proDlg=new ProgressDialog(this);
        proDlg.setTitle(R.string.planmystory_new_progresstitle);
        proDlg.setMessage("行程資料讀取中");
        proDlg.setIcon(R.drawable.ic_time);
        proDlg.setCancelable(false);
        proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        proDlg.setMax(100);
        proDlg.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                arrayList = new ArrayList<>();
                //2021.01.30讀取行程2 MySQL
                sqlctl = "SELECT * FROM itinerary02 WHERE  itinerary_id =\""+itinerary_id+"\" ORDER BY nth_day ASC";
                ArrayList<String> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(sqlctl);
                try {
                    String result = DBConnector.executeQuery(nameValuePairs);
                    /**************************************************************************
                     * SQL 結果有多筆資料時使用JSONArray
                     * 只有一筆資料時直接建立JSONObject物件 JSONObject
                     * jsonData = new JSONObject(result);
                     **************************************************************************/
                    JSONArray jsonArray = new JSONArray(result);
                    proDlg.setMax(jsonArray.length());//2021.02.06
                    // -------------------------------------------------------
                    if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                        DbHelper04.clearMemory02();                 // 匯入前,刪除所有SQLite資料

                        // 處理JASON 傳回來的每筆資料
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            String nth_day = jsonData.getString("nth_day");
                            for (int j=1;j<3;j++){//配合四個ViewType將取得的資料區分
                                hashMap = new HashMap<>();
//                                HashMap<String, String> hashMap = new HashMap<>();
                                if (j == 1){
                                    if (!day.equals(nth_day)){//文字是用equals!!
                                        hashMap.put("DAY","第"+nth_day+"天");
                                        hashMap.put("VIEW_TYPE", "0");
                                        arrayList.add(hashMap);
                                        day=nth_day;
                                    }
                                }else if(j == 2){
                                    String attraction_id = jsonData.getString("attraction_id");//取得景點id
                                    hashMap.put("attraction_id",attraction_id);

                                    sqlctl = "SELECT * FROM attraction WHERE attraction_id=\""+attraction_id+"\"";
                                    ArrayList<String> nameValuePairs1 = new ArrayList<>();
                                    nameValuePairs1.add(sqlctl);
                                    try {
                                        String result1 = DBConnector.executeQuery(nameValuePairs1);
                                        /**************************************************************************
                                         * SQL 結果有多筆資料時使用JSONArray
                                         * 只有一筆資料時直接建立JSONObject物件 JSONObject
                                         * jsonData = new JSONObject(result);
                                         **************************************************************************/
                                        JSONArray jsonArray1 = new JSONArray(result1);
                                        if (jsonArray1.length() > 0) { // MySQL 連結成功有資料
                                            // 處理JASON 傳回來的每筆資料
                                            for (int k = 0; k < jsonArray1.length(); k++) {
                                                JSONObject jsonData1 = jsonArray1.getJSONObject(k);
                                                hashMap.put("PLACE",jsonData1.getString("attraction_name"));
                                                hashMap.put("attraction_address",jsonData1.getString("attraction_address"));
                                                hashMap.put("longitude",jsonData1.getString("longitude"));
                                                hashMap.put("latitude",jsonData1.getString("latitude"));
                                                //2021.02.06
                                                hashMap.put("district",jsonData1.getString("district"));//地區
                                                hashMap.put("phone_number",jsonData1.getString("phone_number"));//電話
                                                hashMap.put("attraction_picture",jsonData1.getString("attraction_picture"));//圖片
                                                hashMap.put("tbd1",jsonData1.getString("tbd1"));//縣市
                                            }
                                        }

                                    } catch (Exception e) {
                                    }

                                    hashMap.put("VIEW_TYPE", "1");
                                    hashMap.put("TIME", "停留時間:"+jsonData.getString("stay_time"));
                                    hashMap.put("STORY", jsonData.getString("note"));

                                    //寫入memory02
                                    int t_sub_memory_id = 0;//新建尚未有sub_memory_id先給預設值
                                    t_memory_id= String.valueOf(memory_id);
                                    t_nth_day=jsonData.getString("nth_day");
                                    t_attraction_id=jsonData.getString("attraction_id");
                                    t_schedule_date=jsonData.getString("schedule_date");
                                    t_stay_time=jsonData.getString("stay_time");
                                    t_memory_edit=jsonData.getString("note");
                                    t_order_number=jsonData.getString("order_number");
                                    Long sub_memory_id = DbHelper04.insertMemory02(t_sub_memory_id,t_memory_id, t_nth_day, t_attraction_id,t_schedule_date, t_stay_time, t_memory_edit, t_order_number,t_creation_time,t_modify_time);
                                    hashMap.put("sub_memory_id", String.valueOf(sub_memory_id));//把ID放進hashMap
                                    arrayList.add(hashMap);
                                }
                            }
                            final int finalI = i;
                            mHandler.post(new Runnable() {//2021.02.06
                                @Override
                                public void run() {
                                    proDlg.setProgress(finalI);
                                }
                            });
                        }

                    } else {
                        return;
                    }
                } catch (Exception e) {
                }

                hander2.sendEmptyMessage(0); // 完成後發送處理消息
                proDlg.cancel();
            }
        }).start();

    }

    private Handler hander2=new Handler(){ //Handler處理線程與Activity之間的通信問題
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (arrayList!=null&&arrayList.size()>0){
                        //            Edit 設定recyclerview放入Adapter
                        planmystory_edit_adapter = new PlanMystory_Edit_Adapter(arrayList);//建立外部class並帶入資料arrayList
                        planmystory_edit_rv.setAdapter(planmystory_edit_adapter);
                        /**從PlanMystory_Edit_Adapter取得資料的點擊事件*/
                        planmystory_edit_adapter.setOnButtonClick(new PlanMystory_Edit_Adapter.OnClick() {
                            @Override
                            public void OnButtonClick(HashMap<String, String> hashMap) {
                                //intent景點資訊頁面
                                //2021.02.06
                                Bundle bundle = new Bundle();
                                //2021.02.21(不太可能發生但如果發生至少不會閃退)
                                if (hashMap.get("PLACE") == null){//行程的
                                    bundle.putString("attraction_id", "0");
                                    bundle.putString("attraction_name", "範例景點");
                                    bundle.putString("attraction_address", "地址");
                                    bundle.putString("longitude", "0");
                                    bundle.putString("latitude", "0");
                                    bundle.putString("district", "地區");//地區
                                    bundle.putString("phone_number", "電話");//電話
                                    bundle.putString("attraction_picture","0");//圖片
                                    bundle.putString("tbd1","縣市");//縣市
                                } else {
                                    //2021.02.06
                                    bundle.putString("attraction_id", hashMap.get("attraction_id"));
                                    bundle.putString("attraction_name", hashMap.get("PLACE"));
                                    bundle.putString("attraction_address", hashMap.get("attraction_address"));
                                    bundle.putString("longitude", hashMap.get("longitude"));
                                    bundle.putString("latitude", hashMap.get("latitude"));
                                    bundle.putString("district", hashMap.get("district"));//地區
                                    bundle.putString("phone_number", hashMap.get("phone_number"));//電話
                                    bundle.putString("attraction_picture", hashMap.get("attraction_picture"));//圖片
                                    bundle.putString("tbd1", hashMap.get("tbd1"));//縣市
                                }
                                intent.putExtras(bundle);
                                intent.setClass(PlanMystory_Edit.this, Search_content.class);
                                startActivity(intent);
                            }
                        });//Click
                        planmystory_edit_adapter.setSaveEdit(new PlanMystory_Edit_Adapter.SaveEditListener() {
                            @Override
                            public void SaveEdit(int t_sub_memory_id, String t_memory_edit) {
                                Calendar c = Calendar.getInstance();
                                int year = c.get(Calendar.YEAR);
                                int month = c.get(Calendar.MONTH)+1;
                                int day1 = c.get(Calendar.DAY_OF_MONTH);
                                String modify_time = year + "/" + month + "/" + day1;
                                DbHelper04.updateMemory02("lid",t_sub_memory_id,t_memory_edit,modify_time);
                            }
                        });//memory_edit更新
//        finish.setOnClickListener(addStoryON);//新增資料庫
                    }
                    break;
            }
        }
    };

    //監聽隱私權設定
    private RadioGroup.OnCheckedChangeListener privacyON=new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId){
                case R.id.planmystory_edit_public:
                    privcySelect=1;//UPDATE>>編輯結束後統一更新(在Menu設定)
                    break;
                case R.id.planmystory_edit_private:
                    privcySelect=0;
                    break;
            }
        }
    };

    //生命週期
    @Override
    public void onBackPressed() {
//禁用返回鍵
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //???????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //MENU
    private void u_menu_PlanMystory_Edit() {
        menu_addPlan.setVisible(false);
        menu_addPlanMyStory.setVisible(false);
//        menu_logIn.setVisible(false);
//        menu_logOut.setVisible(false);
        menu_GPS_ON.setVisible(false);
        menu_GPS_OFF.setVisible(false);
        menu_back.setVisible(true);
        menu_end.setVisible(false);
    }

    //    以下兩個放最下面
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menu_addPlan=menu.findItem(R.id.menu_addPlan);
        menu_addPlanMyStory=menu.findItem(R.id.menu_addPlanMyStory);
//        menu_logIn=menu.findItem(R.id.menu_logIn);
//        menu_logOut=menu.findItem(R.id.menu_logOut);
        menu_GPS_ON=menu.findItem(R.id.menu_GPS_ON);
        menu_GPS_OFF=menu.findItem(R.id.menu_GPS_OFF);
        menu_back=menu.findItem(R.id.menu_back);
        menu_end=menu.findItem(R.id.menu_end);
        u_menu_PlanMystory_Edit();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                //2021.02.06
                if (edit_flag==false){//還沒編輯
                    PlanMystory_Edit.this.finish();
                }else {
                    //            Dialog
                    AlertDialog.Builder altDlgBldr = new AlertDialog.Builder(PlanMystory_Edit.this); //繼承標準對話盒，改style
                    altDlgBldr.setTitle("完成編輯");
                    altDlgBldr.setMessage("確認完成編輯?");
                    altDlgBldr.setIcon(R.drawable.ic_edit);
                    altDlgBldr.setCancelable(false);
                    //.............................................................................................................
                    altDlgBldr.setPositiveButton("完成編輯", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String memory_name = name.getText().toString().trim();
                            Calendar c = Calendar.getInstance();
                            int year = c.get(Calendar.YEAR);
                            int month = c.get(Calendar.MONTH) + 1;
                            int day1 = c.get(Calendar.DAY_OF_MONTH);
                            String modify_time = year + "/" + month + "/" + day1;

                            if (page_from.equals("PlanMystory_Show")) {//更新MySQL
                                DbHelper04.updateMemory01("memory_id", memory_id, memory_name, privcySelect, modify_time);

                                ArrayList<String> recSet = DbHelper04.getRecSet("memory01");
                                final ArrayList<String> recSet1 = DbHelper04.getRecSet("memory02");

//                                try {
//                                    Thread.sleep(500);//延遲Thread延遲0.5秒
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                                String result = DBConnector.executeIUpdateMemory01(recSet);

                                //        2021.02.06Dialog
                                final ProgressDialog proDlg=new ProgressDialog(PlanMystory_Edit.this);
                                proDlg.setTitle(R.string.planmystory_new_progresstitle);
                                proDlg.setMessage("旅遊回憶更新中");
                                proDlg.setIcon(R.drawable.ic_time);
                                proDlg.setCancelable(false);
                                proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                proDlg.setMax(100);
                                proDlg.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        proDlg.setMax(recSet1.size());

                                        for (int i = 0; i < recSet1.size(); i++) {
                                            String result1 = DBConnector.executeUpdateMemory02(recSet1.get(i));
                                            final int finalI = i;
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    proDlg.setProgress(finalI);
                                                }
                                            });
                                        }
                                        hander.sendEmptyMessage(0); // 完成後發送處理消息
                                        proDlg.cancel();
                                    }
                                }).start();

                            } else {
                                //PlanMyStory_Card按下新增回憶到此
                                DbHelper04.updateMemory01("lid", memory_id, memory_name, privcySelect, modify_time);
                                ArrayList<String> recSet = DbHelper04.getRecSet("memory01");
                                final ArrayList<String> recSet1 = DbHelper04.getRecSet("memory02");

//                                try {
//                                    Thread.sleep(500);//延遲Thread延遲0.5秒
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
                                //-----------------------------------------------真正執行新增MySQL
                                final String mysql_memory_id = DBConnector.executeInsertMemory01(recSet);

                                //        2021.02.06Dialog
                                final ProgressDialog proDlg=new ProgressDialog(PlanMystory_Edit.this);
                                proDlg.setTitle(R.string.planmystory_new_progresstitle);
                                proDlg.setMessage("旅遊回憶上傳中");
                                proDlg.setIcon(R.drawable.ic_time);
                                proDlg.setCancelable(false);
                                proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                proDlg.setMax(100);
                                proDlg.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        proDlg.setMax(recSet1.size());
                                        for (int i = 0; i < recSet1.size(); i++) {
                                            String result = DBConnector.executeInsertMemory02(mysql_memory_id, recSet1.get(i));
                                            final int finalI = i;
                                            mHandler.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    proDlg.setProgress(finalI);
                                                }
                                            });
                                        }
                                        hander.sendEmptyMessage(0); // 完成後發送處理消息
                                        proDlg.cancel();
                                    }
                                }).start();

                            }//else
//                            try {
//                                Thread.sleep(1000);//延遲Thread延遲0.5秒
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                            PlanMystory_Edit.this.finish();
                        }
                    });
                    altDlgBldr.setNeutralButton("繼續編輯", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    altDlgBldr.setNegativeButton("放棄編輯", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlanMystory_Edit.this.finish();
                        }
                    });
                    altDlgBldr.show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Handler hander=new Handler(){ //Handler處理線程與Activity之間的通信問題
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    PlanMystory_Edit.this.finish();
                    break;
            }
        }
    };

    //    副class Planmystory_Edit_New_Adapter(新增回憶>>選行程)
    class Planmystory_Edit_New_Adapter extends RecyclerView.Adapter<Planmystory_Edit_New_Adapter.ViewHolder> {
        // 建立ViewHolder宣告物件
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View planRV;
            private final TextView t001,t002;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                planRV = itemView.findViewById(R.id.planmystory_edit_planRV);
                t001 = itemView.findViewById(R.id.planmystory_edit_planrv_t001);
                t002 = itemView.findViewById(R.id.planmystory_edit_planrv_t002);
            }
        }

        //選取Layout
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.planmystory_edit_planrv, parent, false));
        }

        //設置顯示內容並設置監聽事件
        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

            holder.t001.setText(arrayList_new.get(position).get("itinerary_name"));
            holder.t002.setText(arrayList_new.get(position).get("itinerary_date"));
            holder.planRV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //傳值到EDIT,關掉NEW,新增資料表???
                    planmystory_edit_new.setVisibility(View.INVISIBLE);
                    planmystory_edit.setVisibility(View.VISIBLE);
                    name.setText(holder.t001.getText().toString());
                    itinerary_id=arrayList_new.get(position).get("itinerary_id");
                    itinerary_day=arrayList_new.get(position).get("itinerary_day");
                    planmystory_new_edit();//無法直接寫DB新增一個方法
                }
            });
        }

        @Override
        public int getItemCount() {
            return arrayList_new.size();
        }
    }

}
