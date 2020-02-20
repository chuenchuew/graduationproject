package com.example.admin.graduationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UserInfoActivity extends AppCompatActivity {

    private String Nickname;
    private String Department;
    private String School;
    private String UID;

    private Button button_user_info_return;
    private Button button_user_info_shoppingbag;
    private Button button_user_info_worklist;
    private Button button_user_info_sell;
    private Button button_user_info_work;
    private Button button_user_info_setting;
    private Button button_user_info_record;

    private ListView userInfo_listview;

    private List<Work> user_workList= new ArrayList<>();
    private List<Item> user_itemList = new ArrayList<>();

    private String JsonURL_Sell = "http://www.fcupapper.16mb.com/papper/get/single_search/sell.php?uid=";
    private String JsonURL_Work = "http://www.fcupapper.16mb.com/papper/get/single_search/work.php?uid=";
    private String JsonURL_Shoppingbag = "http://www.fcupapper.16mb.com/papper/get/single_search/focus.php?fuid=";
    private String JsonURL_Shoppingbag_search = "";
    private String JsonURL_Sell_Search = "";
    private String JsonURL_Work_Search = "";
    private String JsomURL_Focus_Delete = "http://www.fcupapper.16mb.com/papper/post/delete/focus.php";

    private int change_listview  = 2; //0購物車 1工作車 2我的商品 3我的工作

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userInfo_listview = (ListView)findViewById(R.id.user_info_listView);
        //listview點擊事件
        SetOnItemClickListener_UserInfo_Form();
        //個人資訊設定
        UserInfo_Set();
        //Button Listener
        OnClickButtonListener_UserInfo_Return();
        OnClickButtonListener_UserInfo_ShoppingBag();
        OnClickButtonListener_UserInfo_WorCar();
        OnClickButtonListener_UserInfo_Sell();
        OnClickButtonListener_UserInfo_Work();
        OnClickButtonListener_UserInfo_AccountSetting();
        OnClickButtonListener_UserInfo_Record();
        //listview long click
        SetOnItemLongClickListener_Delete_Message();
        //我的商品listview
        UserInfo_MySellForm();
    }

    private void UserInfo_Set(){
        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        Nickname = userinfo.getString("Nick","");
        Department = userinfo.getString("Class","");
        School = userinfo.getString("School","");
        UID = userinfo.getString("UID","");

        TextView seller_info_nickname = (TextView) findViewById(R.id.user_info_nickname);
        seller_info_nickname.setText(Nickname);

        TextView seller_info_department = (TextView) findViewById(R.id.user_info_department);
        seller_info_department.setText(Department);

        TextView seller_info_school = (TextView) findViewById(R.id.user_info_school);
        seller_info_school.setText(School);
    }

    private void SetOnItemClickListener_UserInfo_Form(){
        userInfo_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(change_listview == 0){
                    Intent SellFormActivity = new Intent(UserInfoActivity.this, SellFormActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("FNO", id);//傳遞ID
                    SellFormActivity.putExtras(bundle);

                    startActivity(SellFormActivity);
                }
                else if(change_listview == 1){
                    Intent WorkFormActivity = new Intent(UserInfoActivity.this, WorkFormActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("WNO",id);//傳遞ID
                    WorkFormActivity.putExtras(bundle);

                    startActivity(WorkFormActivity);
                }
                else if(change_listview == 2) {
                    Intent MySellFormActivity = new Intent(UserInfoActivity.this, MySellFormActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("FNO", id);//傳遞ID
                    MySellFormActivity.putExtras(bundle);

                    startActivity(MySellFormActivity);
                }
                else if(change_listview == 3) {
                    Intent MyWorkFormActivity = new Intent(UserInfoActivity.this, MyWorkFormActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("WNO", id);//傳遞ID
                    MyWorkFormActivity.putExtras(bundle);

                    startActivity(MyWorkFormActivity);
                }
                else{
                    Log.d("error","error");
                }
            }
        });
    }

    private void OnClickButtonListener_UserInfo_AccountSetting() {
        button_user_info_setting = (Button) findViewById(R.id.button_user_info_setting);

        button_user_info_setting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent AccountSettingActivity = new Intent(UserInfoActivity.this, AccountSettingActivity.class);
                        startActivity(AccountSettingActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfo_Return() {
        button_user_info_return = (Button) findViewById(R.id.button_user_info_return);

        button_user_info_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfo_Record() {
        button_user_info_record = (Button) findViewById(R.id.button_user_info_record);

        button_user_info_record.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent RecordActivity = new Intent(UserInfoActivity.this, RecordActivity.class);
                        startActivity(RecordActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfo_ShoppingBag() {
        button_user_info_shoppingbag = (Button) findViewById(R.id.button_user_info_shoppingbag);

        button_user_info_shoppingbag.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_listview = 0;
                        UserInfo_ShoppingBag();
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfo_WorCar() {
        button_user_info_worklist = (Button) findViewById(R.id.button_user_info_worklist);

        button_user_info_worklist.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_listview = 1;
                        UserInfo_Workcar();
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfo_Sell() {
        button_user_info_sell = (Button) findViewById(R.id.button_user_info_sell);

        button_user_info_sell.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_listview = 2;
                        UserInfo_MySellForm();
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfo_Work() {
        button_user_info_work = (Button) findViewById(R.id.button_user_info_work);

        button_user_info_work.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        change_listview = 3;
                        UserInfo_MyWorkForm();
                    }
                }
        );
    }

    private void SetOnItemLongClickListener_Delete_Message(){

        userInfo_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                if(change_listview == 0 || change_listview == 1) {
                    Dialog_Focus_Delete(l);
                }
                return true;
            }
        });
    }

    private void Dialog_Focus_Delete(final long l){
        final String fno = String.valueOf(l);
        AlertDialog.Builder dialog = new AlertDialog.Builder(UserInfoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_check, null);
        TextView Text = (TextView) dialogView.findViewById(R.id.dialog_form_check_text);
        if(change_listview == 0 ) {
            Text.setText("是否要從購物車移除該表單?");
            dialog.setView(dialogView).setTitle("購物車商品刪除確認");//載入對話框 的資源黨設定
        }
        else if(change_listview == 1){
            Text.setText("是否要從工作車移除該表單?");
            dialog.setView(dialogView).setTitle("工作車打工刪除確認");//載入對話框 的資源黨設定
        }
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserInfoFocusDelete(fno);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private class Sell_Adapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Item> itemList;

        private ImageLoader imageLoader;

        private Sell_Adapter(Context context, List<Item> itemList){
            layoutInflater = LayoutInflater.from(context);
            this.itemList = itemList;
            requestQueue = Volley.newRequestQueue(context);
            imageLoader = CustomVolleyRequest.getInstance(context.getApplicationContext()).getImageLoader();
        }
        public int getCount(){
            return itemList.size();
        }

        public Object getItem(int position){
            return itemList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return itemList.get(position).getId();
        }

        public View getView(int position,View convertView,ViewGroup parent){
            if(convertView==null) convertView = layoutInflater.inflate(R.layout.listview_main, parent, false);//取得listItem容器 view

            Item item = itemList.get(position);

            TextView Text_Name = (TextView) convertView.findViewById(R.id.listview_main_name);
            Text_Name.setText(item.getName());

            TextView Text_Price = (TextView) convertView.findViewById(R.id.listview_main_price);
            Text_Price.setText(item.getPrice());

            TextView Text_Number = (TextView) convertView.findViewById(R.id.listview_main_number);
            Text_Number.setText(item.getNumber());

            TextView Text_Status = (TextView) convertView.findViewById(R.id.listview_main_status);
            Text_Status.setText(item.getStatus());

            NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image_listview_main_image);
            imageView.setImageUrl(item.getImageURL(), imageLoader);
            imageView.setErrorImageResId(R.mipmap.defaultimage);

            return convertView;
        }
    }

    private class Work_Adapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Work> workList;

        private Work_Adapter(Context context ,List<Work> workList){
            layoutInflater = LayoutInflater.from(context);
            this.workList = workList;

        }
        public int getCount(){
            return workList.size();
        }

        public Object getItem(int position){
            return workList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return workList.get(position).getId();
        }

        public View getView(int position,View convertView,ViewGroup parent){
            if(convertView==null) convertView=layoutInflater.inflate(R.layout.listview_main_work,parent,false);
            Work work = workList.get(position);
            TextView Name = (TextView) convertView.findViewById(R.id.listview_main_work_name);
            Name.setText(work.getName());

            TextView Salary = (TextView) convertView.findViewById(R.id.listview_main_work_salary);
            Salary.setText(work.getSalary());

            TextView Hours = (TextView) convertView.findViewById(R.id.listview_main_work_hours);
            Hours.setText(work.getHours());

            TextView Text_Status = (TextView) convertView.findViewById(R.id.listview_main_work_status);
            Text_Status.setText(work.getStatus());
            return convertView;
        }
    }

    private void UserInfo_ShoppingBag(){
        JsonURL_Shoppingbag_search = JsonURL_Shoppingbag + UID;
        Log.d("JsonURL_form_search",JsonURL_Shoppingbag_search);//除錯用
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_UserInfo_ShoppingBag = new JsonObjectRequest(Request.Method.GET, JsonURL_Shoppingbag_search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Focus");
                            user_itemList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                if(array.getJSONObject(i).getString("Status").equals("Sell")) {
                                    user_itemList.add(new Item(array.getJSONObject(i).getString("FNO")
                                            , array.getJSONObject(i).getString("Title")
                                            , array.getJSONObject(i).getString("Number")
                                            , array.getJSONObject(i).getString("SPrice")
                                            , array.getJSONObject(i).getString("SURL")
                                            , array.getJSONObject(i).getString("FStatus")));
                                }
                            }
                            userInfo_listview.setAdapter(new Sell_Adapter(UserInfoActivity.this,user_itemList));
                        }
                        catch (Exception e) {
                            Log.e("jsonException",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(obreq_UserInfo_ShoppingBag);
    }

    private void UserInfo_Workcar(){
        JsonURL_Shoppingbag_search = JsonURL_Shoppingbag + UID;
        Log.d("JsonURL_form_search",JsonURL_Shoppingbag_search);//除錯用
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_UserInfo_ShoppingBag = new JsonObjectRequest(Request.Method.GET, JsonURL_Shoppingbag_search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Focus");
                            user_workList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                if(array.getJSONObject(i).getString("Status").equals("Work")) {
                                    user_workList.add(new Work(array.getJSONObject(i).getString("FNO")
                                            , array.getJSONObject(i).getString("Title")
                                            , array.getJSONObject(i).getString("Salary")
                                            , array.getJSONObject(i).getString("WorkTime")
                                            , array.getJSONObject(i).getString("FStatus")));
                                }
                            }
                            userInfo_listview.setAdapter(new Work_Adapter(UserInfoActivity.this,user_workList));
                        }
                        catch (Exception e) {
                            Log.e("jsonException",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(obreq_UserInfo_ShoppingBag);
    }

    private void UserInfo_MySellForm(){
        JsonURL_Sell_Search = JsonURL_Sell + UID;
        Log.d("JsonURL_Seller_Search",JsonURL_Sell_Search);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_UserInfo_Sell = new JsonObjectRequest(Request.Method.GET, JsonURL_Sell_Search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Sell");
                            user_itemList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                user_itemList.add(new Item(array.getJSONObject(i).getString("FNO")
                                        , array.getJSONObject(i).getString("Title")
                                        , array.getJSONObject(i).getString("Number")
                                        , array.getJSONObject(i).getString("SPrice")
                                        , array.getJSONObject(i).getString("SURL")
                                        , "0"));
                            }
                            userInfo_listview.setAdapter(new Sell_Adapter(UserInfoActivity.this,user_itemList));
                        }
                        catch (Exception e) {
                            Log.e("jsonException",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(obreq_UserInfo_Sell);
    }

    private void UserInfo_MyWorkForm(){
        JsonURL_Work_Search = JsonURL_Work + UID;
        Log.d("JsonURL_Seller_Search",JsonURL_Work_Search);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_UserInfo_Work = new JsonObjectRequest(Request.Method.GET, JsonURL_Work_Search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Work");
                            user_workList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                user_workList.add(new Work(array.getJSONObject(i).getString("FNO")
                                        ,array.getJSONObject(i).getString("Title")
                                        ,array.getJSONObject(i).getString("Salary")
                                        ,array.getJSONObject(i).getString("WorkTime")
                                        ,"0"));
                                Log.d("FNO",array.getJSONObject(i).getString("FNO"));
                            }
                            userInfo_listview.setAdapter(new Work_Adapter(UserInfoActivity.this, user_workList));
                        }
                        catch (Exception e) {
                            Log.e("jsonException",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(obreq_UserInfo_Work);
    }

    private void UserInfoFocusDelete(final String fno) {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MySellFormDeleteMessage = new StringRequest(Request.Method.POST,
                JsomURL_Focus_Delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(UserInfoActivity.this, "表單刪除成功", Toast.LENGTH_LONG).show();
                    if(change_listview == 0 ) {
                        UserInfo_ShoppingBag();
                    }
                    else if(change_listview == 1){
                        UserInfo_Workcar();
                    }
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(UserInfoActivity.this, "與伺服器連線異常請重試", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fno", fno);
                params.put("uid", UID);

                return params;
            }
        };
        requestQueue.add(obreq_MySellFormDeleteMessage);
    }
}
