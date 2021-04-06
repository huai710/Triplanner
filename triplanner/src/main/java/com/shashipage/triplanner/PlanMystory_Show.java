package com.shashipage.triplanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

//瀏覽旅遊紀錄頁面
//1.從探索>熱門旅遊紀錄牆>按下卡片後到此(別人的沒有編輯按鈕)
//2.從行程>旅遊紀錄>按下卡片後到此(自己的有編輯按鈕)
public class PlanMystory_Show extends AppCompatActivity {
    //layout設定
    private TextView googlename,t001,t002,t003;
    private Button b001;
    private CircleImgView userimage;
    //RV
    private RecyclerView planmystory_show_rv;
    private Intent intent=new Intent();
    private PlanMystory_Show_Adapter planmystory_show_adapter;
    private ArrayList<HashMap<String, String>> arrayList= new ArrayList<>();
    private HashMap<String, String> hashMap=new HashMap<>();
    //DB
    private static final String DB_FILE="astray.db",DB_TABLE="memory01",DB_TABLE2="memory02";
    private static final int DBversion = DbHelper.VERSION;
    private int planmystory_card_index;
    //Menu
    private MenuItem menu_addPlan,menu_addPlanMyStory,menu_logIn,menu_logOut,menu_GPS_ON,menu_GPS_OFF,menu_back,menu_end;
    private Toolbar toolbar;
    //2021.01.31
    private String sqlctl;
    private DbHelper DbHelper04;
    private String day="0";
    private Handler mHandler=new Handler();
    //2021.02.21
    private boolean read_flag = false;//讀取狀態(是否需要重新讀取)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);//MySQL要加(官方版)
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planmystory_show);
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

    //讀取回憶，顯示設定
    private void setupViewCompoent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);

        b001=(Button)findViewById(R.id.planmystory_show_edit);
        b001.setOnClickListener(edit);
        userimage=(CircleImgView)findViewById(R.id.planmystory_show_userimage);
        googlename=(TextView)findViewById(R.id.planmystory_show_user);
        t001=(TextView)findViewById(R.id.planmystory_show_showdate);
        t002=(TextView)findViewById(R.id.planmystory_show_date);
        t003=(TextView)findViewById(R.id.planmystory_show_name);
        initDB();
        planmystory_card_index = Integer.parseInt(this.getIntent().getStringExtra("planmystory_card_index"));

        //2021.01.31讀取回憶01MySQL
        sqlctl = "SELECT * FROM memory01 WHERE memory_id=\""+planmystory_card_index+"\"";
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
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                DbHelper04.clearMemory01();                 // 匯入前,刪除所有SQLite資料
                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    // ---(2) 使用固定已知欄位---------------------------
                    googlename.setText(jsonData.getString("creation_nickname") );
                    //-------改變圖像--------------
                    String img_URL=jsonData.getString("creation_profile_picture").trim();
                    Glide.with(getApplicationContext()).
                            load(img_URL)
                            .placeholder(R.drawable.logo_triplanner)
                            .error(R.drawable.logo_triplanner)
                            .into(userimage);

                    //-------------------------
                    t001.setText(getString(R.string.planmystory_show_showdate)+jsonData.getString("modify_time"));
                    t002.setText(getString(R.string.planmystory_show_day)+jsonData.getString("memory_day")+"天");
                    t003.setText(jsonData.getString("memory_name"));
                    int t_memory_id=planmystory_card_index;
                    String t_itinerary_id = jsonData.getString("itinerary_id");
                    String t_google_login_id = jsonData.getString("google_login_id");
                    String t_creation_nickname = jsonData.getString("creation_nickname");
                    String t_creation_profile_picture = jsonData.getString("creation_profile_picture");
                    String t_memory_name = jsonData.getString("memory_name");
                    int t_privacy = Integer.parseInt(jsonData.getString("privacy"));
                    String t_memory_day = jsonData.getString("memory_day");
                    String t_creation_time = jsonData.getString("creation_time");
                    String t_modify_time = jsonData.getString("modify_time");
                    Long memory_id = DbHelper04.insertMemory01(t_memory_id,t_itinerary_id, t_google_login_id, t_creation_nickname, t_creation_profile_picture, t_memory_name, t_privacy, t_memory_day, t_creation_time, t_modify_time);
                }
            } else {
                return;
            }
        } catch (Exception e) {
        }

        makeData();//讀取回憶02MySQL

        //-------------------------------------------------------
//是否顯示編輯按鈕
        String page_from = this.getIntent().getStringExtra("pagefrom");
        if(page_from.equals("Plan")){//從探索頁面點過來(別人的不能編輯)
            b001.setVisibility(View.VISIBLE);
        }else{//從我的旅遊紀錄點過來(自己的可以編輯)
            b001.setVisibility(View.INVISIBLE); // 設定參考物件隱藏但佔空間
        }
    }

    //建立DB
    private void initDB() {
        if (DbHelper04 == null) {
            DbHelper04 = new DbHelper(this, DB_FILE, null, DBversion);
        }
    }

    /**資料處理，讀取回憶02*/
    private void makeData() {

        //        2021.02.06讀取資料Dialog
        final ProgressDialog proDlg=new ProgressDialog(this);
        proDlg.setTitle(R.string.planmystory_new_progresstitle);
        proDlg.setMessage("旅遊回憶讀取中");
        proDlg.setIcon(R.drawable.ic_time);
        proDlg.setCancelable(false);
        proDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        proDlg.setMax(100);
        proDlg.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                arrayList = new ArrayList<>();
                //2021.01.31讀取回憶02 MySQL
                sqlctl = "SELECT * FROM memory02 WHERE  memory_id =\""+planmystory_card_index+"\" ORDER BY nth_day ASC";
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
                            //寫入memory02
                            String t_memory_id = String.valueOf(planmystory_card_index);
                            String t_nth_day = jsonData.getString("nth_day");
                            String t_attraction_id = jsonData.getString("attraction_id");
                            String t_schedule_date = jsonData.getString("memory_date");
                            String t_stay_time = jsonData.getString("stay_time");
                            String t_memory_edit = jsonData.getString("memory_edit");
                            String t_order_number = jsonData.getString("order_number");
                            String t_creation_time = jsonData.getString("creation_time");
                            String t_modify_time = jsonData.getString("modify_time");
                            int t_sub_memory_id = Integer.parseInt(jsonData.getString("sub_memory_id"));//取得回憶02id
                            Long lid = DbHelper04.insertMemory02(t_sub_memory_id, t_memory_id, t_nth_day, t_attraction_id,t_schedule_date, t_stay_time, t_memory_edit, t_order_number,t_creation_time,t_modify_time);

                            for (int j=1;j<3;j++){//配合2個ViewType將取得的資料區分
                                hashMap = new HashMap<>();

                                if (j == 1){
                                    if (!day.equals(t_nth_day)){//文字是用equals!!
                                        hashMap.put("DAY","第"+t_nth_day+"天");
                                        hashMap.put("VIEW_TYPE", "0");
                                        arrayList.add(hashMap);
                                        day=t_nth_day;
                                    }
                                }else if(j == 2){
                                    hashMap.put("attraction_id",t_attraction_id);
                                    hashMap.put("VIEW_TYPE", "1");
                                    hashMap.put("TIME", "停留時間:"+t_stay_time);
                                    hashMap.put("STORY", t_memory_edit);
                                    hashMap.put("sub_memory_id", String.valueOf(t_sub_memory_id));

                                    sqlctl = "SELECT * FROM attraction WHERE attraction_id=\""+t_attraction_id+"\"";
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
                    }
                } catch (Exception e) {
                }
                hander.sendEmptyMessage(0); // 完成後發送處理消息
                proDlg.cancel();

            }
        }).start();

    }//makeData

    private Handler hander=new Handler(){ //Handler處理線程與Activity之間的通信問題
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    if (arrayList!=null&&arrayList.size()>0){
                        //---設定RecyclerView
                        planmystory_show_rv = (RecyclerView) findViewById(R.id.planmystory_show_rv);
                        planmystory_show_rv.setLayoutManager(new LinearLayoutManager(PlanMystory_Show.this));
                        planmystory_show_adapter = new PlanMystory_Show_Adapter(arrayList);//建立外部class並帶入資料makeData()
                        planmystory_show_rv.setAdapter(planmystory_show_adapter);
                        /**從MyRecyclerViewAdapter取得資料的點擊事件*/
                        planmystory_show_adapter.setOnButtonClick(new PlanMystory_Show_Adapter.OnClick() {
                            @Override
                            public void OnButtonClick(HashMap<String, String> hashMap) {
                                //intent景點資訊頁面
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

                                intent.setClass(PlanMystory_Show.this, Search_content.class);
                                startActivity(intent);
                                //2021.20.21
                                read_flag=true;

                            }
                        });//Click
                    }
                    break;
            }
        }
    };

    //編輯按鈕監聽
    private View.OnClickListener edit=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //intent編輯旅遊紀錄頁面
//            帶著資料往下傳1.intent來源2.已建立回憶資料
            String name = t003.getText().toString();
            intent.putExtra("pagefrom", "PlanMystory_Show");
            intent.putExtra("mystory", arrayList);
            intent.putExtra("storyname",name);
            intent.putExtra("memeory_id",planmystory_card_index);
            intent.setClass(PlanMystory_Show.this, PlanMystory_Edit.class);
            startActivity(intent);
        }
    };

    //生命週期
    @Override
    public void onBackPressed() {
//禁用返回鍵
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //2021.02.21
        if (read_flag==false){//需要重新讀取
            setupViewCompoent();
        }else {
            read_flag=false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //????????????????
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
                //按下返回不傳值直接回PlanMyStory_Card頁面,
                PlanMystory_Show.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}