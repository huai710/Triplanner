package com.shashipage.triplanner;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.view.MenuItem.SHOW_AS_ACTION_WITH_TEXT;

public class PlanMyStory_Card extends AppCompatActivity implements View.OnClickListener {
    //RE adapter
    private ArrayList<HashMap<String, String>> pm_card_arrayList = new ArrayList<>(); //adapter 需要資料陣列
    private RecyclerView pm_card_reView;
    private pm_card_Adapter pm_card_adapter;
    private String[] imgArr_bg = {"https://images.unsplash.com/photo-1596037559964-638eccebcec9?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1052&q=80",
            "https://images.unsplash.com/photo-1606820246154-537f32904b19?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1105&q=80",
            "https://images.unsplash.com/photo-1565711542753-111476a91406?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
            "https://images.unsplash.com/photo-1600635974381-7f31efa76002?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=967&q=80",
            "https://images.unsplash.com/photo-1609168783931-85558873097c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1323&q=80",
            "https://images.unsplash.com/photo-1555539967-b89c939c623a?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1950&q=80"}; //內建背景
    //    private Integer[] imgArr_bg = {R.drawable.bg_01, R.drawable.bg_02, R.drawable.bg_03}; //背景
    //SQLite
    private static final String DB_File = "astray.db", DB_TABLE = "memory01"; //資料庫名稱&資料庫table
    private SQLiteDatabase mItDb; //資料庫
    private DbHelper DbHelper09;
    private ArrayList<String> r_Tm01; //讀取SQLite
    private int DBversion = DbHelper.VERSION;
    private String sqlctl_m01;
    //headermenu
    private TextView user_name, user_email;
    private Button signInButton, signOutButton;
    private View header;

    private Intent intent = new Intent();
    private Menu menu;
    private MenuItem menu_addPlan, menu_addPlanMyStory, menu_logIn, menu_logOut, menu_GPS_ON, menu_GPS_OFF, menu_back, menu_end;
    private Toolbar toolbar;
    private Uri uri, User_IMAGE;
    private GoogleSignInClient mGoogleSignInClient;
    private CircleImgView img;
    //google 登入
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;

    //以下不知還有沒有用或做什麼用
//    private Button astray_menu_b01, astray_menu_b02, astray_menu_b03, astray_menu_b04;
    private RadioGroup astray_plan_rb01;
    private RadioButton astray_plan_rb01a, astray_plan_rb01b, astray_plan_rb01c;
    private long firstTime = 0;
    private ProgressBar pm_pb;
    private Handler mHandler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planmystory_card_main);
        setupViewCompoment();
    }

    private void enableStrictMode(PlanMyStory_Card planMyStory_card) {
        //--------抓取遠端資料庫設定執行續(官方)----------------------只要連MySQL都要加入
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


    private void setupViewCompoment() {
        //設定RecyclerView
        pm_card_reView = findViewById(R.id.pm_card_review); //抓呈現頁面的RecyclerViewID
        pm_card_reView.setLayoutManager(new LinearLayoutManager(this));
        pm_card_reView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        pm_card_adapter = new pm_card_Adapter();
        pm_card_reView.setAdapter(pm_card_adapter);
        //------------------------------------------------------------------------------------------
//        astray_menu_b01 = (Button) findViewById(R.id.astray_menu_b01);
//        astray_menu_b02 = (Button) findViewById(R.id.astray_menu_b02);
//        //astray_menu_b03=(Button) findViewById(R.id.astray_menu_b03);
//        astray_menu_b04 = (Button) findViewById(R.id.astray_menu_b04);
//        astray_menu_b01.setOnClickListener(menuON);
//        astray_menu_b02.setOnClickListener(menuON);
//        //astray_menu_b03.setOnClickListener(menuON);
//        astray_menu_b04.setOnClickListener(menuON);
        // astray_plan_rb01 = (RadioGroup) findViewById(R.id.astray_plan_rb01);
        // astray_plan_rb01a = (RadioButton) findViewById(R.id.astray_plan_rb01a);
        // astray_plan_rb01b = (RadioButton) findViewById(R.id.astray_plan_rb01b);
        // astray_plan_rb01c = (RadioButton) findViewById(R.id.astray_plan_rb01c);
        // astray_plan_rb01.setOnCheckedChangeListener(rb01ON);
        //------設定navigation drawer------
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);
        //------headermenu------
        header = navigationView.getHeaderView(0);
//
        // For sample only: make sure there is a valid server client ID.


        //--END build_client--

        // --START customize_button--
        // Set the dimensions of the sign-in button.
//        SignInButton signInButton = header.findViewById(R.id.sign_in_button);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setColorScheme(SignInButton.COLOR_LIGHT);
        signInButton = header.findViewById(R.id.sign_in_button);
        signOutButton = header.findViewById(R.id.sign_out_button);

        // Button listeners
        header.findViewById(R.id.sign_in_button).setOnClickListener(this);
        header.findViewById(R.id.sign_out_button).setOnClickListener(this);
        user_name = (TextView) header.findViewById(R.id.user_name);
        user_email = (TextView) header.findViewById(R.id.user_email);
        //--END customize_button--

        //------將drawerLayout和toolbar整合，會出現「三」按鈕------
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.START);
//                DbHelper userdb = new DbHelper(PlanMyStory_Card.this, "astray.db", null, DBversion);
//                boolean status=userdb.status11();
//                userdb.close();

                // 取得選項id
                int id = item.getItemId();
//                int a=1; //測試用關閉登入限制
//                if(a == 1){
//                if (status) {
                // 依照id判斷點了哪個項目並做相應事件

                if (id == R.id.drawer_plan) {
                    intent.setClass(PlanMyStory_Card.this, Plan_Card.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.drawer_planMyStory) {
//                        intent.setClass(PlanMyStory_Card.this, PlanMyStory_Card.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                    return true;
                }

                if (id == R.id.drawer_planFavorite) {
                    intent.setClass(PlanMyStory_Card.this, PlanFavorite.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }

                if (id == R.id.drawer_planStory) {
                    intent.setClass(PlanMyStory_Card.this, PlanStory_Card.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }

//                } else if (id == R.id.drawer_planFavorite || id == R.id.drawer_planMyStory ||
//                        id == R.id.drawer_plan || id == R.id.drawer_planStory) {
//
//                    MyAlertDialog aldDial = new MyAlertDialog(PlanMyStory_Card.this);
//                    aldDial.setTitle("請先登入");
//                    aldDial.setMessage("此功能需登入後才能使用");
//                    aldDial.setIcon(android.R.drawable.ic_dialog_info);
//                    aldDial.setCancelable(true); //返回鍵關閉
//                    aldDial.setButton(BUTTON_POSITIVE, "快速登入", aldBtListener);
//                    //  aldDial.setButton(BUTTON_NEGATIVE, "取消", aldBtListener);
//                    aldDial.show();
//
//                }
                if (id == R.id.drawer_search) {
                    intent.setClass(PlanMyStory_Card.this, Search.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }

                if (id == R.id.drawer_facebook) {
                    uri = Uri.parse("https://www.facebook.com/Astary%E6%97%85%E9%81%8AApp-105248678147992");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.action_settings) {
                    intent.setClass(PlanMyStory_Card.this, More.class);
                    startActivityForResult(intent, 0);
                    return true;
                }

                return false;
            }
        });
    }

    private void initDB() { //2021/01/30
        if (DbHelper09 == null)
            DbHelper09 = new DbHelper(this, DB_File, null, DBversion);
        r_Tm01 = DbHelper09.getShowList(DB_TABLE);
    }

    //------SQLite 撈取資料撈取轉換成arraylist--------2021/01/30---------
    private void data_toList() {
        r_Tm01 = DbHelper09.getShowList(DB_TABLE);
        pm_card_arrayList.clear();
        for (int i = 0; i < r_Tm01.size(); i++) {
            String[] fld = r_Tm01.get(i).split("#");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("pm_cardID", fld[1]);
            hashMap.put("memory_name", fld[6]);
            hashMap.put("memory_day", fld[8]);
            pm_card_arrayList.add(hashMap);
        }
        pm_card_adapter.notifyDataSetChanged(); //重整p_card_adapter

//-------2021/01/16
//        DbHelper09 = new PC09DbHelper(getApplicationContext(), DB_File, null, 1);
//        pm_card_arrayList.clear();
//        mItDb = DbHelper09.getReadableDatabase(); //讀寫資料庫
//        String sql="SELECT * FROM "+DB_TABLE;
//        Cursor cur_list=mItDb.rawQuery(sql, null); //讀取全部資料
//        if (cur_list == null) return;
//        if (cur_list.getCount() == 0) {
////            Toast.makeText(getApplicationContext(), getString(R.string.test_none), Toast.LENGTH_SHORT).show();
//        } else {
//            HashMap<String, String> hashMap0 = new HashMap<>();
//            cur_list.moveToFirst(); //不管目前在第幾筆資料，都回到第一筆資料
//            hashMap0.put("memory_name", cur_list.getString(4));
//            hashMap0.put("memory_day", cur_list.getString(6));
//            pm_card_arrayList.add(hashMap0);
//            while (cur_list.moveToNext()) {
//                HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("memory_name", cur_list.getString(4));
//                hashMap.put("memory_day", cur_list.getString(6));
//                pm_card_arrayList.add(hashMap);
//            }
//            pm_card_adapter.notifyDataSetChanged(); //重整pm_card_adapter
//            cur_list.close(); //關閉
//            mItDb.close();
//        }
    }

    // 讀取MySQL 資料--2021/01/30--------
    private void dbmysql() {
        //---------Progress對話框--------------------------------------------------------
        final ProgressDialog progDlg = new ProgressDialog(this);
        progDlg.setTitle("請稍等");
        progDlg.setMessage("讀取中......");
        progDlg.setIcon(R.drawable.ic_time);
        progDlg.setCancelable(true);
        progDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDlg.setMax(100);
        progDlg.show();
        //---------------------------------------------------------------------------

        String GoogleID = DbHelper09.find11("google_login_id");
        sqlctl_m01 = "SELECT * FROM `memory01` WHERE `google_login_id` = " + GoogleID;
//        sqlctl_m01 = "SELECT * FROM memory01 ORDER BY memory_id DESC";
        final ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl_m01);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = DBConnector.executeQuery(nameValuePairs);

                    //------
//            chk_httpstate();
                    //------

                    /**************************************************************************
                     * SQL 結果有多筆資料時使用JSONArray
                     * 只有一筆資料時直接建立JSONObject物件 JSONObject
                     * jsonData = new JSONObject(result);
                     **************************************************************************/
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray == null){
                        int c_showlist = DbHelper09.clear_all(DB_TABLE);
                    }
                    // -------------------------------------------------------
                    if (jsonArray!= null && jsonArray.length() > 0) { // MySQL 連結成功有資料
//--------------------------------------------------------
                        int c_showlist = DbHelper09.clear_all(DB_TABLE);                 // 匯入前,刪除所有SQLite資料
//--------------------------------------------------------
                        // 處理JASON 傳回來的每筆資料
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final int a = i;
                            JSONObject jsonData = null;
                            try {
                                jsonData = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ContentValues newRow = new ContentValues();
                            // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
                            Iterator itt = jsonData.keys();
                            while (itt.hasNext()) {
                                String key = itt.next().toString();
                                String value = null; // 取出欄位的值
                                try {
                                    value = jsonData.getString(key);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (value == null) {
                                    continue;
                                } else if ("".equals(value.trim())) {
                                    continue;
                                } else {
                                    try {
                                        jsonData.put(key, value.trim());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                // ------------------------------------------------------------------
                                newRow.put(key, value.toString()); // 動態找出有幾個欄位
                                // -------------------------------------------------------------------
                            }
                            // ---(2) 使用固定已知欄位---------------------------
                            // newRow.put("id", jsonData.getString("id").toString());
                            // newRow.put("name",
                            // jsonData.getString("name").toString());
                            // newRow.put("grp", jsonData.getString("grp").toString());
                            // newRow.put("address", jsonData.getString("address")
                            // -------------------加入SQLite---------------------------------------
                            long rowID = DbHelper09.insert_fMySQL(DB_TABLE, newRow);
//                    Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();
//                    ...........................................
                            mHandler.post(new Runnable() {
                                public void run() {
                                    progDlg.setProgress(a); //進度條對話框 +1
                                }
                            });
//                    ...........................................
                        }
                        // ---------------------------
                    } else {
//                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
                    }
                    r_Tm01 = DbHelper09.getShowList(DB_TABLE);  //重新載入SQLite
//                    data_toList();
                    // --------------------------------------------------------
                    handler.sendEmptyMessage(0); // 下載完成後發送處理消息
                    //-------關閉進度對話盒------
                    progDlg.cancel();
                }
            }).start();

        } catch (Exception e) {
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    data_toList();
                    break;
                default:
                    //其他想做的事情 do something.....
                    break;
            }
        }
    };

    //----------------------------------------------------------------------------------------------


    private void signOut() {
        DbHelper dbHper = new DbHelper(this, DB_File, null, DBversion);
        dbHper.clearRec11();
        dbHper.close();
        GoogleSignInClient mGoogleSignInClient = Loginuser.loginmod(this);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //--START_EXCLUDE--
                        // [END_EXCLUDE]
                        //   img.setImageResource(R.drawable.google); //還原圖示
                        user_email.setText("");
                        user_name.setText("");
                        CircleImgView aa = (CircleImgView) header.findViewById(R.id.img_Gphoto_login);
                        aa.setImageResource(R.drawable.logo_triplanner);

                    }
                });
        Intent intent=new Intent(this, PlanStory_Card.class);
        startActivity(intent);
    }


    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {


            switch (which) {
                case BUTTON_POSITIVE://
//                    getIdToken();
                    break;
//                case BUTTON_NEGATIVE:
//                    ;
//
//
//                    break;
            }
        }
    };

    //----------------------------------------------------------------------------------------------

    //------生命週期-----------------------------------------------------
    @Override
    public void onBackPressed() { //返回鍵
//        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() { //2021/01/30
        super.onResume();
//        Toast.makeText(getApplicationContext(), "onResume", Toast.LENGTH_SHORT).show();
        initDB();
        dbmysql();
        data_toList();
        headerLayout();//重新抓使用者資料
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Toast.makeText(getApplicationContext(), "onPause", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
//        int colse_DB=DbHelper09.close_DB();
//        Toast.makeText(getApplicationContext(), "onStop", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(getApplicationContext(), "onDestroy", Toast.LENGTH_SHORT).show();

    }
    //---------Menu-------------------------------------------------

    @Override
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_addPlanMyStory:
                intent.putExtra("pagefrom", "Plan");
                intent.setClass(PlanMyStory_Card.this, PlanMystory_Edit.class);
                startActivity(intent);
                break;
            case R.id.menu_back:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void u_menu_planStory_1() {
        menu_addPlan.setVisible(false);
        menu_addPlanMyStory.setVisible(true);
//        menu_logIn.setVisible(false);
//        menu_logOut.setVisible(false);
        menu_GPS_ON.setVisible(false);
        menu_GPS_OFF.setVisible(false);
        menu_end.setVisible(false);
        menu_back.setShowAsAction(SHOW_AS_ACTION_WITH_TEXT);
        menu_back.setVisible(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
//                getIdToken();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }


    //-----------副程式RecyclerView.Adapter------------------------------
    private class pm_card_Adapter extends RecyclerView.Adapter<pm_card_Adapter.ViewHolder> {
        private String planmystory_card_index;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View layout_rv, cancel;
            private final ImageView pm_card_bg;
            private TextView dayname, daynum, pm_card_ID; //宣告需用變數

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                //抓取來源
                dayname = itemView.findViewById(R.id.pm_card_dayname);
                daynum = itemView.findViewById(R.id.pm_card_daynum);
                layout_rv = itemView.findViewById(R.id.pm_card_layout001);
                cancel = itemView.findViewById(R.id.pm_card_img_cancel);
                pm_card_ID = itemView.findViewById(R.id.pm_card_id);
                pm_card_bg = itemView.findViewById(R.id.pm_card_bg);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.planmystory_card,//修改layout成自己畫的
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            String layout_bg=imgArr_bg[position%(imgArr_bg.length)];
//            String bg_URL = "https://images.unsplash.com/photo-1558697049-a7c3a41d18f5?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1402&q=80";
            //寫入資料(從arraylist取得)
            holder.pm_card_ID.setText(pm_card_arrayList.get(position).get("pm_cardID"));
            holder.dayname.setText(pm_card_arrayList.get(position).get("memory_name"));
            holder.daynum.setText(pm_card_arrayList.get(position).get("memory_day") + "天");
            Glide.with(holder.pm_card_bg.getContext())
                    .load(layout_bg)
                    .error(R.drawable.logo_triplanner)
                    .placeholder(R.drawable.logo_triplanner)
                    .fitCenter()
                    .into(holder.pm_card_bg);

//            holder.layout_rv.setBackgroundResource(imgArr_bg[position % (imgArr_bg.length)]);
            holder.layout_rv.setId(position);
            holder.layout_rv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //點取card
                    //------傳點到的cardID(MySQL上的ID)給下一頁--------2021/01/16--------
//                    DbHelper09 = new PC09DbHelper(getApplicationContext(), DB_File, null, 1);
//                    mItDb = DbHelper09.getReadableDatabase(); //讀寫資料庫
//                    int position_list = holder.getAdapterPosition();
//                    String sql="SELECT * FROM "+DB_TABLE;
//                    Cursor cur_list=mItDb.rawQuery(sql, null); //讀取全部資料
//                    if (cur_list == null) return;
//                    if (cur_list.getCount() == 0) {
////                                Toast.makeText(getApplicationContext(), getString(R.string.test_none), Toast.LENGTH_SHORT).show();
//                    } else {
//                        cur_list.moveToPosition(position_list);
//                        planmystory_card_index = cur_list.getInt(0);
//                    }
//                    cur_list.close();
//                    mItDb.close();

                    //------2021/01/30
                    planmystory_card_index = holder.pm_card_ID.getText().toString().trim();
                    intent.setClass(PlanMyStory_Card.this, PlanMystory_Show.class); //轉跳到PlanMystory_Show頁面(已建立過)
                    intent.putExtra("pagefrom", "Plan");
                    intent.putExtra("planmystory_card_index", planmystory_card_index);
                    //       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //刪除
                    //跳出取消dialog
                    AlertDialog.Builder dlg_delet = new AlertDialog.Builder(PlanMyStory_Card.this);
                    //設定dialog
                    dlg_delet.setTitle(getString(R.string.dlg_deleteTitle));
                    dlg_delet.setMessage(getString(R.string.dlg_deleteMessage));
                    dlg_delet.setIcon(R.drawable.ic_delete);
                    dlg_delet.setCancelable(false);
                    //設定按鈕
                    dlg_delet.setPositiveButton(getString(R.string.dlg_delete_positive), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //------2021/01/30
                            planmystory_card_index = holder.pm_card_ID.getText().toString().trim();
                            mysql_del();// 執行MySQL刪除
                            dbmysql();
//                            int position_list = holder.getAdapterPosition();
//                            DbHelper09 = new PC09DbHelper(getApplicationContext(), DB_File, null, 1);
//                            mItDb = DbHelper09.getWritableDatabase(); //讀寫資料庫
//                            String sql="SELECT * FROM "+DB_TABLE;
//                            Cursor cur_list=mItDb.rawQuery(sql, null); //讀取全部資料
//                            if (cur_list == null) return;
//                            if (cur_list.getCount() == 0) {
////                                Toast.makeText(getApplicationContext(), getString(R.string.test_none), Toast.LENGTH_SHORT).show();
//                            } else {
//                                cur_list.moveToPosition(position_list);
////                                Toast.makeText(getApplicationContext(), Integer.toString(position_list), Toast.LENGTH_SHORT).show();
//                                mItDb.delete(DB_TABLE, "id=" + cur_list.getInt(0), null); //delete尚未完全寫好!!
//                            }
//                            cur_list.close();
//                            mItDb.close();
//
//                            pm_card_arrayList.remove(position_list);
//                            notifyItemRemoved(position_list); //刪除
//                            notifyItemRangeChanged(position_list, pm_card_arrayList.size()); //變更陣列長度
                        }

                        private void mysql_del() {
                            //        s_id = b_id.getText().toString().trim();
                            ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
                            nameValuePairs.add(planmystory_card_index);
                            try {
                                Thread.sleep(100); //  延遲Thread 睡眠0.5秒
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
//-----------------------------------------------
                            String result = DBConnector.executeDelet_09m01(nameValuePairs);
//        Log.d(TAG, "Delete result:" + result);
//-----------------------------------------------
                        }
                    });
                    dlg_delet.setNeutralButton(getString(R.string.dlg_delete_neutral), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dlg_delet.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            //回傳list長度
            return pm_card_arrayList.size();
        }
    }

    //........................................................headermenue
    public void headerLayout(){


        DbHelper dbHper = new DbHelper(this, "astray.db", null, DBversion);

        String u_email=dbHper.find11("email");
        String u_name=dbHper.find11("nickname");
        String  u_pic=dbHper.find11("profile_picture");
        boolean login_status = dbHper.status11();
        if(login_status){
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            user_name.setText(u_name);
            user_email.setText(u_email);

        }else{
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            CircleImgView cc = (CircleImgView) header.findViewById(R.id.img_Gphoto_login);
            cc.setImageResource(R.drawable.logo_triplanner);
            user_name.setText("");
            user_email.setText("");
        }

        Uri user_uri;
        if(u_pic!=null){
            user_uri=Uri.parse(u_pic);
        }else{
            CircleImgView cc = header.findViewById(R.id.img_Gphoto_login);
            cc.setImageResource(R.drawable.logo_triplanner);
            cc.invalidate();
            user_uri=null;
        }

        if(user_uri!=null){
            CircleImgView cc = header.findViewById(R.id.img_Gphoto_login);
            Glide.with(this)
                    .load(user_uri)
                    .placeholder(R.drawable.logo_triplanner)
                    .into(cc);

        }

        dbHper.close();

    }
}