package com.shashipage.triplanner;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.content.DialogInterface.BUTTON_POSITIVE;

public class Search extends AppCompatActivity implements View.OnClickListener {
    private ListView listView;
    private String attraction_id, attraction_name, district, attraction_address, longitude, latitude;
    private JSONArray jsonArray;
    private List<Map<String, Object>> mList;
    private Intent intent = new Intent();
    private String check_t = null;
    private MenuItem menu_addPlan, menu_addPlanMyStory, menu_logIn, menu_logOut, menu_GPS_ON, menu_GPS_OFF, menu_back, menu_end;
    private Toolbar toolbar;
    private View header;
    private Uri uri, User_IMAGE;
    private CircleImgView img;
    private ImageView img0101;
    //google ??????
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;
    private Button signInButton, signOutButton;
    private TextView user_name, user_email;
    //Handler
//    private Handler handler = new Handler();
    //SQLite
    private static final String DB_FILE = "astray.db";//???????????????
    private static final String DB_TABLE = "attraction";//???????????????
    private static final int DBversion = DbHelper.VERSION;//??????
    private DbHelper dbHper;
    private ArrayList<String> recSet;
    private String sqlctl;
    private Spinner mSpntbd1,mSpndistrict;
    private SQLiteDatabase mItDb;
    private GoogleSignInClient mGoogleSignInClient;
    private String spindistrict;
    private ArrayList<Map<String, Object>> spinner;//??????????????????????????????Listview
    private Handler mHandler=new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //=====MySQL===
        enableStrictMode(this);
        //=============
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
//        initDB();//??????????????????????????????????????????????????????
        setupViewComponent();
    }

    //???????????????(MySQL),??????????????????
//*****************************************************************************
    private void enableStrictMode(Context context) {
        //-------------????????????????????????????????????------------------------------
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

    //****************************************************************************
    private void initDB() {
        if (dbHper == null) {
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);

        }
        recSet = dbHper.getRecSet01s();
    }

    private void setupViewComponent() {
        initDB();//??????????????????????????????????????????????????????
        if (dbHper.recCount01s() != 0) {//????????????????????????
//            u_list();//???SQLite???list
            u_spinner();
        } else {
            u_showdata();//???MYSQL???????????????SQLite
        }
        //------------------------------------------------------------------------------------------
        //??????navigation drawer
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // ???toolbar??????APP???ActionBar
        setSupportActionBar(toolbar);
        header = navigationView.getHeaderView(0);
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
//        headerLayout();

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
//                DbHelper userdb = new DbHelper(Search.this, DB_FILE, null, DBversion);
                Boolean status = dbHper.status11();
//                headerLayout();
//                userdb.close();



                // ????????????id
                int id = item.getItemId();
//                int a=1; //???????????????????????????
//                if(a == 1){
                if (status) {
                    // ??????id??????????????????????????????????????????

                    if (id == R.id.drawer_planMyStory) {
                        intent.setClass(Search.this, PlanMyStory_Card.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        return true;
                    }

                    if (id == R.id.drawer_planFavorite) {
                        intent.setClass(Search.this, PlanFavorite.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        return true;
                    }
                    if (id == R.id.drawer_plan) {
                        intent.setClass(Search.this, Plan_Card.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                        return true;
                    }



                } else if (id == R.id.drawer_planFavorite || id == R.id.drawer_planMyStory ||
                        id == R.id.drawer_plan ) {

                    MyAlertDialog aldDial = new MyAlertDialog(Search.this);
                    aldDial.setTitle("????????????");
                    aldDial.setMessage("?????????????????????????????????");
                    aldDial.setIcon(R.drawable.logo_triplanner);
                    aldDial.setCancelable(true); //???????????????
                    aldDial.setButton(BUTTON_POSITIVE, "????????????", aldBtListener);
                    //  aldDial.setButton(BUTTON_NEGATIVE, "??????", aldBtListener);
                    aldDial.show();

                }
                if (id == R.id.drawer_planStory) {
                    intent.setClass(Search.this, PlanStory_Card.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                    return true;
                }


                if (id == R.id.drawer_search) {
//                    intent.setClass(Search.this, Search.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivity(intent);
                    return true;
                }

                if (id == R.id.drawer_facebook) {
                    uri = Uri.parse("https://www.facebook.com/Astary%E6%97%85%E9%81%8AApp-105248678147992");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.action_settings) {
                    intent.setClass(Search.this, More.class);
                    startActivityForResult(intent, 0);
                    return true;
                }

                return false;
            }
        });
        //-------------------------------------------------------
        // ?????????????????? ????????????????????????
        //        listview
        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int newscrollheight = displayMetrics.heightPixels * 50 / 100; // ??????ScrollView????????????
        listView = (ListView) findViewById(R.id.listview);
        listView.getLayoutParams().height = newscrollheight;
        listView.setLayoutParams(listView.getLayoutParams()); // ??????ScrollView??????
//        img0101
        DisplayMetrics displayMetrics01 = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics01);
        int newscrollheight01 = displayMetrics.heightPixels * 30 / 100; // ??????ScrollView????????????
        img0101 = (ImageView) findViewById(R.id.img0101);
        img0101.getLayoutParams().height = newscrollheight01;
        img0101.setLayoutParams(img0101.getLayoutParams()); // ??????ScrollView??????
    }
    private void u_spinner() {
        //-------????????????--------------
        mSpntbd1=(Spinner)findViewById(R.id.spinner);//??????
        mSpndistrict = (Spinner) findViewById(R.id.spinner1);//??????
        List<String>spinnertbd1=dbHper.gettbd1();//???SQLite???????????????????????????spinnertbd1
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item);
        adapter.addAll(spinnertbd1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpntbd1.setAdapter(adapter);
        mSpntbd1.setOnItemSelectedListener(mSpnNameOnItemSelLis);//??????spinner
        mSpndistrict.setOnItemSelectedListener(mSpnDistrictOnItemSelLis);//??????spinner
    }
    //??????spinner
    private AdapterView.OnItemSelectedListener mSpnNameOnItemSelLis = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            spindistrict = parent.getSelectedItem().toString();

            List<String>spinnerdistrict=dbHper.getdistrict(spindistrict);
            ArrayAdapter<String> adapter=new  ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item);
            adapter.addAll(spinnerdistrict);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpndistrict.setAdapter(adapter);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    //??????spinner
    private AdapterView.OnItemSelectedListener mSpnDistrictOnItemSelLis=new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String sss=parent.getSelectedItem().toString();
            spinner=dbHper.getAll(spindistrict,sss);

            MyAdapter adapter=new MyAdapter(
                    getApplicationContext(),
                    spinner,
                    R.layout.search_list_item,
                    new String[]{"attraction_name"},
                    new int[]{R.id.txt02}
            );
            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(listViewOnItemClickLis);
            listView.setOnItemLongClickListener(listViewOnItemClickLis01);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {

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

    //???SQLite???list
//    private void u_list() {
//        mList = new ArrayList<Map<String, Object>>();
//        for (int i = 0; i < recSet.size(); i++) {
//            Map<String, Object> item = new HashMap<String, Object>();
//            String[] fld = recSet.get(i).split("#");
//            item.put("attraction_id", fld[2]);//id
//            item.put("district", fld[3]);//??????
//            item.put("attraction_name", fld[4]);//??????
//            item.put("longitude", fld[7]);//??????
//            item.put("latitude", fld[8]);//??????
//            item.put("attraction_address", fld[6]);//??????
//            item.put("phone_number", fld[12]);//??????
//            item.put("attraction_picture",fld[14]);//??????
//            item.put("tbd1",fld[20]);//??????
//            mList.add(item);
//        }
//        SimpleAdapter adapter = new SimpleAdapter(
//                this,
//                mList,
//                R.layout.search_list_item,
//                new String[]{"district", "attraction_name"},
//                new int[]{R.id.txt01, R.id.txt02}
//        );
//        listView.setAdapter(adapter);
//        listView.setTextFilterEnabled(true);
//        listView.setOnItemClickListener(listViewOnItemClickLis);
//    }

    //???MYSQL???????????????SQLite
    private void u_showdata() {
        dbmysql();
        //?????????
        try {
            Thread.sleep(100); //1000???1???
        } catch (InterruptedException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
//        u_list();
        u_spinner();
    }

    private void dbmysql() {
        //---------Progress?????????--------------------------------------------------------
        final ProgressDialog progDlg = new ProgressDialog(this);
        progDlg.setTitle("?????????");
        progDlg.setMessage("?????????......");
        progDlg.setIcon(R.drawable.ic_time);
        progDlg.setCancelable(true);
        progDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progDlg.setMax(100);
        progDlg.show();
        //---------------------------------------------------------------------------
        sqlctl = " SELECT * FROM `attraction` WHERE 1 ";
        final ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = DBConnector.executeQuery(nameValuePairs);

                    /**************************************************************************
                     * SQL ??????????????????????????????JSONArray
                     * ?????????????????????????????????JSONObject?????? JSONObject
                     * jsonData = new JSONObject(result);
                     **************************************************************************/
                    JSONArray jsonArray=null;
                    try {
                        jsonArray = new JSONArray(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    JSONArray jsonArray = new JSONArray(result);
                    // -------------------------------------------------------
                    if (jsonArray.length() > 0) { // MySQL ?????????????????????
                        progDlg.setMax(jsonArray.length()+1);

                        int rowsAffected = dbHper.clearRec01s();                 // ?????????,????????????SQLite??????

                        // ??????JASON ????????????????????????
                        for (int i = 0; i < jsonArray.length(); i++) {
                            final int a = i;
                            JSONObject jsonData=null;
                            try {
                                jsonData = jsonArray.getJSONObject(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
//                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            ContentValues newRow = new ContentValues();
                            // --(1) ?????????????????? --?????? jsonObject ????????????("key","value")-----------------------
                            Iterator itt = jsonData.keys();
                            while (itt.hasNext()) {
                                String key = itt.next().toString();
                                String value=null;
                                try {
                                    value = jsonData.getString(key);// ??????????????????
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
//                                String value = jsonData.getString(key); // ??????????????????
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
//                                    jsonData.put(key, value.trim());
                                }
                                // ------------------------------------------------------------------
                                newRow.put(key, value.toString()); // ???????????????????????????
                                // -------------------------------------------------------------------
                            }
                            // ---(2) ????????????????????????---------------------------
                            // newRow.put("id", jsonData.getString("id").toString());
                            // newRow.put("name",
                            // jsonData.getString("name").toString());
                            // newRow.put("grp", jsonData.getString("grp").toString());
                            // newRow.put("address", jsonData.getString("address")
                            // -------------------??????SQLite---------------------------------------
                            long rowID = dbHper.insertRec_m01s(newRow);
                            mHandler.post(new Runnable() {
                                public void run() {
                                    progDlg.setProgress(a); //?????????????????? +1
                                }
                            });
//                    Toast.makeText(getApplicationContext(), "????????? " + Integer.toString(jsonArray.length() ) + " ?????????", Toast.LENGTH_SHORT).show();
                        }
                        // ---------------------------
                    } else {
//                Toast.makeText(getApplicationContext(), "????????????????????????", Toast.LENGTH_LONG).show();
                    }
                    recSet = dbHper.getRecSet01s();  //????????????SQLite
                    handler.sendEmptyMessage(0); // ?????????????????????????????????
                    //-------?????????????????????------
                    progDlg.cancel();
                    // --------------------------------------------------------
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
                    u_spinner();
                    break;
                default:
                    //????????????????????? do something.....
                    break;
            }
        }
    };

    //------------------------------------------------------------------------------------
    AdapterView.OnItemClickListener listViewOnItemClickLis = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent it = new Intent();
            it.setClass(Search.this, Search_content.class);
            Bundle bundle = new Bundle();
            //??????
            bundle.putString("attraction_id", spinner.get(position).get("attraction_id").toString());//id
            bundle.putString("attraction_name", spinner.get(position).get("attraction_name").toString());//??????
            bundle.putString("district", spinner.get(position).get("district").toString());//??????
            bundle.putString("attraction_address", spinner.get(position).get("attraction_address").toString());//??????
            bundle.putString("longitude", spinner.get(position).get("longitude").toString());//??????
            bundle.putString("latitude", spinner.get(position).get("latitude").toString());//??????
            bundle.putString("phone_number", spinner.get(position).get("phone_number").toString());//??????
            bundle.putString("attraction_picture", spinner.get(position).get("attraction_picture").toString());//??????
            bundle.putString("tbd1", spinner.get(position).get("tbd1").toString());//??????

            it.putExtras(bundle);
            startActivity(it);

        }
    };
    AdapterView.OnItemLongClickListener listViewOnItemClickLis01=new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            ImageView img0101 = (ImageView) findViewById(R.id.img0101);
            String img_url=spinner.get(position).get("attraction_picture").toString();
//            Picasso.get().load(url).placeholder(R.drawable.loading_anim).error(R.drawable.img_ns).into(img0101);
            img_url=utf8Togb2312(img_url).replace("http://", "https://");

            CircularProgressDrawable drawable = new CircularProgressDrawable(Search.this);
            drawable.setColorSchemeColors(Color.argb(1, 	252, 235, 167)
                    ,Color.argb(1,249, 169, 47),Color.argb(1,	249, 119, 47));
            drawable.setCenterRadius(30f);
            drawable.setStrokeWidth(5f);
            drawable.setStyle(CircularProgressDrawable.LARGE);
            // set all other properties as you would see fit and start it
            drawable.start();

            Glide.with(getApplicationContext())
                    .load(img_url)
                    .placeholder(drawable)
                    .error(R.drawable.logo_triplanner)
                    .into(img0101);
            TextView txt0101 = (TextView) findViewById(R.id.txt0101);
            txt0101.setText(spinner.get(position).get("attraction_name").toString());
            return true;
        }
    };
    private String utf8Togb2312(String img_url) {
        String r_data = "";
        try {
            for (int i = 0; i < img_url.length(); i++) {
                char ch_word = img_url.charAt(i);
//            ??????????????????????????????:????????????????????????
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
    //------menu--------------
    private void u_menu_search() {
        menu_addPlan.setVisible(false);
        menu_addPlanMyStory.setVisible(false);
//        menu_logIn.setVisible(false);
//        menu_logOut.setVisible(false);
        menu_GPS_ON.setVisible(false);
        menu_GPS_OFF.setVisible(false);
        menu_back.setVisible(true);
        menu_end.setVisible(false);
    }

    //----------------------------------------------------------------------------------------------
    //draw_header??????


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

    //................................................................headermenu



    //???????????????
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        headerLayout();  //????????????????????????
    }

    //menu
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

        u_menu_search();//??????????????? 236???
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
//            case R.id.menu_GPS_ON:
//                Toast.makeText(getApplicationContext(),getString(R.string.search_gpson),Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.menu_GPS_OFF:
//                Toast.makeText(getApplicationContext(),getString(R.string.search_gpsoff),Toast.LENGTH_SHORT).show();
//                break;
            case R.id.menu_back:
                this.finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    //ListView???img????????????
    public class MyAdapter extends SimpleAdapter{
        public MyAdapter(Context applicationContext, ArrayList<Map<String, Object>> spinner, int search_list_item, String[] strings, int[] ints) {
            super(applicationContext,spinner,search_list_item,strings,ints);
        }
        public View getView(int position, View convertView, ViewGroup parent){
            View v=super.getView(position,convertView,parent);

            ImageView img_view=(ImageView)v.getTag();
            if (img_view==null){
                img_view=(ImageView)v.findViewById(R.id.circleImgView3);
                v.setTag(img_view);
            }
            //get the URL from the data in list
            String img_url=spinner.get(position).get("attraction_picture").toString().trim();
//            Picasso.get().load(img_URL)
//                    .placeholder(R.drawable.astary_logo)
//                    .error(R.drawable.img_ns)
////                .centerCrop()
//                    .into(img_view);
            img_url=utf8Togb2312(img_url).replace("http://", "https://");
            Glide.with(getApplicationContext())
                    .load(img_url)
                    .placeholder(R.drawable.logo_triplanner)
                    .error(R.drawable.logo_triplanner)
                    .into(img_view);
            return v;
        }
    }
    //................
    //0225
    //???????????????
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
            updateUI(account);
        } catch (ApiException e) {
            //   Log.w(TAG, "handleSignInResult:error", e);
            updateUI(null);
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

    private void signOut() {
//        DbHelper dbHper = new DbHelper(this, DB_FILE, null, DBversion);
        String userid=dbHper.find11("google_login_id");

        dbHper.clearRec11();

        GoogleSignInClient mGoogleSignInClient = Loginuser.loginmod(this);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(Search.this, new OnCompleteListener<Void>() {
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
            Boolean status = dbHper.status11();
            if(status==false){
                Loginuser.userdata(account,this);
            }

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
    //......................................................................................................................
}