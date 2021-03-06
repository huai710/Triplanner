package com.shashipage.triplanner;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import static android.content.DialogInterface.BUTTON_POSITIVE;

;

public class PlanStory_Card extends AppCompatActivity implements View.OnClickListener {
    //RE adapter
    private ArrayList<HashMap<String, String>> ps_card_arrayList = new ArrayList<>(); //adapter ??????????????????
    private RecyclerView ps_card_reView;
    private ps_card_Adapter ps_card_adapter;
    private ArrayList<Map<String, Object>> ps_card_arrayList_photo = new ArrayList<>();
    private int id;
    private String[] imgArr_bg = {"https://images.unsplash.com/photo-1596037559964-638eccebcec9?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1052&q=80",
            "https://images.unsplash.com/photo-1606820246154-537f32904b19?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1105&q=80",
            "https://images.unsplash.com/photo-1565711542753-111476a91406?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1050&q=80",
            "https://images.unsplash.com/photo-1600635974381-7f31efa76002?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=967&q=80",
            "https://images.unsplash.com/photo-1609168783931-85558873097c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1323&q=80",
            "https://images.unsplash.com/photo-1555539967-b89c939c623a?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1950&q=80"}; //????????????
    private MediaPlayer goodbye;
    //?????????
    private ArrayList<Integer> slidesArrayList = new ArrayList<Integer>(); //?????????
    private static int currentPage = 0;
    private Integer[] slides_bg = {R.drawable.slideshow01, R.drawable.slideshow02, R.drawable.slideshow03, R.drawable.slideshow04, R.drawable.slideshow05, R.drawable.slideshow06};
    private ViewPager mPager;
    private CircleIndicator indicator;
    //SQLite
    private static final String DB_File = "astray.db"; //???????????????&?????????table
    private static final String DB_TABLE = "public_home" ,DB_TABLE2="public_attr",DB_TABLE3="attraction";

    private SQLiteDatabase mItDb; //?????????
    private DbHelper DbHelper09;
    private int DBversion = DbHelper.VERSION;
    private ArrayList<String> r_Thome;
    private ArrayList<String> r_Thome2;
    private ArrayList<String> r_Thome_attr;
    private String sqlctl_h01;

    private Intent intent = new Intent();
    private MenuItem menu_addPlan, menu_addPlanMyStory, menu_logIn, menu_logOut, menu_GPS_ON, menu_GPS_OFF, menu_back, menu_end;
    private Toolbar toolbar;
    private Uri uri, User_IMAGE;

    //google ??????
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;
    private Button signInButton, signOutButton;


    //??????????????????????????????????????????
    private LinearLayout ps_card_layout001;
    private long firstTime = 0;
    private Button ps_slide_expand1, ps_slide_expand2;
    private RelativeLayout ps_slide_expand_layout;
    private TextView user_name, user_email;
    private CircleImgView img;
    private View header;

    private SQLiteDatabase mFriendDb;
    private GridView location_gView;
    private List<Map<String, Object>> l_item =new ArrayList<>();
    private List<Map<String, String>> l_attr =new ArrayList<>();
    private SimpleAdapter l_adapter;
    private String sqlctl_l01;
    private Handler mHandler=new Handler();
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        //..............................????????????  ?????????????????????app
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        //.........................
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planstory_card_main);



        setupViewCompoment();
        homeSlideShow();
    }

    private void enableStrictMode(PlanStory_Card planStory_card) {
        //--------????????????????????????????????????(??????)----------------------?????????MySQL????????????
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
        //??????RecyclerView
        ps_card_reView = findViewById(R.id.ps_card_review); //??????????????????RecyclerViewID
        ps_card_reView.setLayoutManager(new LinearLayoutManager(this));
        ps_card_reView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        ps_card_adapter = new ps_card_Adapter();
        ps_card_reView.setAdapter(ps_card_adapter);
//        //??????RecyclerView????????????
//        recyclerviewAction_pscard(ps_card_reView,ps_card_arrayList,ps_card_arrayList_photo,ps_card_adapter);


        //------????????????(????????????)------2021/02/13
        location_gView=(GridView)findViewById(R.id.location_g_view);
        //??????????????????


        //------------------------------------------------------------------------------------------
        //planstory_slide_expand?????????????????????
        ps_slide_expand_layout = (RelativeLayout) findViewById(R.id.ps_slide_expand_layout);

        //------------------------------------------------------------------------------------------

        //??????navigation drawer
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // ???toolbar??????APP???ActionBar
        setSupportActionBar(toolbar);

        //----------------------------------------------------------------------------------------------
        // ??????Header
        header = navigationView.getHeaderView(0);
        //
        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();

        // --START configure_signin--
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        //--END configure_signin---

        // --START build_client--
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(PlanStory_Card.this, gso);
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


        // ???drawerLayout???toolbar?????????????????????????????????
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // ?????????????????????
                drawerLayout.closeDrawer(GravityCompat.START);
                DbHelper userdb = new DbHelper(PlanStory_Card.this, DB_File, null, DBversion);
                boolean status=userdb.status11();
                userdb.close();


                // ????????????id
                int id = item.getItemId();
//                int a=1; //???????????????????????????
//                if(a == 1){
                if (status) {
                    // ??????id??????????????????????????????????????????
                    if (id == R.id.drawer_planStory) {
//                    intent.setClass(PlanStory_Card.this, PlanStory_Card.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                        return true;
                    }

                    if (id == R.id.drawer_plan) {
                        intent.setClass(PlanStory_Card.this, Plan_Card.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    }
                    if (id == R.id.drawer_planMyStory) {
                        intent.setClass(PlanStory_Card.this, PlanMyStory_Card.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    }



                    if (id == R.id.drawer_planFavorite) {
                        intent.setClass(PlanStory_Card.this, PlanFavorite.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        return true;
                    }

                } else if (id == R.id.drawer_planFavorite || id == R.id.drawer_planMyStory ||
                        id == R.id.drawer_plan ) {

                    MyAlertDialog aldDial = new MyAlertDialog(PlanStory_Card.this);
                    aldDial.setTitle("????????????");
                    aldDial.setMessage("?????????????????????????????????");
                    aldDial.setIcon(R.drawable.logo_triplanner);
                    aldDial.setCancelable(true); //???????????????
                    aldDial.setButton(BUTTON_POSITIVE, "????????????", aldBtListener);
                    //  aldDial.setButton(BUTTON_NEGATIVE, "??????", aldBtListener);
                    aldDial.show();

                }
                if (id == R.id.drawer_search) {
                    intent.setClass(PlanStory_Card.this, Search.class);
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
                    intent.setClass(PlanStory_Card.this, More.class);
                    startActivityForResult(intent, 0);
                    return true;
                }

                return false;
            }
        });
    }
    //------2021/02/13------
    private void setGridView() {
        int size = l_item.size(); //?????????????????????
        int length = 120; //???????????????
        //----------------------
        DisplayMetrics dm = new DisplayMetrics(); //??????????????????????????????
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        float w=dm.widthPixels;
        float h=dm.heightPixels;
        int gridviewWidth = (int) (size * (length + 10) * density*0.9); //???????????????????????????
        int itemWidth = (int) (length * density*0.9); //????????????????????????
//String aa="?????????";
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        location_gView.setLayoutParams(params);
        location_gView.setColumnWidth(itemWidth);
        location_gView.setHorizontalSpacing(20); // ??????
        location_gView.setStretchMode(GridView.NO_STRETCH); //
        location_gView.setNumColumns(size); // ???????????????
        l_adapter=new MySimpleAdapter(PlanStory_Card.this,
                l_item,
                R.layout.location_gview_item,
                new String[]{"attr_name"},
                new int[]{R.id.l_text});


        location_gView.setAdapter(l_adapter);
        location_gView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object info_l = l_item.get(position).get("attr_id");
                r_Thome_attr = DbHelper09.getShowList_l(DB_TABLE3,info_l);
//                l_attr.clear();
//                for (int i = 0; i < r_Thome_attr.size(); i++) {
                String[] fld = r_Thome_attr.get(0).split("#");
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("attraction_id",info_l.toString());
                hashMap.put("PLACE",fld[4]);
                hashMap.put("attraction_address",fld[6]);
                hashMap.put("longitude",fld[7]);
                hashMap.put("latitude",fld[8]);
                //2021.02.06
                hashMap.put("district",fld[3]);//??????
                hashMap.put("phone_number",fld[12]);//??????
                hashMap.put("attraction_picture",fld[14]);//??????
                hashMap.put("tbd1",fld[20]);//??????
//                    l_attr.add(hashMap);
//                }

                //intent??????????????????
                Bundle bundle = new Bundle();
                //------2021/02/25
                if (hashMap.get("PLACE") == null){//?????????
                    bundle.putString("attraction_id", "0");
                    bundle.putString("attraction_name", "????????????");
                    bundle.putString("attraction_address", "??????");
                    bundle.putString("longitude", "0");
                    bundle.putString("latitude", "0");
                    bundle.putString("district", "??????");//??????
                    bundle.putString("phone_number", "??????");//??????
                    bundle.putString("attraction_picture","0");//??????
                    bundle.putString("tbd1","??????");//??????
                } else {
                    //2021.02.06
                    bundle.putString("attraction_id", hashMap.get("attraction_id"));
                    bundle.putString("attraction_name", hashMap.get("PLACE"));
                    bundle.putString("attraction_address", hashMap.get("attraction_address"));
                    bundle.putString("longitude", hashMap.get("longitude"));
                    bundle.putString("latitude", hashMap.get("latitude"));
                    bundle.putString("district", hashMap.get("district"));//??????
                    bundle.putString("phone_number", hashMap.get("phone_number"));//??????
                    bundle.putString("attraction_picture", hashMap.get("attraction_picture"));//??????
                    bundle.putString("tbd1", hashMap.get("tbd1"));//??????
                }
                intent.putExtras(bundle);

                intent.setClass(PlanStory_Card.this, Search_content.class);
                startActivity(intent);
            }
        });
    }

    //-----2021/01/30--------
    private void initDB() {
        if (DbHelper09 == null)
            DbHelper09 = new DbHelper(this, DB_File, null, DBversion);
        r_Thome = DbHelper09.getShowList(DB_TABLE);
    }

    //------SQLite ???????????????????????????arraylist--------2021/01/30---------
    private void data_toList() {
        r_Thome = DbHelper09.getShowList(DB_TABLE);
        ps_card_arrayList.clear();
        for (int i = 0; i < r_Thome.size(); i++) {
            String[] fld = r_Thome.get(i).split("#");
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("ps_cardID", fld[4]);
            hashMap.put("google_name", fld[2]);
            hashMap.put("photo_URL", fld[3]);
            hashMap.put("memory_Name", fld[5]);
            hashMap.put("memory_Day", fld[6]);
            hashMap.put("modify_Time", fld[7]);
            ps_card_arrayList.add(hashMap);
        }
        ps_card_adapter.notifyDataSetChanged(); //??????p_card_adapter

        r_Thome2=DbHelper09.getShowList(DB_TABLE2);
        l_item.clear();
        for (int i=0;i<r_Thome2.size();i++){
            String[] fld=r_Thome2.get(i).split("#");
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("attr_id",fld[1]);
            hashMap.put("attr_name",fld[2]);
            hashMap.put("attr_photoURL",fld[3]);
            l_item.add(hashMap);
        }
        setGridView();


    }

    // ??????MySQL ??????--2021/01/30--------
    private void dbmysql() {
        //---------Progress?????????--------------------------------------------------------
        final ProgressDialog progDlg = new ProgressDialog(this);
        progDlg.setTitle("?????????");
        progDlg.setMessage("?????????......");
        progDlg.setIcon(R.drawable.ic_time);
        progDlg.setCancelable(true);
        progDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDlg.setMax(100);
        progDlg.show();
        //---------------------------------------------------------------------------
        sqlctl_h01 = "SELECT `google_login_id`,`creation_nickname`,`creation_profile_picture`,`memory_id`,`memory_name`,`memory_day`,`modify_time` FROM `memory01` WHERE `privacy`=1"; //????????????
        sqlctl_l01="SELECT `attraction`.`attraction_id`,`attraction`.`attraction_name`,`attraction`.`attraction_picture` FROM `attraction` ORDER BY rand() LIMIT 6"; //????????????
        final ArrayList<String> nameValuePairs = new ArrayList<>();
        final ArrayList<String> nameValuePairs2 = new ArrayList<>();
        nameValuePairs.add(sqlctl_h01);
        nameValuePairs2.add(sqlctl_l01);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = DBConnector.executeQuery(nameValuePairs);
                    String result2= DBConnector.executeQuery(nameValuePairs2);

                    //------
//            chk_httpstate();
                    //------

                    /**************************************************************************
                     * SQL ??????????????????????????????JSONArray
                     * ?????????????????????????????????JSONObject?????? JSONObject
                     * jsonData = new JSONObject(result);
                     **************************************************************************/
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = new JSONArray(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray jsonArray2 = null;
                    try {
                        jsonArray2 = new JSONArray(result2);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (jsonArray==null){
                        int c_showlist = DbHelper09.clear_all(DB_TABLE);  //????????????
                    }
                    // -------------------------------------------------------
                    if (jsonArray!= null && jsonArray.length() > 0) { // MySQL ????????????????????? ????????????(??????)
                        progDlg.setMax(jsonArray.length()); //????????????????????????
//--------------------------------------------------------
                        int c_showlist = DbHelper09.clear_all(DB_TABLE);                 // ?????????,????????????SQLite??????
//--------------------------------------------------------
                        // ??????JASON ????????????????????????
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final int a = i;
                            JSONObject jsonData = null;
                            try {
                                jsonData = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ContentValues newRow = new ContentValues();
                            // --(1) ?????????????????? --?????? jsonObject ????????????("key","value")-----------------------
                            Iterator itt = jsonData.keys();
                            while (itt.hasNext()) {
                                String key = itt.next().toString();
                                String value = null; // ??????????????????
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
                                newRow.put(key, value.toString()); // ???????????????????????????
                                // -------------------------------------------------------------------
                            }

                            // -------------------??????SQLite---------------------------------------
                            long rowID = DbHelper09.insert_fMySQL(DB_TABLE, newRow);
//                    Toast.makeText(getApplicationContext(), "????????? " + Integer.toString(jsonArray.length()) + " ?????????", Toast.LENGTH_SHORT).show();
//                    ...........................................
                            mHandler.post(new Runnable() {
                                public void run() {
                                    progDlg.setProgress(a); //?????????????????? +1
                                }
                            });
//                    ...........................................
                        }
                        // ---------------------------
                    } else {
//                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_LONG).show();
                    }

                    if (jsonArray2==null){
                        int c_showlist = DbHelper09.clear_all(DB_TABLE);  //????????????
                    }
                    if (jsonArray2!= null && jsonArray2.length() > 0) { // MySQL ????????????????????? ????????????
//--------------------------------------------------------
                        int c_showlist = DbHelper09.clear_all(DB_TABLE2);                 // ?????????,????????????SQLite??????
//--------------------------------------------------------
                        // ??????JASON ????????????????????????
                        for (int i = 0; i < jsonArray2.length(); i++) {
                            JSONObject jsonData = null;
                            try {
                                jsonData = jsonArray2.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ContentValues newRow = new ContentValues();
                            // --(1) ?????????????????? --?????? jsonObject ????????????("key","value")-----------------------
                            Iterator itt = jsonData.keys();
                            while (itt.hasNext()) {
                                String key = itt.next().toString();
                                String value = null; // ??????????????????
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
                                newRow.put(key, value.toString()); // ???????????????????????????
                                // -------------------------------------------------------------------
                            }

                            // -------------------??????SQLite---------------------------------------
                            long rowID = DbHelper09.insert_fMySQL(DB_TABLE2, newRow);
                        }
                        // ---------------------------
                    } else {
                    }
                    r_Thome = DbHelper09.getShowList(DB_TABLE);  //????????????SQLite ????????????(??????)
                    r_Thome2 = DbHelper09.getShowList(DB_TABLE2);  //????????????SQLite ????????????
//                    data_toList();
                    // --------------------------------------------------------
                    handler.sendEmptyMessage(0); // ?????????????????????????????????
                    //-------?????????????????????------
                    progDlg.cancel();
                }
            }).start();

            // --------------------------------------------------------
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
                    //????????????????????? do something.....
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                getIdToken();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }

    }

    //----------------------------------------------------------------------------------------------
    //draw_header??????

    private void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in google_sign.xml, must end with " + suffix;

        }
    }

    private void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    private void signOut() {
        DbHelper dbHper = new DbHelper(this, DB_File, null, DBversion);
        dbHper.clearRec11();
        dbHper.close();
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(PlanStory_Card.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //--START_EXCLUDE--
                        updateUI(null);
                        // [END_EXCLUDE]

                        //   img.setImageResource(R.drawable.google); //????????????
                        user_email.setText("");
                        user_name.setText("");
                        CircleImgView aa = (CircleImgView) header.findViewById(R.id.img_Gphoto_login);
                        aa.setImageResource(R.drawable.logo_triplanner);

                    }
                });
    }

private void updateUI(GoogleSignInAccount account) {
    if (account != null) {
        Loginuser.userdata(account,this);
        signInButton.setVisibility(View.GONE);
        signOutButton.setVisibility(View.VISIBLE);
        user_name.setText(account.getDisplayName());
        user_email.setText(account.getEmail());
        User_IMAGE = account.getPhotoUrl();
        if (User_IMAGE == null) {
            return;
        }
        img = header.findViewById(R.id.img_Gphoto_login);
        Glide.with(this)
                .load(User_IMAGE)
                .placeholder(R.drawable.logo_triplanner)
                .into(img);
    } else {
        signInButton.setVisibility(View.VISIBLE);
        signOutButton.setVisibility(View.GONE);
    }
}

    //----------------------------------------------------------------------------------------------


    //----------?????????----------------------------------------------------
    private void homeSlideShow() {
        for (int i = 0; i < slides_bg.length; i++)
            slidesArrayList.add(slides_bg[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new planstory_slides_adapter(PlanStory_Card.this, slidesArrayList));
        indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == slides_bg.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        //Auto start
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 1000, 5000);
    }


    //------????????????-----------------------------------------------------

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // --START on_start_sign_in--
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null && GoogleSignIn.hasPermissions(account, new Scope(Scopes.DRIVE_APPFOLDER))) {
            updateUI(account);
        } else {
            updateUI(null);
            //--END on_start_sign_in--
        }
    }

    @Override
    protected void onResume() { //2021/01/30
        super.onResume();
        initDB();
        dbmysql();
        data_toList();
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
            case R.id.menu_end:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void u_menu_planStory_1() {
        menu_addPlan.setVisible(false);
        menu_addPlanMyStory.setVisible(false);
//        menu_logIn.setVisible(false);
//        menu_logOut.setVisible(false);
        menu_GPS_ON.setVisible(false);
        menu_GPS_OFF.setVisible(false);
        menu_back.setVisible(false);
        menu_end.setVisible(true);
    }

    //-----------------?????????RecyclerView.Adapter-------------------------------------------------------
    private class ps_card_Adapter extends RecyclerView.Adapter<ps_card_Adapter.ViewHolder> {
        private String planmystory_card_index;

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View layout_rv, layout_rv_main;
            private final ImageView ps_card_bg,Gphoto;
            private TextView dayname, daynum, Gname, releaseday, ps_card_ID; //??????????????????

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                //????????????
                dayname = itemView.findViewById(R.id.p_day_day);
                daynum = itemView.findViewById(R.id.ps_card_daynum);
                Gname = itemView.findViewById(R.id.ps_card_Gname);
                Gphoto = itemView.findViewById(R.id.img_Gphoto);
                ps_card_ID = itemView.findViewById(R.id.ps_card_id);
                releaseday = itemView.findViewById(R.id.p_day_date);
                layout_rv = itemView.findViewById(R.id.ps_card_layout02);
                layout_rv_main = itemView.findViewById(R.id.p_day_layout002);
                ps_card_bg=itemView.findViewById(R.id.ps_card_bg);

            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.planstory_card,//??????layout???????????????
                    parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            String Gphoto_URL= ps_card_arrayList.get(position).get("photo_URL");
            String layout_bg=imgArr_bg[position%(imgArr_bg.length)];
            //????????????(???arraylist??????)
            holder.ps_card_ID.setText(ps_card_arrayList.get(position).get("ps_cardID"));
            holder.dayname.setText(ps_card_arrayList.get(position).get("memory_Name"));
            holder.daynum.setText(ps_card_arrayList.get(position).get("memory_Day")+"???");
            holder.Gname.setText(ps_card_arrayList.get(position).get("google_name"));
            Glide.with(holder.Gphoto.getContext())
                    .load(Gphoto_URL)
                    .error(R.drawable.logo_triplanner)
                    .placeholder(R.drawable.logo_triplanner)
                    .fitCenter()
                    .into(holder.Gphoto);


//            holder.Gphot o.setImageResource(imgArr[position % (imgArr.length)]); //hashMap.put("photo_URL", fld[3]);
            holder.releaseday.setText(ps_card_arrayList.get(position).get("modify_Time"));
            //?????????
            Glide.with(holder.ps_card_bg.getContext())
                    .load(layout_bg)
                    .error(R.drawable.logo_triplanner)
                    .placeholder(R.drawable.logo_triplanner)
                    .fitCenter()
                    .into(holder.ps_card_bg);

//            holder.layout_rv.setBackgroundResource(imgArr_bg[position % (imgArr_bg.length)]);
            holder.layout_rv_main.setId(position);
            holder.layout_rv_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //------????????????cardID(MySQL??????ID)????????????--------2021/01/30--------
                    planmystory_card_index = holder.ps_card_ID.getText().toString().trim();
//            int ii=(v.getId());
//            String mm = String.format("%02d", ii);
                    intent.putExtra("pagefrom", "PlanStory_Card");
                    intent.putExtra("planmystory_card_index", planmystory_card_index);
                    intent.setClass(PlanStory_Card.this, PlanMystory_Show.class);
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            //??????list??????
            return ps_card_arrayList.size();
        }
    }
//---------------------------------------------------------------------------------------------
//RecyclerView?????????(??????)
//    private void recyclerviewAction_pscard(RecyclerView ps_card_reView,
//                                           final ArrayList<HashMap<String, String>> ps_card_arrayList,
//                                           final ArrayList<Map<String, Object>> ps_card_arrayList_photo,
//                                           final ps_card_Adapter ps_card_adapter){
//        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
//            @Override
//            public int getMovementFlags(@NonNull RecyclerView recyclerView,
//                                        @NonNull RecyclerView.ViewHolder viewHolder) {//???????????????RecyclerView????????????????????????
////                return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN , ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//??????????????????
//                return makeMovementFlags(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//?????????????????????
////                return makeMovementFlags(0,ItemTouchHelper.RIGHT); //???????????????
//            }
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView,
//                                  @NonNull RecyclerView.ViewHolder viewHolder,
//                                  @NonNull RecyclerView.ViewHolder target) {//??????????????????
//                //----????????????----
////                int position_dragged = viewHolder.getAdapterPosition();
////                int position_target = target.getAdapterPosition();
////                Collections.swap(ps_card_arrayList, position_dragged, position_target);
////                Collections.swap(ps_card_arrayList_photo, position_dragged, position_target);
////                ps_card_adapter.notifyItemMoved(position_dragged, position_target);
//                //---------------
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
//                                 int direction) {//??????????????????
//                int position = viewHolder.getAdapterPosition();
//                switch (direction) {
//                    case ItemTouchHelper.LEFT:
//                    case ItemTouchHelper.RIGHT:
//                        goodbye.start();
//                        //????????????
//                        ps_card_arrayList.remove(position);
//                        ps_card_arrayList_photo.remove(position);
//                        ps_card_adapter.notifyItemRemoved(position);
//                        ps_card_adapter.notifyItemRangeChanged(position,ps_card_arrayList.size()); //??????????????????
//                        ps_card_adapter.notifyItemRangeChanged(position,ps_card_arrayList_photo.size());
//                        Toast.makeText(PlanStory_Card.this,"???!?????????...",Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        });
//        helper.attachToRecyclerView(ps_card_reView);
//
//
//    }    //RecyclerView?????????(??????)----------end


    //..........
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_GET_TOKEN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);


            updateUI(account);
        } catch (ApiException e) {
            //   Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
        }
    }

    //.............................dialog.20210118

    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {


            switch (which) {
                case BUTTON_POSITIVE://
                    getIdToken();
                    break;
//                case BUTTON_NEGATIVE:
//                    ;
//
//
//                    break;
            }
        }
    };

}