package com.shashipage.triplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.shashipage.triplanner.DBConnector.addPoint;
import static com.shashipage.triplanner.DBConnector.newPlan;


public class DbHelper extends SQLiteOpenHelper {
    String TAG = "TAG=>";
    private final String sCreateTableCommand;
    private static final String DB_FILE = "astray.db";
    public static final int VERSION = 1; //------2021/02/02------ 欄位有變更或者db有變更需改版本!!
    private static final String[] DB_TABLE = new String[9]; //------09更動(8->9)------2021/02/13------
    private static final String[] DB_TABLE_detail= new String[9]; //------09更動(8->9)------2021/02/13------
    private static SQLiteDatabase database;

    private String sql_user_details="CREATE  TABLE " +
            "user_details"  +//探索 公開回憶之測試人員table
            "  (user_ID  INTEGER , " + " login_id TEXT , " + "google_login_id  TEXT ," +
            "login_password TEXT ," +"login_password2 TEXT ," +
            "profile_picture TEXT  ,"+
            "email TEXT ," +"email2 TEXT ," +
            "nickname TEXT ,"+"first_name TEXT ," +"last_name TEXT ," +"name_1 TEXT ," +"name_2 TEXT ," +
            "login_status TEXT," +
            "access_level TEXT," +
            "creation_time LONG," +
            "edit_time LONG," +
            "last_login_time LONG," +
            "reserve_1 TEXT," +
            "reserve_2 TEXT," +
            "reserve_3 TEXT," +
            "reserve_4 TEXT," +
            "reserve_5 TEXT);";

    private String sql_itinerary01="CREATE TABLE "+
            "itinerary01"+   //Plan_Card使用之table
            " (lid INTEGER PRIMARY KEY,itinerary_id INTEGER ,google_login_id INTEGER ,itinerary_name TEXT ," +
            "itinerary_name_tbd ,start_date TEXT,finish_date TEXT,finish_date_tbd TEXT ,itinerary_day INTEGER ," +
            "creation_time TEXT ,edit_time TEXT ,last_login_time TEXT ,login_info TEXT ,tbd1 TEXT ,tbd2 TEXT ," +
            "tbd3 TEXT ,tbd4 TEXT ,tbd5 TEXT );";

    //將SQLite_it02的edit_time改為modify_time --2021.01.31
    private String sql_itinerary02="CREATE  TABLE "+
            "itinerary02"+  //Plan_Card使用之table
            " (lid INTEGER PRIMARY KEY, sub_itinerary_id  INTEGER ,itinerary_id TEXT ,nth_day INTEGER ,"+
            "schedule_date TEXT ,schedule_date_tbd TEXT ,attraction_id TEXT ,stay_time INTEGER ,"+
            "note TEXT ,note_tbd TEXT ,order_number INTEGER ,creation_time TEXT ,modify_time TEXT ,"+
            "last_login_time TEXT ,login_info TEXT ,tbd1 TEXT ,tbd2 TEXT ,tbd3 TEXT ,tbd4 TEXT ,tbd5 TEXT );";

    private String sql_memory01="CREATE TABLE "+
            "memory01"+   //PlanMyStory_Card使用之table
            " (lid  INTEGER PRIMARY KEY , memory_id  INTEGER," +
            "itinerary_id  TEXT ,google_login_id  TEXT ," +
            "creation_nickname   TEXT  ,creation_profile_picture TEXT,memory_name   TEXT  ,memory_name_tbd    TEXT  ,memory_day  TEXT  ,  privacy   TEXT ,creation_time TEXT ," +
            "modify_time TEXT, modify_time_tbd  TEXT, last_login_time TEXT ,login_info  TEXT,tbd1 TEXT,tbd2 TEXT,tbd3 TEXT,tbd4 TEXT,tbd5 TEXT);";

    private String sql_memory02="CREATE TABLE "+
            "memory02"+   //PlanMyStory_Card使用之table
            " (lid  INTEGER PRIMARY KEY , sub_memory_id   INTEGER ,memory_id  TEXT ,nth_day   TEXT ,memory_date TEXT  ,"+
            "memory_date_tbd TEXT,attraction_id  TEXT ,stay_time TEXT , memory_edit TEXT ,memory_edit_tbd  TEXT ,"+
            "order_number  TEXT,  creation_time TEXT ,modify_time TEXT,last_login_time TEXT ,"+
            "login_info  TEXT,tbd1 TEXT,tbd2 TEXT,tbd3 TEXT,tbd4 TEXT,tbd5 TEXT);";

    private String sql_attraction="CREATE TABLE " + "attraction" + "  ( " +
            "lid   INTEGER   PRIMARY KEY," +"id   INTEGER ," +"attraction_id TEXT ,"+"district TEXT," +
            "attraction_name TEXT ,"+"attraction_name_tbd TEXT,"+ "attraction_address TEXT," +
            "longitude TEXT,"+"latitude TEXT,"+"position_tbd TEXT," +
            "description TEXT,"+"description_tbd TEXT,"+"phone_number TEXT,"+"phone_number_tbd TEXT," +
            "attraction_picture TEXT,"+"attraction_picture_tbd TEXT,"+"creation_time TEXT,"+"modify_time TEXT," +
            "last_login_time TEXT,"+"login_info TEXT,"+"tbd1 TEXT," +"tbd2 TEXT,"+"tbd3 TEXT,"+"tbd4 TEXT,"+ "tbd5 TEXT);";

    private String sql_favorite="CREATE TABLE " + "favorite" + "   ( " +
            " id INTEGER PRIMARY KEY," +"favorite_id TEXT,"+"google_login_id TEXT," +
            "attraction_id TEXT NOT NULL,"+"district TEXT,"+ "attraction_name TEXT," +
            "attraction_name_tbd TEXT,"+"attraction_address TEXT,"+"longitude TEXT," +
            "latitude TEXT,"+"position_tbd TEXT,"+"favorite_date DATE,"+"creation_time TIMESTAMP," +
            "modify_time TIMESTAMP,"+"last_login_time TIMESTAMP,"+"login_info TEXT,"+"tbd1 TEXT," +
            "tbd2 TEXT,"+"tbd3 TEXT,"+"tbd4 TEXT," + "tbd5 TEXT);";

    //------09table------Public------
    private String sql_public_home="CREATE  TABLE " +
            "public_home"  +//探索 公開回憶之測試人員table
            " (lid  INTEGER PRIMARY KEY , google_login_id TEXT , creation_nickname TEXT , "+
            "creation_profile_picture  TEXT , memory_id  TEXT , memory_name   TEXT , "+
            "memory_day TEXT  , modify_time TEXT);";

    private String sql_public_attr="CREATE  TABLE " +
            "public_attr"  +//探索 公開回憶之測試人員table
            " (lid  INTEGER PRIMARY KEY , attraction_id TEXT , attraction_name TEXT , "+
            "attraction_picture TEXT );";

    private String sqlctl;
    public static String google_id="";

    //----------------------------------------------
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new DbHelper(context, DB_FILE, null, VERSION)
                    .getWritableDatabase();
        }
        return database;
    }

    //    ContentValues values
    public long insertRec_m(ContentValues rec, String s) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(s, null, rec);
        db.close();
        return rowID;
    }

    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        sCreateTableCommand = "";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DB_TABLE[0]="user_details";
        DB_TABLE[1]="itinerary01";
        DB_TABLE[2]="itinerary02";
        DB_TABLE[3]="memory01";
        DB_TABLE[4]="memory02";
        DB_TABLE[5]="attraction";
        DB_TABLE[6]="favorite";
        DB_TABLE[7]="public_home"; //------09新增------2021/01/30
        DB_TABLE[8]="public_attr"; //------09新增------2021/02/13
        DB_TABLE_detail[0]=sql_user_details;
        DB_TABLE_detail[1]=sql_itinerary01;
        DB_TABLE_detail[2]=sql_itinerary02;
        DB_TABLE_detail[3]=sql_memory01;
        DB_TABLE_detail[4]=sql_memory02;
        DB_TABLE_detail[5]=sql_attraction;
        DB_TABLE_detail[6]=sql_favorite;
        DB_TABLE_detail[7]=sql_public_home; //------09新增------2021/01/30
        DB_TABLE_detail[8]=sql_public_attr; //------09新增------2021/02/13

        for(int i=0;i<DB_TABLE.length;i++){
            db.execSQL(DB_TABLE_detail[i]);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");

        DB_TABLE[0]="user_details";
        DB_TABLE[1]="itinerary01";
        DB_TABLE[2]="itinerary02";
        DB_TABLE[3]="memory01";
        DB_TABLE[4]="memory02";
        DB_TABLE[5]="attraction";
        DB_TABLE[6]="favorite";
        DB_TABLE[7]="public_home"; //------09新增------2021/01/30------
        DB_TABLE[8]="public_attr"; //------09新增------2021/02/13

        for(int i=0;i<DB_TABLE.length;i++){
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE[i]);
        }
        onCreate(db);
    }

    //目前共有幾筆的function
    public int RecCount(String db_table) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + db_table;
        Cursor recSet = db.rawQuery(sql, null);
        int cnt = recSet.getCount();
        recSet.close();
        db.close();
        return cnt;
    }

    public ArrayList<String> getRecSet(String db_table) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + db_table;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        //----------------------------
        Log.d(TAG, "recSet=" + recSet);
        int columnCount = recSet.getColumnCount();

        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();

        Log.d(TAG, "recAry=" + recAry);
        return recAry;
    }





    //==============================================================================================
    //01
    //==search_content==
    //新增
    public Long insertRec01sc(String google_login_id, String b_attraction_id, String b_attraction_name, String b_attraction_address, String b_longitude, String b_latitude, String imgURL) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("google_login_id", google_login_id);
        rec.put("attraction_id", b_attraction_id);
        rec.put("attraction_name", b_attraction_name);
        rec.put("attraction_address", b_attraction_address);
        rec.put("longitude", b_longitude);
        rec.put("latitude", b_latitude);
        rec.put("tbd1", imgURL);
        Long rowID=db.insert("favorite",null,rec);
        db.close();
        return rowID;
    }
    //目前共有幾筆的function
    public int recCount01sc() {
        SQLiteDatabase db = getWritableDatabase();
        String sql=" SELECT * FROM favorite";
        Cursor recSet=db.rawQuery(sql,null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }
    //查詢是否有這筆
    public String findRec01sc(String b_attraction_id) {
        SQLiteDatabase db=getReadableDatabase();
        String fldSet=null;
        String sql="SELECT * FROM favorite WHERE attraction_id =? ORDER BY id ASC ";
        String [] args = {b_attraction_id};
        Cursor recSet=db.rawQuery(sql,args);//args=?
        int columnCount=recSet.getColumnCount();//幾個欄位的意思
        if(recSet.getCount() !=0){
            recSet.moveToFirst();
            fldSet=recSet.getString(0)+","//attraction_id
                    +recSet.getString(1)+","//attraction_name
                    +recSet.getString(2)+","//attraction_address
                    +recSet.getString(3)+","//longitude
                    +recSet.getString(4)+"\n";//latitude
            while (recSet.moveToNext()) {
                for (int i = 0; i < columnCount; i++) {
                    fldSet += recSet.getString(i) + " ";
                }
                fldSet += "\n";
            }
        }
        recSet.close();
        db.close();
        return fldSet;
    }
    //===========================================search ====================================================
    // 新增
    public long insertRec_m01s(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert("attraction", null, rec);
        db.close();
        return rowID;
    }
    //目前共有幾筆的function
    public int recCount01s() {
        SQLiteDatabase db = getWritableDatabase();
        String sql=" SELECT * FROM attraction";
        Cursor recSet=db.rawQuery(sql,null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }

    //資料庫裡共有幾筆
    public ArrayList<String> getRecSet01s() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM attraction";
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();

        int columnCount = recSet.getColumnCount();

        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";//"#"每個欄位用#區隔(用不常用的字)
            //                recSet.getString(i) 這樣就不用瞭解欄位有幾個了
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }

    public int clearRec01s() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM attraction";
        Cursor recSet = db.rawQuery(sql, null);
//        //-----------
//        Cursor c1=db.execSQL("");
//        Cursor c2=db.rawQuery();
//        Cursor c3=db.insert();
//        Cursor c4=db.update(, , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , , );
//        Cursor c5=db.delete();
//        //------------------------------------------------
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = db.delete("attraction", "1", null); //
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    //01號 spinner抓取縣市鄉鎮用的方法
    public List<String> gettbd1() {
        List<String>town=new ArrayList<String>();
        String selectQuery="SELECT * FROM attraction ORDER BY tbd2 ASC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor=db.rawQuery(selectQuery, null);

        cursor.moveToFirst();//從第一筆開始
        while(!cursor.isAfterLast()){
            if(!town.contains(cursor.getString(20))){
                town.add(cursor.getString(20));
            }
            cursor.getString(20);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return town;
    }

    public List<String> getdistrict(String spindistrict) {
        List<String>town=new ArrayList<String>();
        //        用HashSet去判斷裡面的所有資料有沒有重複
        Set<String> set = new HashSet<String>();
        String selectQuery = "SELECT  * FROM   attraction  WHERE tbd1 = '" + spindistrict + "' ";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do{
                set.add(cursor.getString(3));
            }while (cursor.moveToNext());
        }
        town.addAll(set);
        cursor.close();
        db.close();
        return town;
    }

    public ArrayList<Map<String, Object>> getAll(String spindistrict, String sss) {
        List<String> labels = new ArrayList<String>();
        String selectQuery = "SELECT  * FROM  attraction  WHERE district = '" + sss + "' AND tbd1 = '" + spindistrict + "' ";
        SQLiteDatabase db = this.getReadableDatabase();  //讀
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<Map<String, Object>> mLsit = new ArrayList<Map<String, Object>>();
        if (cursor.moveToFirst()) {
            do {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("district", cursor.getString(3));//鄉鎮
                item.put("attraction_name", cursor.getString(4));//名稱
                item.put("attraction_id",cursor.getString(2));//id
                item.put("longitude",cursor.getString(7));//東經
                item.put("latitude",cursor.getString(8));//北緯
                item.put("attraction_address",cursor.getString(6));//地址
                item.put("phone_number",cursor.getString(12));//電話
                item.put("attraction_picture",cursor.getString(14));//圖片
                item.put("tbd1",cursor.getString(20));//縣市
                mLsit.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return mLsit;
    }


    //==============================================================================================
    //03
    //==============================================================================================
    //04
    public Long insertMemory01(int b_memory_id, String b_itinerary_id, String b_google_login_id, String b_creation_nickname, String b_creation_profile_picture, String b_memory_name, int b_privacy, String b_memory_day, String b_creation_time, String b_modify_time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("memory_id",b_memory_id);
        rec.put("itinerary_id",b_itinerary_id);
        rec.put("google_login_id", b_google_login_id);
        rec.put("creation_nickname", b_creation_nickname);
        rec.put("creation_profile_picture", b_creation_profile_picture);
        rec.put("memory_name", b_memory_name);
        rec.put("privacy", b_privacy);
        rec.put("memory_day", b_memory_day);
        rec.put("creation_time", b_creation_time);
        rec.put("modify_time", b_modify_time);
        Long rowID=db.insert("memory01",null,rec);//回傳ID
        db.close();
        return rowID;
    }

    public Long insertMemory02(int b_sub_memory_id, String b_memory_id, String b_nth_day, String b_attraction_id, String b_schedule_date, String b_stay_time, String b_memory_edit, String b_order_number, String b_creation_time, String b_modify_time) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("sub_memory_id",b_sub_memory_id);
        rec.put("memory_id",b_memory_id);
        rec.put("nth_day", b_nth_day);
        rec.put("attraction_id", b_attraction_id);
        rec.put("memory_date", b_schedule_date);
        rec.put("stay_time", b_stay_time);
        rec.put("memory_edit", b_memory_edit);
        rec.put("order_number", b_order_number);
        rec.put("creation_time", b_creation_time);
        rec.put("modify_time", b_modify_time);

        Long rowID=db.insert("memory02",null,rec);//回傳ID
        db.close();
        return rowID;
    }

    public void updateMemory02(String b_id, int b_sub_memory_id, String b_memory_edit, String b_modify_time) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM memory02";
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0){
            ContentValues rec=new ContentValues();
            rec.put("memory_edit",b_memory_edit);
            rec.put("modify_time",b_modify_time);

            String whereClause=b_id +"='"+b_sub_memory_id +"'";
            db.update("memory02",rec,whereClause,null);
            recSet.close();
            db.close();
        }else {
            recSet.close();
            db.close();
        }
    }

    public void updateMemory01(String b_id, Long b_memory_id, String b_memory_name, int b_memory_privacy, String b_modify_time) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM memory01";
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0){
            ContentValues rec=new ContentValues();
            rec.put("memory_name",b_memory_name);
            rec.put("privacy",b_memory_privacy);
            rec.put("modify_time",b_modify_time);

            String whereClause=b_id +"='"+b_memory_id +"'";//lid或memory_id被更新
//            String whereClause="id= "+b_id ;
            db.update("memory01",rec,whereClause,null);
            recSet.close();
            db.close();
        }else {
            recSet.close();
            db.close();
        }
    }

    public void clearMemory01() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM memory01";
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            db.delete("memory01", "1", null); //0+WHERE條件 1刪除
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
        } else {
            recSet.close();
            db.close();
        }
    }

    public void clearMemory02() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM memory02";
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            db.delete("memory02", "1", null); //0+WHERE條件 1刪除
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
        } else {
            recSet.close();
            db.close();
        }
    }
    //04--------結束-----------------------------------------
    //==============================================================================================
    //08
    public String DB_NewPlan08(String b_pname, int b_dnum, Calendar b_stime) {

        String sid = "";
        String gid = find11("google_login_id");
        Calendar ccf = (Calendar) b_stime.clone();
        ccf.add(Calendar.DAY_OF_YEAR,b_dnum-1);
        try{
            Thread.sleep(500);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        newPlan(b_pname,""+b_dnum,""+timeFormat08(b_stime),""+timeFormat08(ccf),gid);
        dbmysql("itinerary01");
        dbmysql("itinerary02");
        try{
            Thread.sleep(500);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        SQLiteDatabase mAstrayDb = getWritableDatabase();
        Cursor cursor = mAstrayDb.rawQuery("SELECT   DISTINCT  tbl_name  from  sqlite_master where tbl_name = 'itinerary01'", null);//Cursor
        //通用寫法 sqlite_master檔案Astray.db
        if (cursor!=null){
            cursor = mAstrayDb.rawQuery("select * from itinerary01 ",null);
            if(cursor.moveToLast()){
                sid = cursor.getString(cursor.getColumnIndex("itinerary_id"));
            }
            cursor.close();//cursor要關 不關吃流量
            mAstrayDb.close();
        }
        return sid;
    }

    public static String timeFormat08(Calendar b_cc){
        return new SimpleDateFormat("yyyy/MM/dd").format(b_cc.getTime());
    }

    public String AddPoint08(String b_lw, int b_i, List<String> b_a,String b_planname,int b_dnum) {
        String sid = "";
        try{
            Thread.sleep(500);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        addPoint(b_lw,b_planname.split("#")[1],""+(b_i+1),""+(b_a.size()+1),""+b_dnum);
        dbmysql("itinerary01");
        dbmysql("itinerary02");
        try{
            Thread.sleep(500);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        // 檢查資料表是否已經存在，如果不存在，就建立一個。
        SQLiteDatabase mAstrayDb = getWritableDatabase();
        Cursor cursor = mAstrayDb.rawQuery("SELECT   DISTINCT  tbl_name  from  sqlite_master where tbl_name = 'itinerary02'", null);//Cursor
        //通用寫法 sqlite_master檔案Astray.db
        if (cursor!=null){
            cursor = mAstrayDb.rawQuery("select * from itinerary02 ",null);
            if(cursor.moveToLast()){
                sid = cursor.getString(cursor.getColumnIndex("sub_itinerary_id"));
            }
            cursor.close();//cursor要關 不關吃流量
            mAstrayDb.close();
        }
        return sid;
    }

    public void DelView08(String b_id) {
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(b_id);
        DBConnector.executeDelet(nameValuePairs,"i02");
        dbmysql("itinerary02");
        try{
            Thread.sleep(500);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public String[] DB_ReadPlan08(int b_id) {
//        dbmysql("itinerary01");
        String sid = "",pname="",ptime="";
        SQLiteDatabase mAstrayDb = getWritableDatabase();
        Cursor cursor = mAstrayDb.rawQuery("select * from itinerary01 WHERE itinerary_id=?",new String[]{""+b_id});
        int aaa=0;
        if(cursor.moveToLast()){
            sid = cursor.getString(cursor.getColumnIndex("itinerary_day"));
            pname = cursor.getString(cursor.getColumnIndex("itinerary_name"));
            ptime = cursor.getString(cursor.getColumnIndex("start_date"));
        }
        cursor.close();//cursor要關 不關吃流量
        mAstrayDb.close();
        return new String[]{sid,pname,ptime};
    }
    public String[] DB_ReadPoint08(int b_id,int b_point,int b_i) {
        String sid = "",aid="",alat="0",along="0",addr="0",stime="0";
        SQLiteDatabase mAstrayDb = getWritableDatabase();
        Cursor cursor = mAstrayDb.rawQuery("select * from itinerary02 LEFT JOIN attraction WHERE attraction.attraction_id = itinerary02.attraction_id AND itinerary02.itinerary_id=? AND itinerary02.nth_day=?",new String[]{""+b_id,""+b_i});
        int aaa=0;
        if(cursor.moveToPosition(b_point)){

            sid = cursor.getString(cursor.getColumnIndex("sub_itinerary_id"));
            aid = cursor.getString(cursor.getColumnIndex("attraction_name"));
            along = cursor.getString(cursor.getColumnIndex("longitude"));
            alat = cursor.getString(cursor.getColumnIndex("latitude"));
            addr = cursor.getString(cursor.getColumnIndex("attraction_address"));
            stime = cursor.getString(cursor.getColumnIndex("stay_time"));
            if (stime==null) stime="0";
        }
        else{
            cursor.close();//cursor要關 不關吃流量
//            mAstrayDb.close();
            return null;
        }
        cursor.close();//cursor要關 不關吃流量
//        mAstrayDb.close();
        return new String[]{aid,sid,alat+","+along,addr,stime};
    }
    public String getTxt08(int b_txtid) {
        String ss="";
        SQLiteDatabase mAstrayDb = getWritableDatabase();
        Cursor cursor = mAstrayDb.rawQuery("select * from itinerary02 WHERE sub_itinerary_id=?",new String[]{""+b_txtid});
        int aaa=0;
        if(cursor.moveToLast()){
            ss = cursor.getString(cursor.getColumnIndex("note"));
        }
        cursor.close();//cursor要關 不關吃流量
        mAstrayDb.close();
        if(ss==null) return "";
        return ss;
    }
    public void updateTxt08(int txtid,String ss) {
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(""+txtid);
        nameValuePairs.add(ss);
        DBConnector.updatePoint(nameValuePairs,"txt");
        dbmysql("itinerary02");
        return;
    }
    public void DelDay08(String b_name,int b_dnum) {
        String pid = b_name.split("#")[1];
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(pid);
        nameValuePairs.add(""+b_dnum);
        DBConnector.executeDelet( nameValuePairs,"i02d");
        dbmysql("itinerary01");
        dbmysql("itinerary02");
    }

    public void updatePointTime(String stayt,String b_id) {
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(b_id);
        nameValuePairs.add(stayt);
        DBConnector.updatePoint(nameValuePairs,"time");
        dbmysql("itinerary02");
        return;
    }

    // 讀取MySQL 資料
    public void dbmysql(String b_table) {
        String sqlctl = "SELECT * FROM "+b_table+"";
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
//--------------------------------------------------------
                clearRec(b_table);                 // 匯入前,刪除所有SQLite資料
                int bbb=0;
//--------------------------------------------------------
                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
                    if(true){
                        // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
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
                            // ------------------------------------------------------------------
                            newRow.put(key, value); // 動態找出有幾個欄位
                            int aaa =0;
                            // -------------------------------------------------------------------
                        }
                    }
                    else{
//                        newRow.put("sub_itinerary_id", jsonData.getString("sub_itinerary_id"));
//                        newRow.put("nth_day", jsonData.getString("nth_day"));
//                        newRow.put("itinerary_id", jsonData.getString("itinerary_id"));
//                        newRow.put("order_number", jsonData.getString("order_number"));
//                        newRow.put("creation_time", jsonData.getString("creation_time"));
//                        newRow.put("edit_time", jsonData.getString("edit_time"));
                    }
                    // ---(2) 使用固定已知欄位---------------------------
                    // newRow.put("id", jsonData.getString("id").toString());
                    // newRow.put("name",
                    // jsonData.getString("name").toString());
                    // newRow.put("grp", jsonData.getString("grp").toString());
                    // newRow.put("address", jsonData.getString("address")
                    // -------------------加入SQLite---------------------------------------
                    SQLiteDatabase db = getWritableDatabase();
                    insertRec_m(newRow,b_table,db);
//                    db.close();
                }
                // ---------------------------
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
    public void dbmysql(String b_table, boolean b_re) {
        if (b_re){
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT *  from  "+b_table+"", null);//Cursor
            //通用寫法 sqlite_master檔案Astray.db
            if (cursor!=null){
                if(cursor.getCount()!=0){
                    cursor.close();
                    db.close();
                    return;
                }
            }
            cursor.close();
            db.close();
        }
        dbmysql(b_table);
    }
    // 讀取MySQL 資料
    public void dbmysql(String b_table,String b_gid) {
        String sqlctl = "SELECT * FROM "+b_table+" WHERE google_login_id ="
                +b_gid;
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
//--------------------------------------------------------
                clearRec(b_table);                 // 匯入前,刪除所有SQLite資料
                int bbb=0;
//--------------------------------------------------------
                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
                    if(true){
                        // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
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
                            // ------------------------------------------------------------------
                            newRow.put(key, value); // 動態找出有幾個欄位
                            int aaa =0;
                            // -------------------------------------------------------------------
                        }
                    }
                    else{
//                        newRow.put("sub_itinerary_id", jsonData.getString("sub_itinerary_id"));
//                        newRow.put("nth_day", jsonData.getString("nth_day"));
//                        newRow.put("itinerary_id", jsonData.getString("itinerary_id"));
//                        newRow.put("order_number", jsonData.getString("order_number"));
//                        newRow.put("creation_time", jsonData.getString("creation_time"));
//                        newRow.put("edit_time", jsonData.getString("edit_time"));
                    }
                    // ---(2) 使用固定已知欄位---------------------------
                    // newRow.put("id", jsonData.getString("id").toString());
                    // newRow.put("name",
                    // jsonData.getString("name").toString());
                    // newRow.put("grp", jsonData.getString("grp").toString());
                    // newRow.put("address", jsonData.getString("address")
                    // -------------------加入SQLite---------------------------------------
                    SQLiteDatabase db = getWritableDatabase();
                    insertRec_m(newRow,b_table,db);
//                    db.close();
                }
                // ---------------------------
            }
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public void dbmysql(String b_table,String b_gid,boolean b_re) {
        if (b_re){
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM "+b_table+" WHERE google_login_id ="
                    +b_gid, null);//Cursor
            //通用寫法 sqlite_master檔案Astray.db
            if (cursor!=null){
                if(cursor.getCount()!=0){
                    cursor.close();
                    db.close();
                    return;
                }
            }
            cursor.close();
            db.close();
        }
        dbmysql(b_table,b_gid);
    }

    //    ContentValues values
    public long insertRec_m(ContentValues rec,String b_table,SQLiteDatabase b_db) {
        long rowID = b_db.insert(b_table, null, rec);
//        while (recSet.moveToNext()) {
//            String fldSet = "";
//            for (int i = 0; i < columnCount; i++)
//                fldSet += recSet.getString(i) + "#";
//        }
        return rowID;
    }
    //    public ArrayList<String> getRecSet(String b_table) {
//        SQLiteDatabase db = getReadableDatabase();
//        String sql = "SELECT * FROM " + b_table;
//        Cursor recSet = db.rawQuery(sql, null);
//        ArrayList<String> recAry = new ArrayList<String>();
//
//        //----------------------------
//        Log.d(TAG, "recSet=" + recSet);
//        int columnCount = recSet.getColumnCount();
//
//        while (recSet.moveToNext()) {
//            String fldSet = "";
//            for (int i = 0; i < columnCount; i++)
//                fldSet += recSet.getString(i) + "#";
//            recAry.add(fldSet);
//        }
//        //------------------------
//        recSet.close();
//        db.close();
//
//        Log.d(TAG, "recAry=" + recAry);
//        return recAry;
//    }
    public int clearRec(String b_table) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + b_table;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//          String whereClause = "id < 0";
            int rowsAffected = db.delete(b_table, "1", null); //
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }



    //==============================================================================================
    //------09------2021/01/30----------
    public ArrayList<String> getShowList(String b_dbTable) {
        SQLiteDatabase db = getReadableDatabase(); //僅查詢，故用read
        String sql = "SELECT * FROM " + b_dbTable;
        //20210308
        if(b_dbTable.equals("public_home")){
            sql += " ORDER BY memory_id DESC";
        }else if (b_dbTable.equals("itinerary01")||b_dbTable.equals("memory01")){
            sql += " ORDER BY creation_time DESC";
        }//排序
        Cursor cur_recset = db.rawQuery(sql, null);
        ArrayList<String> recArray = new ArrayList<>();
        int columnCount = cur_recset.getColumnCount(); //取得欄位數量
        if (cur_recset.getCount() != 0) {
            cur_recset.moveToFirst();
            do {
                String fldSet = ""; //設定一個字串清空
                for (int i = 0; i < columnCount; i++)
                    fldSet += cur_recset.getString(i) + "#"; //將所有欄位、所有資料抓取出來
                recArray.add(fldSet); //塞進陣列
            }
            while (cur_recset.moveToNext());
        }
        cur_recset.close();
        db.close();
        return recArray;
    }

    public ArrayList<String> getShowList_l(String b_dbTable, Object b_info_l) {
        SQLiteDatabase db = getReadableDatabase(); //僅查詢，故用read
        String sql = "SELECT * FROM " + b_dbTable +" WHERE attraction_id = '"+b_info_l+"'";
        int a=0;
        Cursor cur_recset = db.rawQuery(sql, null);
        ArrayList<String> recArray = new ArrayList<>();
        int columnCount = cur_recset.getColumnCount(); //取得欄位數量
        if (cur_recset.getCount() != 0) {
            cur_recset.moveToFirst();
            do {
                String fldSet = ""; //設定一個字串清空
                for (int i = 0; i < columnCount; i++)
                    fldSet += cur_recset.getString(i) + "#"; //將所有欄位、所有資料抓取出來
                recArray.add(fldSet); //塞進陣列
            }
            while (cur_recset.moveToNext());
        }
        cur_recset.close();
        db.close();
        return recArray;
    }



    public int clear_all(String b_dbTable) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + b_dbTable;
        Cursor cur_recset = db.rawQuery(sql, null); //搜索全部
//        Cursor cc=db.execSQL(); //執行SQLite指令
//        Cursor cc7=db.rawQuery(sql, selectionArgs:null(全部)); //讀取資料
//        Cursor cc1=db.insert(String, String, ContentValues) //寫入資料
//        Cursor cc2=db.insertOrThrow(String, String, ContentValues)
//        Cursor cc3=db.insertWithOnConflict(String, String, ContentValues, int)
//
//        Cursor cc4=db.update(String, ContentValues, String, String[]) //更新資料
//        Cursor cc5=db.updateWithOnConflict(String, ContentValues, String, String[], int)
//
//        Cursor cc6=db.delete(Table, 哪裡的資料, String[]) //刪除資料
        if (cur_recset.getCount() != 0) { //若有資料才執行
//			String whereClause = "id < 0";
            int rowsAffected = db.delete(b_dbTable, "1", null); //"1"表示所有資料清空
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            cur_recset.close();
            db.close();
            return rowsAffected;
        } else {
            cur_recset.close();
            db.close();
            return -1;
        }

    }

    //    ContentValues values
    public long insert_fMySQL(String b_dbTable, ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(b_dbTable, null, rec);
        int a=0;
        db.close();
        return rowID;
    }

    public ArrayList<String> getPublicMemory(String b_dbTable) {
        SQLiteDatabase db = getReadableDatabase(); //僅查詢，故用read
//
        String sql = "SELECT * FROM " + b_dbTable;
        Cursor cur_recset = db.rawQuery(sql, null);
        ArrayList<String> recArray = new ArrayList<>();
        int columnCount = cur_recset.getColumnCount(); //取得欄位數量
        if (cur_recset.getCount() != 0) {
            cur_recset.moveToFirst();
            do {
                String fldSet = ""; //設定一個字串清空
                for (int i = 0; i < columnCount; i++)
                    fldSet += cur_recset.getString(i) + "#"; //將所有欄位、所有資料抓取出來
                recArray.add(fldSet); //塞進陣列
            }
            while (cur_recset.moveToNext());
        }
        cur_recset.close();
        db.close();
        return recArray;
    }

    public int close_DB() {
        if(database.isOpen()){
            database.close();
            return 1;
        }else {
            return 0;
        }
        //若關閉資料庫則回傳1
    }
    //------09-----over-------

    //==============================================================================================
    //10
    public ArrayList<String> getRecSet10(String db_table, String column, String orderBy) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " +db_table+ " ORDER BY "+column+" "+orderBy;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();
        Log.d(TAG, "recSet=" + recSet);
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        recSet.close();
        db.close();
        Log.d(TAG, "recAry=" + recAry);
        return recAry;
    }

    public String deleteFavorite10(int position){
        String id = "";
        SQLiteDatabase db = getWritableDatabase();
        Cursor cur_list = db.query(true,
                "favorite",
                new String[]{"id","attraction_id"},
                null,
                null,
                null,
                null,
                "creation_time DESC",
                null);
        if (cur_list == null) return null;
        try {
            cur_list.moveToPosition(position);
            String attraction_id = cur_list.getString(1);
            db.delete("favorite", "attraction_id=" +"'"+attraction_id+"'", null);
            id = attraction_id;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.close();
            cur_list.close();
        }
        return id;
    }

    //==============================================================================================
    //11 SQL
    public String find11(String str) {//收尋
//        if (str.equals("google_login_id")&&!google_id.equals("")){
//            return google_id;
//        }
        SQLiteDatabase db = getReadableDatabase();
        String fldSet = null;
        String sql = "SELECT "+str+"  FROM user_details";
        Cursor recSet = db.rawQuery(sql, null);
//        int columnCount = recSet.getColumnCount();
        try {
            if (recSet.getCount() != 0) {
                recSet.moveToFirst();
                fldSet = recSet.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recSet.close();
            db.close();
        }

//        if (recSet.getCount() != 0) {
//            recSet.moveToFirst();
//            fldSet = recSet.getString(0);
//        }
//        recSet.close();
//        db.close();
//        if (str.equals("google_login_id")) google_id = fldSet;

        return fldSet;
    }
    //...............................................

    ///.........................................................................
    public int clearRec11() {//清空
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM user_details";
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() >= 0) {
//			String whereClause = "id < 0";

            int rowsAffected = db.delete( "user_details", "1", null); //

            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }
    //............................
    public boolean status11() {//收尋判斷登入狀態
        SQLiteDatabase db = getReadableDatabase();
        boolean fldSet = false;
        String sql = "SELECT  *  FROM user_details" ;
        Cursor recSet = db.rawQuery(sql, null);

        try {
            if (recSet.getCount() != 0) {
//            recSet.moveToFirst();
//            if(recSet.getString(0).equals("1")){
                fldSet = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recSet.close();
            db.close();
        }
//        if (recSet.getCount() != 0) {
////            recSet.moveToFirst();
////            if(recSet.getString(0).equals("1")){
//            fldSet=true;
//        }
//        recSet.close();
//        db.close();
        return fldSet;
    }




    //........................................................................................



}
