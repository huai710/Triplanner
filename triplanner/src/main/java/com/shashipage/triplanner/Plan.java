package com.shashipage.triplanner;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shashipage.triplanner.DbHelper.timeFormat08;
import static java.lang.String.format;

public class Plan extends AppCompatActivity {
    public static int dddDay;
    private List<Plan_plan> mDatas = new ArrayList<>();
    Plan_r000 adapter = new Plan_r000(mDatas, R.layout.plan_r000, this);
    SimpleAdapter dadapter;
    Map<String, Object> item;
    // List 用來裝Map內的所有訊息
    List<Map<String, Object>> items;
    private RecyclerView recyclerView;
    private EditText date_choose;
    private ImageView toMap, addD,planEdit;
    private TextView pName;
    private TextView[] dDay = new TextView[100];
    AlertDialog dialog;
    int dnum = 1, i = 2;
    // 將輸入的資料變成物件
    // Adapter 用來連接List與GridView
//    r001_Adapter r001_adapter = new r001_Adapter();
    // GridView 用來盛裝adapter的內容
    LinearLayout l001;
    private final Intent intent = new Intent();
    private RecyclerView r001RecView;
    private boolean aaa = true;
    private String[] locationList;
    private int int_yy, int_mm, int_dd;
    Plan_plan bookShelf1[] = new Plan_plan[100];
    private String  PlanDay;
    public static String PlanName;
    private GridView planhs;
    private float density;
    private int length;
    private MenuItem menu_addPlan,menu_addPlanMyStory,menu_logIn,menu_logOut,menu_GPS_ON,menu_GPS_OFF,menu_back,menu_end;
    private Toolbar toolbar;
    private static final String DB_File="astray.db";
    private DbHelper db=null;
    private SQLiteDatabase mAstrayDb;
    private Calendar cc = Calendar.getInstance();
    private boolean planEditT=false;
    private int DBversion = DbHelper.VERSION;
    private int planId;
    public static ProgressBar p_cycle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan);
        db=new DbHelper(getApplicationContext(),DB_File,null,DBversion);
        setupViewComponent();
    }

    private void setupViewComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        // 用toolbar做為APP的ActionBar
        setSupportActionBar(toolbar);
        Intent it =getIntent();
        planId = Integer.parseInt(it.getStringExtra("plan_card_index"));
        mDatas.clear();
        pName = findViewById(R.id.plan_t001);
        recyclerView = (RecyclerView) findViewById(R.id.plan_grid);
        p_cycle = findViewById(R.id.plan_cycle);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);

        recyclerView.setAdapter(adapter);
        DisplayMetrics dm = new DisplayMetrics(); //找出使用者手機的寬高
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        density = dm.density;
        length = 83;


        toMap = findViewById(R.id.plan_toMap);
        toMap.setOnClickListener(toMapOn);
        planEdit = findViewById(R.id.plan_edit);
        planEdit.setOnClickListener(planEditOn);
        addD = findViewById(R.id.plan_addD);
        addD.setOnClickListener(addDOn);

        items = new ArrayList<>();
        dadapter = new SimpleAdapter(this, items, R.layout.plan_d
                , new String[]{"aa"}, new int[]{R.id.plan_d_b});
        planhs = findViewById(R.id.plan_hs);
        planhs.setAdapter(dadapter);
        if(planId == 0){
            addNewPlanDlg();
        }

        else{
            getExistPlan(planId);
        }

    }

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

    private void getExistPlan(int b_id) {
        int ditem=0;
        String[] getPlan = db.DB_ReadPlan08(b_id);

        dnum = Integer.parseInt(getPlan[0]);
        int gridviewWidth = (int) (dnum * (length + 4) * density * 0.9); //整個水平選單的寬度
        int itemWidth = (int) (length * density * 0.9); //每個縮圖站的寬度
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
        PlanName = getPlan[1];
        PlanName += "#" + b_id;
        adapter.planName = PlanName;
        pName.setText(PlanName.split("#")[0]);
        cc.setTime( Timestamp.valueOf(getPlan[2]));
        adapter.setPlan_c(cc);
        planhs.setLayoutParams(params);
        planhs.setColumnWidth(itemWidth);
        planhs.setNumColumns(dnum); //
        for (int i = 1; i <= dnum; i++) {
            int pointN=0;
            bookShelf1[i] = new Plan_plan();
            String[] books1 = new String[]{};
            bookShelf1[i].setBooks(Arrays.asList(books1));
            adapter.notifyDataSetChanged();
            //
            while(true){
                List<String> a = bookShelf1[i].getBooks();
                String[] getPoint = db.DB_ReadPoint08(b_id,pointN,i);
                if(getPoint==null) break;
                pointN++;
                String[] array = a.toArray(new String[0]);
                List<String> b = new ArrayList<>();
                for(String s:array){
                    b.add(s);
                }
                b.add(getPoint[3]+"#"+getPoint[0]+"#"+getPoint[1]+"#"+getPoint[4]);
                bookShelf1[i].setBooks(b);
            }
            adapter.notifyDataSetChanged();
            mDatas.add(bookShelf1[i]);
            ditem++;
            bookShelf1[i].setName("第" + ditem + "天");
            item = new HashMap<>();
            item.put("aa","第" + ditem + "天");
            items.add(item);
        }
        dadapter.notifyDataSetChanged();
        planhs.setOnItemClickListener(planhsOn);
        planhs.setOnItemLongClickListener(planhsOnL);
    }

    AdapterView.OnItemLongClickListener planhsOnL = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,final int position, long id) {

            return true;
        }
    };

    AdapterView.OnItemClickListener planhsOn = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            if(!planEditT){
                recyclerView.smoothScrollToPosition(position);
            }
            else {
                if (mDatas.size() <= 1) {
                    Toast.makeText(getApplicationContext(), "剩一天不要再刪啦", Toast.LENGTH_SHORT).show();
                    return;
                }
                //跳出取消dialog
                AlertDialog.Builder dlg_delet = new AlertDialog.Builder(view.getContext());
                //設定dialog
                dlg_delet.setTitle(dlg_delet.getContext().getString(R.string.dlg_deleteTitle));
                dlg_delet.setMessage(dlg_delet.getContext().getString(R.string.dlg_deleteMessage));
                dlg_delet.setIcon(R.drawable.ic_delete);
                dlg_delet.setCancelable(false);
                //設定按鈕
                dlg_delet.setPositiveButton(dlg_delet.getContext().getString(R.string.dlg_delete_positive), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        mDatas.remove(position);
                        adapter.notifyDataSetChanged();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                db.DelDay08(PlanName, position + 1);
                            }
                        }).start();

//            dnum--;
                        items.remove(position);
                        for (int i = 0; i < items.size(); i++) {
                            Map<String, Object> aaa = new HashMap<>();
                            aaa.put("aa", "刪除\n第" + (i + 1) + "天");
                            items.set(i, aaa);
                        }
                        dadapter.notifyDataSetChanged();
                        dnum--;
                        int aaa = 0;
                    }
                });
                dlg_delet.setNeutralButton(dlg_delet.getContext().getString(R.string.dlg_delete_neutral), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg_delet.show();
            }
        }
    };

    private final View.OnClickListener planEditOn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!planEditT){
                for(int i=0;i<items.size();i++){
                    Map<String, Object> aaa=new HashMap<>();
                    aaa.put("aa","刪除\n第" + (i+1) + "天");
                    items.set(i,aaa);
                }
            }
            else{
                for(int i=0;i<items.size();i++){
                    Map<String, Object> aaa=new HashMap<>();
                    aaa.put("aa","第" + (i+1) + "天");
                    items.set(i,aaa);
                }
            }
            dadapter.notifyDataSetChanged();
            planEditT=!planEditT;
        }
    };

    private final View.OnClickListener toMapOn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intenta = new Intent();
            intenta.putExtra("planid",planId);
            intenta.setClass(Plan.this, Plan_map.class);
            startActivity(intenta);
        }
    };

    private final View.OnClickListener addDOn = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if(dnum>=99){
                Toast.makeText(v.getContext(),"天數最大為99",Toast.LENGTH_SHORT).show();
                return;
            }
            dnum++;
            int gridviewWidth = (int) (dnum * (length + 4) * density * 0.9); //整個水平選單的寬度
            int itemWidth = (int) (length * density * 0.01); //每個縮圖站的寬度
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
            bookShelf1[dnum] = new Plan_plan();
            String[] books1 = new String[]{};
            bookShelf1[dnum].setBooks(Arrays.asList(books1));
            mDatas.add(bookShelf1[dnum]);
            adapter.notifyDataSetChanged();
            planhs.setLayoutParams(params);
            planhs.setColumnWidth(itemWidth);
            planhs.setNumColumns(dnum); //
            item = new HashMap<>();
            items.add(item);
            for(int i=0;i<items.size();i++){
                Map<String, Object> aaa=new HashMap<>();
                aaa.put("aa","第" +( i +1)+ "天");
                items.set(i,aaa);
            }
            dadapter.notifyDataSetChanged();

        }
    };

    //---------RecyclerView.Adapter結束---------------------------------------
    private void addNewPlanDlg() {
        //跳出自訂義Dialog(新增行程)
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Plan.this);
        View dailog = getLayoutInflater().inflate(R.layout.plan_addnew, null); //取得自訂義layout
        alertDialog.setView(dailog);
        Button btnOK = dailog.findViewById(R.id.p_addnew_ok_btn); //建立按鈕
        Button btnC = dailog.findViewById(R.id.p_addnew_cancel_btn); //取消按鈕
        date_choose = (EditText) dailog.findViewById(R.id.p_addnew_startdate_edtext); //日期選擇
        final EditText addplan_name = (EditText) dailog.findViewById(R.id.p_addnew_planname_edtext); //行程名稱
        final EditText addplan_day = (EditText) dailog.findViewById(R.id.p_addnew_day_edtext); //天數
        final AlertDialog dialog_create = alertDialog.create();
        dialog_create.setCancelable(false);
        dialog_create.show();
        dialog_create.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //dlg背景全透明


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String b_id="";
                //取得edittext值
                cc.set(int_yy,int_mm,int_dd);
                adapter.setPlan_c(cc);
                PlanDay ="0"+addplan_day.getText().toString();
                PlanName = addplan_name.getText().toString();
                dnum = Integer.parseInt(PlanDay);
                if(!PlanName.equals("")){
                    if(dnum!=0){
                        if(dnum<100){
                            int gridviewWidth = (int) (dnum * (length + 4) * density * 0.9); //整個水平選單的寬度
                            int itemWidth = (int) (length * density * 0.9); //每個縮圖站的寬度
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);
                            b_id = db.DB_NewPlan08(PlanName,dnum,cc);
                            PlanName = addplan_name.getText().toString() + "#" + b_id;
                            adapter.planName = PlanName;
                            pName.setText(PlanName.split("#")[0]+"\n"+ timeFormat08(cc));
                            planhs.setLayoutParams(params);
                            planhs.setColumnWidth(itemWidth);
                            planhs.setNumColumns(dnum); //
                            for (int i = 1; i <= dnum; i++) {
                                bookShelf1[i] = new Plan_plan();
                                bookShelf1[i].setName("第" + i + "天");
                                String[] books1 = new String[]{};
                                bookShelf1[i].setBooks(Arrays.asList(books1));
                                mDatas.add(bookShelf1[i]);
                                adapter.notifyDataSetChanged();
                                item = new HashMap<>();
                                item.put("aa","第" + i + "天");
                                item.put("date",cc);
                                items.add(item);

                            }
                            dadapter.notifyDataSetChanged();
                            planhs.setOnItemClickListener(planhsOn);
                            planhs.setOnItemLongClickListener(planhsOnL);
                            dialog_create.cancel(); //關閉Dialog
                        }
                        else Toast.makeText(getApplicationContext(),"天數最大為99",Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(getApplicationContext(),"天數最小為1",Toast.LENGTH_SHORT).show();

                }
                else Toast.makeText(getApplicationContext(),"欄位不得空白",Toast.LENGTH_SHORT).show();


            }
        });
        btnC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_create.cancel(); //關閉Dialog
                Plan.this.finish();
            }
        });
        date_choose.setInputType(InputType.TYPE_NULL); //取消鍵盤跳出
        date_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //選擇日期Dialog
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog date_Cdia = new DatePickerDialog(Plan.this,
                        datePicOnClk,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));
//                        date_Cdia.setTitle(getString(R.string.p_card_datestart));
                date_Cdia.setCancelable(true);
//                        date_Cdia.setCanceledOnTouchOutside(true);
                date_Cdia.show();
            }
        }); //點擊選擇日期
        date_choose.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Calendar calendar = Calendar.getInstance();
                    DatePickerDialog date_Cdia = new DatePickerDialog(Plan.this,
                            datePicOnClk,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH));
//                            date_Cdia.setTitle(getString(R.string.p_card_datestart));
                    date_Cdia.setCancelable(true);
                    date_Cdia.show();
                }
            }
        });  //選擇日期(onfocus)

    }

    //選擇日期
    private DatePickerDialog.OnDateSetListener datePicOnClk = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            date_choose.setText((year) + getString(R.string.addN_yy) + ((month) + 1) + getString(R.string.addN_mm) + (dayOfMonth) + getString(R.string.addN_dd));
            int_yy = year;
            int_mm = month;
            int_dd = dayOfMonth;
        }
    };

    private void u_menu_p1() {
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
        u_menu_p1();
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
}


class Plan_r000 extends RecyclerView.Adapter<Plan_r000.ParentViewHolder> {
    String planName="";
    private String[] locationList;
    private AlertDialog dialog;
    private List<Plan_plan> mDatas;
    private int mLayoutId;
    private Context mContext;
    private static final String DB_File="astray.db";
    private DbHelper db=null;
    private SQLiteDatabase mAstrayDb;
    private Calendar r000c = null;
    private String sd = "行程開始時間";
    private int DBversion = DbHelper.VERSION;
    private ArrayList<String> favoriteList = new ArrayList<>();

    public Plan_r000(List<Plan_plan> bookList, int layoutId, Context context) {
        mDatas = bookList;
        mLayoutId = layoutId;
        mContext = context;
    }

    public void setPlan_c(Calendar b_c){
        r000c = b_c;
    }

    @NonNull
    @Override
    public ParentViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutId, viewGroup, false);
        return new ParentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ParentViewHolder viewHolder, final int i) {
        final Plan_plan bookShelf = mDatas.get(i);
//        viewHolder.dst.setOnClickListener(dstOn);
        viewHolder.t001.setText("第"+(i+1)+"天");
//        if (i == 0) viewHolder.dst.setText(sd);
        Calendar r000c2 = (Calendar)r000c.clone();
        r000c2.add(Calendar.DATE,i);
        viewHolder.date001.setText(timeFormat08(r000c2));
        db=new DbHelper(mContext,DB_File,null,DBversion);
        favoriteList = db.getRecSet("favorite");
        // 子RecyclerView
        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        viewHolder.recyclerView.setLayoutManager(manager);
        viewHolder.recyclerView.setAdapter(new Plan_2(bookShelf.getBooks(), R.layout.plan_r001));
        viewHolder.b001.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                int aaa = 0;
                //選擇景點dialog list
                final AlertDialog.Builder builder= new AlertDialog.Builder(v.getContext());
                locationList = new String[favoriteList.size()];
                for(int i = 0;i<favoriteList.size();i++){
                    locationList[i] = favoriteList.get(i).split("#")[5];
                }
                //設定基本對話盒
                builder.setTitle(mContext.getString(R.string.plan_toAdd));
                builder.setCancelable(false);
                //內容為陣列   (陣列名稱, 監聽)
                builder.setItems(locationList, new DialogInterface.OnClickListener() {
                    @Override //系統生成
                    public void onClick(DialogInterface dialog, final int which) {
                        dialog.cancel();
                        final List<String> a = bookShelf.getBooks();
                        final String[] sid = new String[1];
                        final Handler handler=new Handler();
                        new Thread(){
                            @Override
                            public void run() {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            sid[0] = db.AddPoint08(favoriteList.get(which).split("#")[3],i,a,planName,mDatas.size());
                                            Plan.p_cycle.setVisibility(View.INVISIBLE);
                                            String[] array = a.toArray(new String[0]);
                                            List<String> b = new ArrayList<>();
                                            for(String s:array){
                                                b.add(s);
                                            }
                                            b.add(favoriteList.get(which).split("#")[7]+"#"+locationList[which]+"#"+ sid[0]+"#1時");
                                            bookShelf.setBooks(b);
                                            notifyDataSetChanged();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        }.start();
                        Plan.p_cycle.setVisibility(View.VISIBLE);
                    }
                });
                builder.setNeutralButton("取消",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
    }

//    final TimePickerDialog.OnTimeSetListener time_picOn = new TimePickerDialog.OnTimeSetListener() {
//        @Override
//        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//            sd = ""+hourOfDay+":"+minute;
//            notifyDataSetChanged();
//        }
//    };

//    View.OnClickListener dstOn = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            Calendar calendar = Calendar.getInstance();
//            TimePickerDialog timp_pic = new TimePickerDialog(
//                    v.getContext(),
//                    time_picOn,
//                    calendar.get(Calendar.HOUR_OF_DAY),
//                    calendar.get(Calendar.MINUTE),
//                    false);
//            timp_pic.show();
//        }
//    };



    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ParentViewHolder extends RecyclerView.ViewHolder {
        LinearLayout planr000L;
        TextView t001,b001,date001,dst;
        RecyclerView recyclerView;

        public ParentViewHolder(View view) {
            super(view);
            t001 = (TextView) view.findViewById(R.id.plan_r000_t001);
            recyclerView = (RecyclerView) view.findViewById(R.id.plan_r000_re);
            b001 = (TextView) view.findViewById(R.id.plan_r000_add);
            date001 = view.findViewById(R.id.plan_date01);
            dst = view.findViewById(R.id.plan_starttime);
        }
    }


}

class Plan_2 extends RecyclerView.Adapter<Plan_2.ChildViewHolder> {
    private List<String> mBooks;
    private int mLayoutId;
    private static final String DB_File="astray.db";
    private String sd = "";
    private int vTimeNum;
    private int DBversion = DbHelper.VERSION;


    public Plan_2(List<String> list, int layoutId) {
        mBooks = list;
        int aaa = 0;
        mLayoutId = layoutId;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(mLayoutId, viewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ChildViewHolder childViewHolder, final int i) {


        final TimePickerDialog.OnTimeSetListener time_picOn = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                final DbHelper db=new DbHelper(view.getContext(),DB_File, null, DBversion);
                int n = 0;
                String b_ft="";
                if (hourOfDay!=0){
                    b_ft+= format("%d",hourOfDay)+"時";
                }
                if (minute!=0){
                    b_ft+= format("%d",minute)+"分";
                }
                final String ft = b_ft;
                final String sid = mBooks.get(i).split("#")[2];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.updatePointTime(ft,sid);
                    }
                }).start();
                sd = mBooks.get(i);
                for (int j=0;j<sd.length();j++){
                    if (sd.charAt(j) == '#'){
                        n++;
                    }
                }
                if (n<3){
                    sd += "#"+ft;
                }
                else{
                    sd = sd.split("#")[0]+"#"+sd.split("#")[1]+"#"+sd.split("#")[2]+"#"+ft;
                }
                mBooks.set(i,sd);
                notifyDataSetChanged();
            }
        };

        View.OnClickListener vstOn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timp_pic = new TimePickerDialog(
                        v.getContext(),
                        time_picOn,
                        calendar.get(Calendar.HOUR),
                        calendar.get(Calendar.MINUTE),
                        true);
                timp_pic.show();
            }
        };
        childViewHolder.vst.setOnClickListener(vstOn);
        if (true){
            try{childViewHolder.vst.setText("停留時間:"+mBooks.get(i).split("#")[3]);}
            catch(Exception e){}
        }

        childViewHolder.r001_p.setText(mBooks.get(i).split("#")[1]);
        childViewHolder.r001_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(v.getContext(),childViewHolder,i); // pass the context here

            }
        });
        childViewHolder.r001_t1.setText(mBooks.get(i).split("#")[0]);
        childViewHolder.toTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sid = Integer.parseInt(mBooks.get(i).split("#")[2]);
                Intent it = new Intent();
                it.setClass(v.getContext(), Plan_txt.class);
                it.putExtra("pointIndex",sid);
                v.getContext().startActivity(it);
            }
        });
    }


    private void showAlertDialog(Context context, final ChildViewHolder childViewHolder, final int b_i){
        final DbHelper db=new DbHelper(context,DB_File, null, DBversion);
        //跳出取消dialog
        AlertDialog.Builder dlg_delet = new AlertDialog.Builder(context);
        //設定dialog
        dlg_delet.setTitle(dlg_delet.getContext().getString(R.string.dlg_deleteTitle));
        dlg_delet.setMessage(dlg_delet.getContext().getString(R.string.dlg_deleteMessage));
        dlg_delet.setIcon(R.drawable.ic_delete);
        dlg_delet.setCancelable(false);
        //設定按鈕
        dlg_delet.setPositiveButton(dlg_delet.getContext().getString(R.string.dlg_delete_positive), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String sid = mBooks.get(b_i).split("#")[2];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        db.DelView08(sid);
                    }
                }).start();
                mBooks.remove(b_i);
                notifyDataSetChanged();
            }
        });
        dlg_delet.setNeutralButton(dlg_delet.getContext().getString(R.string.dlg_delete_neutral), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlg_delet.show();

    }

    @Override
    public int getItemCount() {
        //回傳list長度
        return mBooks.size();
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        private final TextView r001_p;
        //        private final TextView vvt;
        private final TextView r001_t1,vst;
        private final ImageView toTxt;
        private final ImageView r001_del; //宣告需用變數
        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            //抓取來源
            r001_p = itemView.findViewById(R.id.plan_r001_p);
            vst = itemView.findViewById(R.id.plan_r001_t0);
            r001_t1 = itemView.findViewById(R.id.plan_r001_t1);
//            vvt = itemView.findViewById(R.id.plan_r001_st);
            toTxt = itemView.findViewById(R.id.plan_toTxt);
            r001_del = itemView.findViewById(R.id.plan_toTxt2);

        }
    }

}