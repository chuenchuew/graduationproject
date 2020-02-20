package com.example.admin.graduationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SellerInfoActivity extends AppCompatActivity {

    private Button button_seller_info_return;
    private Button button_seller_info_sell;
    private Button button_seller_info_work;
    private Button button_seller_info_score;

    private ListView sellerInfo_listview;

    private NetworkImageView imageView;
    private String JsomURL_image = "http://www.fcupapper.16mb.com/papper/post/uploads/";
    private String JsonURL_image_FNO = "";

    private String FEID = "";
    private String UID = "";
    private String Nick = "";
    private String School = "";
    private String Department = "";
    private String Star = "";
    private float Star_float;
    private float buyer_score;
    private boolean sellform = true;

    private List<Item> Seller_itemList = new ArrayList<>();
    private List<Work> Seller_workList = new ArrayList<>();

    String JsonURL_Sell = "http://www.fcupapper.16mb.com/papper/get/single_search/sell.php?";
    String JsonURL_Work = "http://www.fcupapper.16mb.com/papper/get/single_search/work.php?";
    String JsonURL_SellerInfo = "http://www.fcupapper.16mb.com/papper/get/single_search/info.php?";
    String JsonURL_Sell_Search = "";
    String JsonURL_Work_Search = "";
    String JsonURL_SellerInfo_Search = "";
    String JsonURL_SellerInfo_UpdataScore = "http://www.fcupapper.16mb.com/papper/post/update/user.php";

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_info);

        Bundle bundle = getIntent().getExtras();
        FEID = bundle.getString("FEID");

        sellerInfo_listview = (ListView)findViewById(R.id.seller_info_listView);
        sellerInfo_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(sellform) {
                    Intent SellFormActivity = new Intent(SellerInfoActivity.this, SellFormActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("FNO",id);//傳遞ID
                    SellFormActivity.putExtras(bundle);

                    startActivity(SellFormActivity);
                }
                else {
                    Intent WorkFormActivity = new Intent(SellerInfoActivity.this, WorkFormActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putLong("WNO",id);//傳遞ID
                    WorkFormActivity.putExtras(bundle);

                    startActivity(WorkFormActivity);
                }
            }
        });

        GetSellerInfo();

        OnClickButtonListener_sellerInfo_Return();
        OnClickButtonListener_SellerInfo_Sell();
        OnClickButtonListener_SellerInfo_Work();
        OnClickButtonListener_SellerInfo_Score();

        Volley_Sell();
    }

    private void OnClickButtonListener_sellerInfo_Return() {
        button_seller_info_return = (Button) findViewById(R.id.button_seller_info_return);

        button_seller_info_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_SellerInfo_Score(){
        button_seller_info_score = (Button) findViewById(R.id.button_seller_info_score);

        button_seller_info_score.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SellerInfoScore();
                    }
                }
        );
    }

    private void OnClickButtonListener_SellerInfo_Sell() {
        button_seller_info_sell = (Button) findViewById(R.id.button_seller_info_sell);

        button_seller_info_sell.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellform = true;
                        Volley_Sell();
                    }
                }
        );
    }

    private void OnClickButtonListener_SellerInfo_Work() {
        button_seller_info_work = (Button) findViewById(R.id.button_seller_info_work);

        button_seller_info_work.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sellform = false;
                        Volley_Work();
                    }
                }
        );
    }

    private void SellerInfoScore(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(SellerInfoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_seller_info_score, null);
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("給他評分");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText editText_seller_info_score = (EditText) dialogView.findViewById(R.id.editText_seller_info_score);
                if(!editText_seller_info_score.getText().toString().equals("")){
                    buyer_score = (Float.valueOf(editText_seller_info_score.getText().toString()) + Star_float)/2;
                    SellerInfo_Score();
                }
            }
        });
        dialog.setNegativeButton("取消/清除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private void SetSellerScore(){
        Log.d("Star_float",String.valueOf(Star_float));
        if(Star_float <= 1){
            TextView seller_info_score = (TextView) findViewById(R.id.seller_info_score);
            seller_info_score.setText("★");
        }
        else if(Star_float > 1 && Star_float <= 2){
            TextView seller_info_score = (TextView) findViewById(R.id.seller_info_score);
            seller_info_score.setText("★★");
        }
        else if(Star_float > 2 && Star_float <= 3){
            TextView seller_info_score = (TextView) findViewById(R.id.seller_info_score);
            seller_info_score.setText("★★★");
        }
        else if(Star_float > 3 && Star_float <= 4){
            TextView seller_info_score = (TextView) findViewById(R.id.seller_info_score);
            seller_info_score.setText("★★★★");
        }
        else{
            TextView seller_info_score = (TextView) findViewById(R.id.seller_info_score);
            seller_info_score.setText("★★★★★");
        }
    }

    private void SetSellerInfo(){
        TextView seller_info_nickname = (TextView) findViewById(R.id.seller_info_nickname);
        seller_info_nickname.setText(Nick);

        TextView seller_info_school = (TextView) findViewById(R.id.seller_info_school);
        seller_info_school.setText(School);

        TextView seller_info_department = (TextView) findViewById(R.id.seller_info_department);
        seller_info_department.setText(Department);
    }

    private class Sell_Adapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Item> itemList;

        private ImageLoader imageLoader;

        private Sell_Adapter(Context context, List<Item> Seller_itemList){
            layoutInflater = LayoutInflater.from(context);
            this.itemList = Seller_itemList;

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

            imageView = (NetworkImageView) convertView.findViewById(R.id.image_listview_main_image);
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

    private void Volley_Sell(){
        JsonURL_Sell_Search = JsonURL_Sell + "uid=" + FEID;
        Log.d("JsonURL_Seller_Search",JsonURL_Sell_Search);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_Itemlist_All = new JsonObjectRequest(Request.Method.GET, JsonURL_Sell_Search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Sell");
                            Seller_itemList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                Seller_itemList.add(new Item(array.getJSONObject(i).getString("FNO")
                                        , array.getJSONObject(i).getString("Title")
                                        , array.getJSONObject(i).getString("Number")
                                        , array.getJSONObject(i).getString("SPrice")
                                        , array.getJSONObject(i).getString("SURL")
                                        , "0"));
                            }
                            sellerInfo_listview.setAdapter(new Sell_Adapter(SellerInfoActivity.this,Seller_itemList));
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
        requestQueue.add(obreq_Itemlist_All);
    }

    private void Volley_Work(){
        JsonURL_Work_Search = JsonURL_Work + "uid=" + FEID;
        Log.d("JsonURL_Seller_Search",JsonURL_Work_Search);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_Worklist = new JsonObjectRequest(Request.Method.GET, JsonURL_Work_Search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Work");
                            Seller_workList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                Seller_workList.add(new Work(array.getJSONObject(i).getString("FNO")
                                        ,array.getJSONObject(i).getString("Title")
                                        ,array.getJSONObject(i).getString("Salary")
                                        ,array.getJSONObject(i).getString("WorkTime")
                                        ,"0"));
                            }
                            sellerInfo_listview.setAdapter(new Work_Adapter(SellerInfoActivity.this, Seller_workList));
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
        requestQueue.add(obreq_Worklist);
    }

    private void GetSellerInfo(){
        JsonURL_SellerInfo_Search = JsonURL_SellerInfo + "id=" + FEID;
        Log.d("SellerInfo_Search", JsonURL_SellerInfo_Search);
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_Seller_Info = new JsonObjectRequest(Request.Method.GET, JsonURL_SellerInfo_Search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("memberinfo");

                            UID = array.getJSONObject(0).getString("UID");
                            Nick = array.getJSONObject(0).getString("Nick");
                            School = array.getJSONObject(0).getString("School");
                            Department = array.getJSONObject(0).getString("Class");
                            Star = array.getJSONObject(0).getString("Star");
                            Star_float = Float.valueOf(Star);

                            Log.d("UID",UID);
                            Log.d("Nick",Nick);
                            Log.d("School",School);
                            Log.d("Department",Department);

                            SetSellerInfo();
                            SetSellerScore();
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
        requestQueue.add(obreq_Seller_Info);
    }

    private void SellerInfo_Score() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_SellerInfo_Score = new StringRequest(Request.Method.POST,
                JsonURL_SellerInfo_UpdataScore, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("uid", FEID);
                params.put("star", String.valueOf(buyer_score));

                return params;
            }
        };
        requestQueue.add(obreq_SellerInfo_Score);
    }

    private void LoadImage(String FNO){
        JsonURL_image_FNO = JsomURL_image + FNO;
        requestQueue = Volley.newRequestQueue(this);
        ImageRequest request_LoadImage = new ImageRequest(JsonURL_image_FNO,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageResource(R.mipmap.defaultimage);
                    }
                });
        requestQueue.add(request_LoadImage);
    }
}
