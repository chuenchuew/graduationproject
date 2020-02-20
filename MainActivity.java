package com.example.admin.graduationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Button button_create_sell_form;
    private Button button_main_user_info;
    private Button button_main_work_mode;
    private Button button_filter;
    private Button button_main_sell_mode;

    private ListView main_listview;

    private Spinner spinner_main_classification;
    private ArrayAdapter<String> classificationList;
    private String[] classification = {"全部","居家生活", "3C產品", "運動休閒", "學生用品", "服飾鞋子配件","食物","雜物","歷史紀錄"};

    private String text_main_type;
    private String text_main_type_UTF_8 = "";
    private String URL_text_main_type_UTF_8 = "";
    private String searchtitle_UTF_8 = "";
    private String URL_searchtitle_UTF_8 = "";
    private String min_price = "";
    private String max_price = "";
    private String Nickname = "";


    private String JsonURL_form = "http://fcupapper.16mb.com/papper/get/combine/sell.php?";
    private String JsonURL_form_search = "http://fcupapper.16mb.com/papper/get/combine/sell.php?";
    private String JsonURL_sellformrecord = "http://www.fcupapper.16mb.com/papper/get/combine/history.php?status=sell";

    RequestQueue requestQueue;

    List<Item> itemList_Search = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //set welcome + nickname
        Welcome_Nickname();
        //SearchView
        SetSearchView();
        //listview
        main_listview = (ListView)findViewById(R.id.main_listview);
        main_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent SellFormActivity = new Intent(MainActivity.this, SellFormActivity.class);
                Bundle bundle = new Bundle();
                bundle.putLong("FNO",id);//傳遞ID
                SellFormActivity.putExtras(bundle);
                startActivity(SellFormActivity);
            }
        });
        //Spinner
        Spinner();
        //Button
        OnClickButtonListener_CreateSellFormActivity();
        OnClickButtonListener_WorkMode();
        OnClickButtonListener_UserInfo();
        OnClickButtonListener_SellMode();
        //listview
        Item_ListView_Search();
        //refresh listview
        SwipeRefresh();
    }

    private void SwipeRefresh(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Item_ListView_Search();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() != 0) { //搜尋框有值時
            try {
                searchtitle_UTF_8 = URLEncoder.encode(newText, "UTF-8");
                URL_searchtitle_UTF_8 = "title=" + searchtitle_UTF_8 + "&";
            } catch (UnsupportedEncodingException e) {
                Log.d("Tag", e.toString());
            }
        } else if (newText.length() == 0) { //搜尋框是空的時
            URL_searchtitle_UTF_8 = "";
            Item_ListView_Search();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Item_ListView_Search();
        return true;
    }

    private class Main_Adapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Item> itemList;

        private ImageLoader imageLoader;

        private Main_Adapter(Context context, List<Item> itemList){
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

            NetworkImageView imageView = (NetworkImageView) convertView.findViewById(R.id.image_listview_main_image);
            imageView.setImageUrl(item.getImageURL(), imageLoader);
            imageView.setErrorImageResId(R.mipmap.defaultimage);

            return convertView;
        }
    }

    private void Welcome_Nickname(){
        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        Nickname = userinfo.getString("Nick","");

        TextView welcome_nickname = (TextView) findViewById(R.id.main_welcome_nickname);
        welcome_nickname.setText(Nickname);
    }

    private void SetSearchView(){
        SearchView searchView = (SearchView) findViewById(R.id.main_searchiew);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true); //是否要點選搜尋圖示後再打開輸入框
        searchView.setSubmitButtonEnabled(true);//輸入框後是否要加上送出的按鈕
        searchView.setQueryHint("請輸入商品名稱"); //輸入框沒有值時要顯示的提示文字
    }

    private void OnClickButtonListener_CreateSellFormActivity() {
        button_create_sell_form = (Button) findViewById(R.id.button_main_create_sell_form);

        button_create_sell_form.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent CreateSellFormActivity = new Intent(MainActivity.this, CreateSellFormActivity.class);
                        startActivity(CreateSellFormActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfo() {
        button_main_user_info = (Button) findViewById(R.id.button_main_user_info);

        button_main_user_info.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent UserInfoActivity = new Intent(MainActivity.this, UserInfoActivity.class);
                        startActivity(UserInfoActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_SellMode() {
        button_main_sell_mode = (Button) findViewById(R.id.button_main_sell_mode);

        button_main_sell_mode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item_ListView_Search();
                    }
                }
        );
    }

    private void OnClickButtonListener_WorkMode() {
        button_main_work_mode = (Button) findViewById(R.id.button_main_work_mode);

        button_main_work_mode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent MainWorkActivity = new Intent(MainActivity.this, MainWorkActivity.class);
                        startActivity(MainWorkActivity);
                        finish();
                    }
                }
        );
    }

    private void Spinner(){
        spinner_main_classification = (Spinner)findViewById(R.id.spinner_main_classification);
        classificationList = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, classification);
        spinner_main_classification.setAdapter(classificationList);
        SetOnItemSelectedListener_spinner_main_classification();
    }

    private void SetOnItemSelectedListener_spinner_main_classification(){
        spinner_main_classification.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                text_main_type = adapterView.getSelectedItem().toString();
                if(text_main_type.equals(classification[0])) {
                    URL_text_main_type_UTF_8 = "";
                }
                else if(text_main_type.equals(classification[8])){
                    Record_SellFormRecord();
                }
                else {
                    try {
                        text_main_type_UTF_8 = URLEncoder.encode(text_main_type, "UTF-8");
                        URL_text_main_type_UTF_8 = "type=" + text_main_type_UTF_8 + "&";
                    } catch (UnsupportedEncodingException e) {
                        Log.d("Tag", e.toString());
                    }
                }
                Item_ListView_Search();
            }
            public void onNothingSelected(AdapterView arg0) {
            }
        });
    }

    public void Button_Main_Filter(View View){
        button_filter = (Button) findViewById(R.id.button_main_filter);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_main_filter, null);
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("篩選價格");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText text_dialog_losest_price = (EditText) dialogView.findViewById(R.id.dialog_losest_price);
                EditText text_dialog_highest_price = (EditText) dialogView.findViewById(R.id.dialog_highest_price);
                if(text_dialog_losest_price != null || text_dialog_highest_price != null) {
                    button_filter.setText(text_dialog_losest_price.getText() + "～" + text_dialog_highest_price.getText() + "元");
                    if (!text_dialog_losest_price.getText().toString().equals("")){
                        min_price = "min=" + text_dialog_losest_price.getText().toString() + "&";
                    }
                    if (!text_dialog_highest_price.getText().toString().equals("")){
                        max_price = "max=" + text_dialog_highest_price.getText().toString() + "&";
                    }
                    Item_ListView_Search();
                }
            }
        });
        dialog.setNegativeButton("取消/清除", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                button_filter.setText("篩選");
                min_price = "";
                max_price = "";
                Item_ListView_Search();
            }
        });
        dialog.show();
    }

    private void Item_ListView_Search(){
        requestQueue = Volley.newRequestQueue(this);
        JsonURL_form_search = JsonURL_form + URL_searchtitle_UTF_8 + URL_text_main_type_UTF_8 + min_price + max_price;
        Log.d("JsonURL_form_search",JsonURL_form_search);//除錯用
        JsonObjectRequest obreq_Itemlist_Search = new JsonObjectRequest(Request.Method.GET, JsonURL_form_search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Sell");
                            itemList_Search.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                itemList_Search.add(new Item(array.getJSONObject(i).getString("FNO")
                                        , array.getJSONObject(i).getString("Title")
                                        , array.getJSONObject(i).getString("Number")
                                        , array.getJSONObject(i).getString("SPrice")
                                        , array.getJSONObject(i).getString("SURL")
                                        , "0"));
                            }
                            main_listview.setAdapter(new Main_Adapter(MainActivity.this,itemList_Search));
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
        requestQueue.add(obreq_Itemlist_Search);
    }

    private void Record_SellFormRecord(){
        requestQueue = Volley.newRequestQueue(this);
        Log.d("JsonURL_sell",JsonURL_sellformrecord);
        JsonObjectRequest obreq_Record_BuyForm = new JsonObjectRequest(Request.Method.GET, JsonURL_sellformrecord,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("History");
                            itemList_Search.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                itemList_Search.add(new Item(array.getJSONObject(i).getString("FNO")
                                        , array.getJSONObject(i).getString("Title")
                                        , array.getJSONObject(i).getString("Number")
                                        , array.getJSONObject(i).getString("SPrice")
                                        , array.getJSONObject(i).getString("SURL")
                                        , "0"));
                            }
                            main_listview.setAdapter(new Main_Adapter(MainActivity.this,itemList_Search));
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
}
