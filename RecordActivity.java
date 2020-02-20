package com.example.admin.graduationproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecordActivity extends AppCompatActivity {

    private Button button_record_return;
    private Button button_record_buy;
    private Button button_record_work;

    private ListView record_listview;

    private List<Work> record_workList= new ArrayList<>();
    private List<Item> record_itemList = new ArrayList<>();

    private String UID = "";

    private String JsonURL_sell = "http://www.fcupapper.16mb.com/papper/get/combine/history.php?status=sell";
    private String JsonURL_work = "http://www.fcupapper.16mb.com/papper/get/combine/history.php?status=work";

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        record_listview = (ListView)findViewById(R.id.record_listview);

        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        UID = userinfo.getString("UID","");

        Record_BuyForm();

        OnClickButtonListener_Record_Return();
        OnClickButtonListener_Record_Buy();
        OnClickButtonListener_Record_Work();
    }

    private void OnClickButtonListener_Record_Return() {
        button_record_return = (Button) findViewById(R.id.button_record_return);

        button_record_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_Record_Buy() {
        button_record_buy = (Button) findViewById(R.id.button_record_buy);

        button_record_buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Record_BuyForm();
                    }
                }
        );
    }

    private void OnClickButtonListener_Record_Work() {
        button_record_work = (Button) findViewById(R.id.button_record_work);

        button_record_work.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Record_WorkForm();
                    }
                }
        );
    }

    private class Sell_Adapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Item> itemList;
        private ImageLoader imageLoader;

        private Sell_Adapter(Context context, List<Item> Sell_itemList){
            layoutInflater = LayoutInflater.from(context);
            this.itemList = Sell_itemList;
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

        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView==null) convertView = layoutInflater.inflate(R.layout.listview_main, parent, false);//取得listItem容器 view

            Item item = itemList.get(position);

            TextView Text_Name = (TextView) convertView.findViewById(R.id.listview_main_name);
            Text_Name.setText(item.getName());

            TextView Text_Price = (TextView) convertView.findViewById(R.id.listview_main_price);
            Text_Price.setText(item.getPrice());

            TextView Text_Number = (TextView) convertView.findViewById(R.id.listview_main_number);
            Text_Number.setText(item.getNumber());

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

        public View getView(int position,View converView,ViewGroup parent){
            if(converView==null) converView=layoutInflater.inflate(R.layout.listview_main_work,parent,false);
            Work work = workList.get(position);
            TextView Name = (TextView)converView.findViewById(R.id.listview_main_work_name);
            Name.setText(work.getName());

            TextView Salary = (TextView)converView.findViewById(R.id.listview_main_work_salary);
            Salary.setText(String.valueOf(work.getSalary()));

            TextView Hours = (TextView)converView.findViewById(R.id.listview_main_work_hours);
            Hours.setText(String.valueOf(work.getHours()));
            return converView;
        }
    }

    private void Record_BuyForm(){
        requestQueue = Volley.newRequestQueue(this);
        Log.d("JsonURL_sell",JsonURL_sell);
        JsonObjectRequest obreq_Record_BuyForm = new JsonObjectRequest(Request.Method.GET, JsonURL_sell,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("History");
                            record_itemList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                if(array.getJSONObject(i).getString("BTUID").equals(UID)) {
                                    record_itemList.add(new Item(array.getJSONObject(i).getString("FNO")
                                            , array.getJSONObject(i).getString("Title")
                                            , array.getJSONObject(i).getString("Number")
                                            , array.getJSONObject(i).getString("SPrice")
                                            , array.getJSONObject(i).getString("SURL")
                                            , "0"));
                                }
                            }
                            record_listview.setAdapter(new Sell_Adapter(RecordActivity.this,record_itemList));
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
        requestQueue.add(obreq_Record_BuyForm);
    }

    private void Record_WorkForm(){
        requestQueue = Volley.newRequestQueue(this);
        Log.d("JsonURL_work",JsonURL_work);
        JsonObjectRequest obreq_Record_WorkForm = new JsonObjectRequest(Request.Method.GET, JsonURL_work,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("History");
                            record_workList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                if(array.getJSONObject(i).getString("BTUID").equals(UID)) {
                                    record_workList.add(new Work(array.getJSONObject(i).getString("FNO")
                                            , array.getJSONObject(i).getString("Title")
                                            , array.getJSONObject(i).getString("Salary")
                                            , array.getJSONObject(i).getString("WorkTime")
                                            , "0"));
                                }
                            }
                            record_listview.setAdapter(new Work_Adapter(RecordActivity.this, record_workList));
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
        requestQueue.add(obreq_Record_WorkForm);
    }
}
