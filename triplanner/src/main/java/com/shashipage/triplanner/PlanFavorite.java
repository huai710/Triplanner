package com.shashipage.triplanner;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PlanFavorite extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<HashMap<String, String>> favorArrayList = new ArrayList<>();
    private RecyclerView favor_adapterRV;
    private favorAdapter favor_adapter;
    private Button btnYes, btnNo;
    private Intent intent = new Intent();
    private Dialog confirmDelDlg;
    private MenuItem menu_addPlan, menu_addPlanMyStory, menu_logIn, menu_logOut, menu_GPS_ON, menu_GPS_OFF, menu_back,
            menu_end;
    private Toolbar toolbar;
    private Uri uri;
    // google 登入
    private Button signInButton, signOutButton;
    // +++++++++++++++++++++++++++DB相關變數+++++++++++++++++
    private static final String DB_File = "astray.db", DB_TABLE = "favorite";
    private View header;
    // ...................................headermenu
    private TextView user_name, user_email;
    private DbHelper dbHper;
    private int DBversion = DbHelper.VERSION;
    private String sqlControl;
    private ArrayList<String> favoriteList;
    private String google_login_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_main);
        setupViewComponent();
    }

    private void setupViewComponent() {
        // 設定RecyclerView
        favor_adapterRV = findViewById(R.id.favorite_rv); // 抓呈現頁面的RecyclerViewID
        favor_adapterRV.setLayoutManager(new LinearLayoutManager(this));
        favor_adapter = new favorAdapter();
        favor_adapterRV.setAdapter(favor_adapter);
        // 設定navigation drawer
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);
        // ...................................................headermenu
        header = navigationView.getHeaderView(0);
        signInButton = header.findViewById(R.id.sign_in_button);
        signOutButton = header.findViewById(R.id.sign_out_button);

        // Button listeners
        header.findViewById(R.id.sign_in_button).setOnClickListener(this);
        header.findViewById(R.id.sign_out_button).setOnClickListener(this);
        user_name = (TextView) header.findViewById(R.id.user_name);
        user_email = (TextView) header.findViewById(R.id.user_email);
        // --END customize_button--

        // 將drawerLayout和toolbar整合，會出現「三」按鈕
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // 點選時收起選單
                drawerLayout.closeDrawer(GravityCompat.START);

                // 取得選項id
                int id = item.getItemId();

                if (id == R.id.drawer_plan) {
                    intent.setClass(PlanFavorite.this, Plan_Card.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }
                if (id == R.id.drawer_planMyStory) {
                    intent.setClass(PlanFavorite.this, PlanMyStory_Card.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }

                if (id == R.id.drawer_planFavorite) {
                    return true;
                }

                if (id == R.id.drawer_planStory) {
                    intent.setClass(PlanFavorite.this, PlanStory_Card.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    return true;
                }

                if (id == R.id.drawer_search) {
                    intent.setClass(PlanFavorite.this, Search.class);
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
                    intent.setClass(PlanFavorite.this, More.class);
                    startActivityForResult(intent, 0);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
    }

    private void initDB() {
        if (dbHper == null)
            dbHper = new DbHelper(this, DB_File, null, DBversion);
        favoriteList = dbHper.getRecSet(DB_TABLE);
    }

    private void dbmysql() {
        google_login_id = dbHper.find11("google_login_id");
        sqlControl = "SELECT * FROM favorite WHERE google_login_id =" + google_login_id;
        final ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlControl);

        final ProgressDialog progDlg = new ProgressDialog(this);
        progDlg.setTitle("請稍等");
        progDlg.setMessage("讀取中......");
        progDlg.setCancelable(false);
        progDlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDlg.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String result = DBConnector.executeQuery(nameValuePairs);
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                        dbHper.clearRec(DB_TABLE); // 匯入前,刪除所有SQLite資料
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonData = jsonArray.getJSONObject(i);
                            ContentValues newRow = new ContentValues();
                            Iterator itt = jsonData.keys();
                            while (itt.hasNext()) {
                                String key = itt.next().toString();
                                String value = jsonData.getString(key); // 取出欄位的值
                                if (value == null) {
                                    continue;
                                } else if ("".equals(value.trim())) {
                                    continue;
                                } else {
                                    jsonData.put(key, value.trim());
                                }
                                newRow.put(key, value); // 動態找出有幾個欄位
                            }
                            dbHper.insertRec_m(newRow, DB_TABLE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "主機資料庫無資料(code:" + DBConnector.httpstate + ") ",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbHper.getRecSet(DB_TABLE); // 重新載入SQLite
                    favoriteList = dbHper.getRecSet10(DB_TABLE, "creation_time", "DESC");
                    favorArrayList.clear();
                    for (int i = 0; i < favoriteList.size(); i++) {
                        String[] fld = favoriteList.get(i).split("#");
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("attraction_id", fld[3]);
                        hashMap.put("attraction_name", fld[5]);
                        hashMap.put("attraction_address", fld[7]);
                        hashMap.put("longitude", fld[8]);
                        hashMap.put("latitude", fld[9]);
                        hashMap.put("creation_time", fld[12]);
                        hashMap.put("tbd1", fld[16]); // 景點圖片放table預留格1
                        favorArrayList.add(hashMap);
                    }
                } catch (Exception e) {
                } finally {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setupViewComponent();
                            progDlg.cancel();
                        }
                    });
                }
            }
        }).start();
    }

    private void mysql_del(String id) {
        google_login_id = dbHper.find11("google_login_id");
        String attraction_id = id;
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(google_login_id);
        nameValuePairs.add(attraction_id);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DBConnector.executeFavoriteDelete(nameValuePairs);
    }

    // ++++++++++++++++++++++++++RecyclerView.Adapter++++++++++++++++++++++++++++++++++
    private class favorAdapter extends RecyclerView.Adapter<favorAdapter.ViewHolder> {
        public class ViewHolder extends RecyclerView.ViewHolder {
            private View favorLayoutImg;
            private TextView favorLocationName;
            private ImageView favorLayoutImgView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                favorLocationName = itemView.findViewById(R.id.favor_place);
                favorLayoutImg = itemView.findViewById(R.id.favor_subLayout);
                favorLayoutImgView = itemView.findViewById(R.id.favor_imgView);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_card, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            String imgURL = favorArrayList.get(position).get("tbd1");
            holder.favorLocationName.setText(favorArrayList.get(position).get("attraction_name"));
            if (imgURL.length() != 0) {
                CircularProgressDrawable drawable = new CircularProgressDrawable(PlanFavorite.this);
                drawable.setColorSchemeColors(Color.argb(1, 	252, 235, 167)
                        ,Color.argb(1,249, 169, 47),Color.argb(1,	249, 119, 47));
                drawable.setCenterRadius(30f);
                drawable.setStrokeWidth(5f);
                drawable.setStyle(CircularProgressDrawable.LARGE);
                // set all other properties as you would see fit and start it
                drawable.start();

                Glide.with(getApplicationContext()).load(imgURL).placeholder(drawable)
                        .error(R.drawable.card_triplanner)
                        .into(holder.favorLayoutImgView);

            } else {
                holder.favorLayoutImgView.setImageResource(R.drawable.card_triplanner);
            }
            holder.favorLayoutImg.setId(position);
            holder.favorLayoutImg.setOnClickListener(cardClk); //
            holder.favorLayoutImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) { // 設長按跳出確認刪除之視窗
                    confirmDelDlg = new Dialog(PlanFavorite.this);
                    confirmDelDlg.setTitle(R.string.login);
                    confirmDelDlg.setCancelable(false);

                    confirmDelDlg.setContentView(R.layout.favorite_del_dlg);
                    btnYes = (Button) confirmDelDlg.findViewById(R.id.yes_btn);
                    btnNo = (Button) confirmDelDlg.findViewById(R.id.no_btn);
                    btnYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { // dialog確認刪除會同時移除資料庫與畫面上的景點
                            int position_list = holder.getAdapterPosition();// 刪除資料庫中對應之資料
                            String attraction_id = dbHper.deleteFavorite10(position_list);
                            mysql_del(attraction_id);
                            favorArrayList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, getItemCount());
                            confirmDelDlg.cancel();// 關閉dialog
                        }
                    });
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDelDlg.cancel();
                        }
                    });
                    confirmDelDlg.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return favorArrayList.size();
        }
    }

    // +++++++++++++++++++++++++點擊卡片跳外部INTENT的監聽+++++++++++++++
    private View.OnClickListener cardClk = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int i = (v.getId());
            String place = favorArrayList.get(i).get("attraction_name"); // 以景點名稱搜尋
            Uri mapUri = Uri.parse("geo:23.9037,121.0794?q=" + place);
            Intent it = new Intent(Intent.ACTION_VIEW, mapUri);
            startActivity(it);
        }
    };

    private void u_menu_pf1() {
        menu_addPlan.setVisible(false);
        menu_addPlanMyStory.setVisible(false);
//        menu_logIn.setVisible(false);
//        menu_logOut.setVisible(false);
        menu_GPS_ON.setVisible(false);
        menu_GPS_OFF.setVisible(false);
        menu_back.setVisible(true);
        menu_end.setVisible(false);
    }

    // ----------------------------------------------------------------------------------------------

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ------生命週期-----------------------------------------------------
    @Override
    public void onBackPressed() { // 返回鍵
//        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDB();
        dbmysql();
        headerLayout(); // 重新抓使用者資料
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu_addPlan = menu.findItem(R.id.menu_addPlan);
        menu_addPlanMyStory = menu.findItem(R.id.menu_addPlanMyStory);
//        menu_logIn = menu.findItem(R.id.menu_logIn);
//        menu_logOut = menu.findItem(R.id.menu_logOut);
        menu_GPS_ON = menu.findItem(R.id.menu_GPS_ON);
        menu_GPS_OFF = menu.findItem(R.id.menu_GPS_OFF);
        menu_back = menu.findItem(R.id.menu_back);
        menu_end = menu.findItem(R.id.menu_end);
        u_menu_pf1();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_back:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ................................................................
    private void signOut() {
        DbHelper dbHper = new DbHelper(this, DB_File, null, DBversion);
        dbHper.clearRec11();
        dbHper.close();
        GoogleSignInClient mGoogleSignInClient = Loginuser.loginmod(this);
        mGoogleSignInClient.signOut().addOnCompleteListener(PlanFavorite.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                user_email.setText("");
                user_name.setText("");
                CircleImgView aa = (CircleImgView) header.findViewById(R.id.img_Gphoto_login);
                aa.setImageResource(R.drawable.logo_triplanner);

            }
        });
        Intent intent = new Intent(this, PlanStory_Card.class);
        startActivity(intent);
    }

    public void headerLayout() {

        if (dbHper == null) {
            dbHper = new DbHelper(PlanFavorite.this, "astray.db", null, DBversion);
        }
        String u_email = dbHper.find11("email");
        String u_name = dbHper.find11("nickname");
        String u_pic = dbHper.find11("profile_picture");
        boolean login_status = dbHper.status11();
        if (login_status) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
            user_name.setText(u_name);
            user_email.setText(u_email);

        } else {
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
            CircleImgView cc = (CircleImgView) header.findViewById(R.id.img_Gphoto_login);
            cc.setImageResource(R.drawable.logo_triplanner);
            user_name.setText("");
            user_email.setText("");
        }

        Uri user_uri;
        if (u_pic != null) {
            user_uri = Uri.parse(u_pic);
        } else {
            CircleImgView cc = header.findViewById(R.id.img_Gphoto_login);
            cc.setImageResource(R.drawable.logo_triplanner);
            cc.invalidate();
            user_uri = null;
        }

        if (user_uri != null) {
            CircleImgView cc = header.findViewById(R.id.img_Gphoto_login);
            Glide.with(this).load(user_uri).placeholder(R.drawable.logo_triplanner).into(cc);
        }
        dbHper.close();
    }
}
