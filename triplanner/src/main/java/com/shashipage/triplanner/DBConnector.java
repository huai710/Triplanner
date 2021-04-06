package com.shashipage.triplanner;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DBConnector {
    private static String postUrl;
    private static String myResponse;
    static String result = null;
    private static OkHttpClient client = new OkHttpClient();
    public static int httpstate=0;

    // -------HOSTING-------
    static String connect_ip = "https://shashipage.com/triplanner/triplanner_connect_db_all.php";
//    static String c_ip_d_i01 = "https://shashipage.com/astray/astray_d_plan2_api.php"; //2021/02/06
//    static String c_ip_d_m01 = "https://shashipage.com/astray/astray_d_memory2_api.php"; //2021/02/06


    public static String executeQuery(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        FormBody body = new FormBody.Builder()//FormBody表示php檔案?後面內容
                .add("selefunc_string","query")
                .add("query_string", query_0)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();

        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值

        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
//
//    //    新增
//    public static String executeInsert(ArrayList<String> query_string) {
////        OkHttpClient client = new OkHttpClient();
//        postUrl=connect_ip ;
//        //--------------
//        String query_0 = query_string.get(0);
//        String query_1 = query_string.get(1);
//        String query_2 = query_string.get(2);
//
//        FormBody body = new FormBody.Builder()//FormBody表示php檔案?後面內容
//                .add("selefunc_string","insert")
//                .add("name", query_0)
//                .add("grp", query_1)
//                .add("address", query_2)
//                .build();
////--------------
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//
//        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
//        httpstate = 0;   //設 httpcode初始值
//
//        try (Response response = client.newCall(request).execute()) {//執行http命令
//            httpstate=response.code();
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//
//    }
//
//    //---更新資料--------------------------------------------------------------
//    public static String executeUpdate(ArrayList<String> query_string) {
////        OkHttpClient client = new OkHttpClient();
//        postUrl=connect_ip ;
//        //--------------
//        String query_0 = query_string.get(0);
//        String query_1 = query_string.get(1);
//        String query_2 = query_string.get(2);
//        String query_3 = query_string.get(3);
//
//        FormBody body = new FormBody.Builder()
//                .add("selefunc_string","update")
//                .add("id", query_0)
//                .add("name", query_1)
//                .add("grp", query_2)
//                .add("address", query_3)
//                .build();
////--------------
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//
//        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
//        httpstate = 0;   //設 httpcode初始值
//
//        try (Response response = client.newCall(request).execute()) {
//            httpstate=response.code();
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//
//    //---刪除資料--------------------------------------------------------------
//    public static String executeDelet(ArrayList<String> query_string) {
////        OkHttpClient client = new OkHttpClient();
//        postUrl=connect_ip ;
//        //--------------
//        String query_0 = query_string.get(0);
//
//        FormBody body = new FormBody.Builder()
//                .add("selefunc_string","delete")
//                .add("id", query_0)
//                .build();
////--------------
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//
//        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
//        httpstate = 0;   //設 httpcode初始值
//
//        try (Response response = client.newCall(request).execute()) {
//            httpstate=response.code();
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    //==============================================================================================
    //01
//    public static String executeQuery(ArrayList<String> query_string) {
////        OkHttpClient client = new OkHttpClient();
//        postUrl=connect_ip ;
//        //--------------
//
//        String query_0 = query_string.get(0);
//        //FormBody ?後面的那一堆東西
//        FormBody body = new FormBody.Builder()
//                .add("selefunc_string","query")
//                .add("query_string", query_0)
//                .build();
////--------------
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
    //==============================================================================================
    //03
    //==============================================================================================
    //04
    public static String executeInsertMemory01(ArrayList<String> query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
//        postUrl="https://shashipage.com/astray/astray_c_memory1_api.php" ;
        //--------------
        String[] fld = query_string.get(0).split("#");
        String query_2 = fld[2];//陣列第三欄itinerary_id
        String query_3 = fld[3];//google_login_id
        String query_4 = fld[4];//creation_nickname
        String query_5 = fld[5];//creation_profile_picture
        String query_6 = fld[6];//memory_name
        String query_8 = fld[8];//memory_day
        String query_9 = fld[9];//privacy
        String query_10 = fld[10];//creation_time
        String query_11 = fld[11];//modify_time

        FormBody body = new FormBody.Builder()//PHP?後帶入的參數
                .add("selefunc_string","insertMemory01")
                .add("itinerary_id", query_2)
                .add("google_login_id", query_3)
                .add("creation_nickname", query_4)
                .add("creation_profile_picture", query_5)
                .add("memory_name", query_6)
                .add("memory_day", query_8)
                .add("privacy", query_9)
                .add("creation_time", query_10)
                .add("modify_time", query_11)
                .build();
        //----------------------------------------------------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeInsertMemory02(String mysql_memory_id, String query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
//        postUrl="https://shashipage.com/astray/astray_c_memory2_api.php" ;
        //--------------
        String[] fld = query_string.split("#");
        String query_2 = mysql_memory_id;//memory_id
        String query_3 = fld[3];//nth_day
        String query_4 = fld[4];//memory_date
        String query_6 = fld[6];//attraction_id
        String query_7 = fld[7];//stay_time
        String query_8 = fld[8];//memory_edit
        String query_10 = fld[10];//order_number
        String query_11 = fld[11];//creation_time
        String query_12 = fld[12];//modify_time

        FormBody body = new FormBody.Builder()//PHP?後帶入的參數
                .add("selefunc_string","insertMemory02")
                .add("memory_id", query_2)
                .add("nth_day", query_3)
                .add("memory_date", query_4)
                .add("attraction_id", query_6)
                .add("stay_time", query_7)
                .add("memory_edit", query_8)
                .add("order_number", query_10)
                .add("creation_time", query_11)
                .add("modify_time", query_12)
                .build();
        //----------------------------------------------------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeIUpdateMemory01(ArrayList<String> query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
//        postUrl="https://shashipage.com/astray/astray_u_memory1_api.php" ;
        //--------------
        String[] fld = query_string.get(0).split("#");
        String query_1 = fld[1];//陣列第二欄memory_id
        String query_4 = fld[4];//creation_nickname
        String query_5 = fld[5];//creation_profile_picture
        String query_6 = fld[6];//memory_name
        String query_9 = fld[9];//privacy
        String query_11 = fld[11];//modify_time

        FormBody body = new FormBody.Builder()//PHP?後帶入的參數
                .add("selefunc_string","updateMemory01")
                .add("memory_id", query_1)
                .add("creation_nickname", query_4)
                .add("creation_profile_picture", query_5)
                .add("memory_name", query_6)
                .add("privacy", query_9)
                .add("modify_time", query_11)
                .build();
        //----------------------------------------------------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeUpdateMemory02(String query_string) {
        //        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
//        postUrl="https://shashipage.com/astray/astray_u_memory2_api.php" ;
        //--------------
        String[] fld = query_string.split("#");
        String query_1 = fld[1];//陣列第二欄sub_memory_id
        String query_8 = fld[8];//memory_edit
        String query_12 = fld[12];//modify_time

        FormBody body = new FormBody.Builder()//PHP?後帶入的參數
                .add("selefunc_string","updateMemory02")
                .add("sub_memory_id", query_1)
                .add("memory_edit", query_8)
                .add("modify_time", query_12)
                .build();
        //----------------------------------------------------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //==============================================================================================
    //08
//    public static String executeQuery(ArrayList<String> query_string) {
//        //        OkHttpClient client = new OkHttpClient();
//        postUrl=connect_ip ;
//        //--------------
//        String query_0 = query_string.get(0);
//        FormBody body = new FormBody.Builder()
//                .add("selefunc_string","query")
//                .add("query_string", query_0)
//                .build();
////--------------
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
//        httpstate = 0;   //設 httpcode初始值
//        try (Response response = client.newCall(request).execute()) {
//            httpstate = response.code();
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    public static String executeInsert(ArrayList<String> query_string) {//新增
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);
        String query_2 = query_string.get(2);

        FormBody body = new FormBody.Builder()//body指PHP?代入的參數
                .add("selefunc_string","insert")
                .add("name", query_0)
                .add("grp", query_1)
                .add("address", query_2)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        httpstate = 0;
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            httpstate = response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    public static void newPlan(String b_name,String b_d,String b_sd,String b_fd,String b_gid) {//新增
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------

        FormBody body = new FormBody.Builder()//body指PHP?代入的參數
                .add("selefunc_string","newPlan")
                .add("itinerary_name",b_name)
                .add("itinerary_day", b_d)
                .add("start_date", b_sd)
                .add("finish_date", b_fd)
                .add("google_login_id",b_gid)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        httpstate = 0;
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            httpstate = response.code();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int aaa=0;

    }

    public static String addPoint(String b_aid,String b_id,String b_nd,String b_ord,String b_pd) {//新增
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        FormBody body = new FormBody.Builder()//body指PHP?代入的參數
                .add("selefunc_string","addPoint")
                .add("attraction_id", b_aid)
                .add("itinerary_id", b_id)
                .add("nth_day",b_nd)
                .add("order_number", b_ord)
                .add("itinerary_day", b_pd)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        httpstate = 0;
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            httpstate = response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    public static String updatePoint(ArrayList<String> query_string,String aaa) {
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        FormBody body = null;
        switch (aaa){
            case "time":{
                //--------------
                String query_0 = query_string.get(0);
                String query_1 = query_string.get(1);

                body = new FormBody.Builder()//body指PHP?代入的參數
                        .add("selefunc_string", "updateTime")
                        .add("sub_itinerary_id", query_0)
                        .add("stay_time", query_1)
                        .build();
                break;
            }
            case "txt":{
                //--------------
                String query_0 = query_string.get(0);
                String query_1 = query_string.get(1);

                body = new FormBody.Builder()//body指PHP?代入的參數
                        .add("selefunc_string", "updateTXT")
                        .add("sub_itinerary_id", query_0)
                        .add("note", query_1)
                        .build();
                break;
            }
        }

//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String updateTXT(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);

        FormBody body = new FormBody.Builder()//body指PHP?代入的參數
                .add("selefunc_string", "updateTXT")
                .add("sub_itinerary_id", query_0)
                .add("note", query_1)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeDelet(ArrayList<String> query_string,String aa) { //刪除行程
//        OkHttpClient client = new OkHttpClient();
        FormBody body = null;
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        switch (aa){
            case "i01":{
                body = new FormBody.Builder()
                        .add("selefunc_string","delete_i01")
                        .add("id", query_0)
                        .build();
                break;
            }
            case "m01":{
                body = new FormBody.Builder()
                        .add("selefunc_string","delete_m01")
                        .add("id", query_0)
                        .build();
                break;
            }
            case "i02":{
                body = new FormBody.Builder()
                        .add("selefunc_string","delete_i02")
                        .add("id", query_0)
                        .build();
                break;
            }
            case "i02d":{
                String query_1 = query_string.get(1);
                body = new FormBody.Builder()
                        .add("selefunc_string","delete_i02d")
                        .add("itinerary_id", query_0)
                        .add("nth_day", query_1)
                        .build();
                break;
            }
        }
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
//        ------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
//        -------------

        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeUpdate(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);
        String query_2 = query_string.get(2);
        String query_3 = query_string.get(3);

        FormBody body = new FormBody.Builder()//body指PHP?代入的參數
                .add("selefunc_string", "update")
                .add("name", query_0)
                .add("grp", query_1)
                .add("address", query_2)
                .add("id", query_3)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        httpstate = 0;
        try (Response response = client.newCall(request).execute()) {//執行HTTP命令
            httpstate = response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //---刪除資料--------------------------------------------------------------
    public static String executeDelet(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete")
                .add("id", query_0)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        httpstate = 0;
        try (Response response = client.newCall(request).execute()) {
            httpstate = response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //==============================================================================================
    //------09------2021/01/30------
//    public static String executeQuery(ArrayList<String> query_string) {//載入(讀取資料)
////        OkHttpClient client = new OkHttpClient();
//        postUrl=connect_ip ;
//        //--------------
//        String query_0 = query_string.get(0);
//        FormBody body = new FormBody.Builder() //要丟給某某變數=什麼值
//                .add("selefunc_string","query")
//                .add("query_string", query_0)
//                .build();
//        //--------------
//        Request request = new Request.Builder()
//                .url(postUrl)
//                .post(body)
//                .build();
//        //------------
//        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
//        httpstate = 0;   //設 httpcode初始值
//        //-------------
//        try (Response response = client.newCall(request).execute()) { //執行http命令
//            httpstate=response.code();
//            return response.body().string();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }

    public static String executeDelet_09i01(ArrayList<String> query_string) { //刪除行程
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ; //2021/02/06
        //--------------
        String query_0 = query_string.get(0);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete_i01")
                .add("id", query_0)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
//        ------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
//        -------------

        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String executeDelet_09m01(ArrayList<String> query_string) { //刪除回憶
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ; //2021/02/06
        //--------------
        String query_0 = query_string.get(0);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","delete_m01")
                .add("id", query_0)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
//        ------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
//        -------------

        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    //------09------over------
    //==============================================================================================
    public static String executeFavoriteInsert(ArrayList<String> query_string) {//新增
//        OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);
        String query_2 = query_string.get(2);
        String query_3 = query_string.get(3);
        String query_4 = query_string.get(4);
        String query_5 = query_string.get(5);
        String query_6 = query_string.get(6);

        FormBody body = new FormBody.Builder()//body指PHP?代入的參數
                .add("selefunc_string", "insertFavorite")
                .add("google_login_id", query_0)
                .add("attraction_id", query_1)
                .add("attraction_name", query_2)
                .add("attraction_address", query_3)
                .add("longitude", query_4)
                .add("latitude", query_5)
                .add("tbd1", query_6)
                .build();
//--------------

        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
//        ------------
        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值
//        -------------

        try (Response response = client.newCall(request).execute()) {
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //==============================================================================================

    //---刪除資料--------------------------------------------------------------
    public static String executeFavoriteDelete(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);

        FormBody body = new FormBody.Builder()
                .add("selefunc_string","deleteFavorite")
                .add("google_login_id", query_0)
                .add("attraction_id", query_1)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        httpstate = 0;
        try (Response response = client.newCall(request).execute()) {
            httpstate = response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    //==============================================================================================
    //11
    //    新增使用者Msql11
    public static String executeInsert_11(ArrayList<String> query_string) {
//        OkHttpClient client = new OkHttpClient();
        postUrl=connect_ip ;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);
        String query_2 = query_string.get(2);
        String query_3 = query_string.get(3);
        String query_4 = query_string.get(4);
        String query_5 = query_string.get(5);
        String query_6 = query_string.get(6);


        FormBody body = new FormBody.Builder()//FormBody表示php檔案?後面內容
                .add("selefunc_string","insert11")
                .add("google_login_id", query_0)
                .add("profile_picture", query_1)
                .add("email", query_2)
                .add("nickname", query_3)
                .add("first_name", query_4)
                .add("last_name", query_5)
                .add("access_level", query_6)
                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();

        // 使用httpResponse的方法取得http 狀態碼設定給httpstate變數
        httpstate = 0;   //設 httpcode初始值

        try (Response response = client.newCall(request).execute()) {//執行http命令
            httpstate=response.code();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
    //---更新使用者Msql11--------------------------------------------------------------
    public static String executeUpdate_11(ArrayList<String> query_string) {
// OkHttpClient client = new OkHttpClient();
        postUrl = connect_ip;
        //--------------
        String query_0 = query_string.get(0);
        String query_1 = query_string.get(1);
        String query_2 = query_string.get(2);
        String query_3 = query_string.get(3);
        String query_4 = query_string.get(4);
        String query_5 = query_string.get(5);
        String query_6 = query_string.get(6);

        FormBody body = new FormBody.Builder()//FormBody表示php檔案?後面內容
                .add("selefunc_string","update11")
                .add("google_login_id", query_0)
                .add("profile_picture", query_1)
                .add("email", query_2)
                .add("nickname", query_3)
                .add("first_name", query_4)
                .add("last_name", query_5)
                .add("access_level", query_6)

                .build();
//--------------
        Request request = new Request.Builder()
                .url(postUrl)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}
