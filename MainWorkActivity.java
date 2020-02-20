package com.example.admin.graduationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainWorkActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private Button button_work_sell_mode;
    private Button button_main_work_create_work_form;
    private Button button_work_filter;
    private Button button_main_work_work_list;
    private Button button_main_work_mode_work;

    private CheckBox main_work_worktimefilter;

    ListView main_work_listview;

    private String searchtitle_UTF_8;
    private String URL_searchtitle_UTF_8 = "";
    private String min_price = "";
    private String max_price = "";
    private String myworktime = "";
    private String Worktime_Form = "";
    private String Worktime_Begin = "";
    private String Worktime_End = "";
    private String[] WorktimeArray;

    private List<Work> user_workList= new ArrayList<>();

    String JsonURL_work = "http://fcupapper.16mb.com/papper/get/combine/work.php?";
    String JsonURL_work_search = "http://fcupapper.16mb.com/papper/get/combine/work.php?";
    String JsonURL_WorkCar = "http://fcupapper.16mb.com/papper/get/combine/work.php";

    private RequestQueue requestQueue;

    private List<Work> workList_Search = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_work);

        //SearchView
        SetSearchView();

        main_work_listview = (ListView) findViewById(R.id.main_work_listView);
        main_work_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(MainWorkActivity.this, WorkFormActivity.class);

                Bundle bundle = new Bundle();
                bundle.putLong("WNO",id);//傳遞ID
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        //Button
        OnClickButtonListener_SellMode();
        OnClickButtonListener_CreateWorkFormActivity();
        OnClickButtonListener_UserInfoActivity();
        OnClickButtonListener_WorkMode();
        //Checkbox
        OnCheckedChangeListener_Registered_Checkbox();
        //Get Work ListView
        Work_ListView_Search();
        //refresh listview
        SwipeRefresh();
    }

    private void SwipeRefresh(){
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.main_work_swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Work_ListView_Search();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override//SearchView 成員函式修改
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
            Work_ListView_Search();
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Work_ListView_Search();
        return true;
    }

    private class WorkAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private List<Work> workList;

        private WorkAdapter(Context context ,List<Work> workList){
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

    private void SetSearchView(){
        SearchView searchView = (SearchView) findViewById(R.id.main_work_searchview);
        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(true); //是否要點選搜尋圖示後再打開輸入框
        searchView.setSubmitButtonEnabled(true);//輸入框後是否要加上送出的按鈕
        searchView.setQueryHint("輸入工作名稱"); //輸入框沒有值時要顯示的提示文字
    }

    private void OnClickButtonListener_SellMode() {
        button_work_sell_mode = (Button) findViewById(R.id.button_main_work_mode_sell);

        button_work_sell_mode.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent MainActivity = new Intent(MainWorkActivity.this, MainActivity.class);
                        startActivity(MainActivity);
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_WorkMode() {
        button_main_work_mode_work = (Button) findViewById(R.id.button_main_work_mode_work);

        button_main_work_mode_work.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Work_ListView_Search();
                    }
                }
        );
    }

    private void OnClickButtonListener_CreateWorkFormActivity() {
        button_main_work_create_work_form = (Button) findViewById(R.id.button_main_work_create_work_form);

        button_main_work_create_work_form.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent CreateSellFormActivity = new Intent(MainWorkActivity.this ,CreateWorkFormActivity.class);
                        startActivity(CreateSellFormActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_UserInfoActivity() {
        button_main_work_work_list = (Button) findViewById(R.id.button_main_work_work_list);

        button_main_work_work_list.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent UserInfoActivity = new Intent(MainWorkActivity.this, UserInfoActivity.class);
                        startActivity(UserInfoActivity);
                    }
                }
        );
    }

    private void OnCheckedChangeListener_Registered_Checkbox(){
        main_work_worktimefilter = (CheckBox) findViewById(R.id.main_work_worktimefilter);

        main_work_worktimefilter.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if(isChecked) {
                    UserInfo_WorkCar();
                }//CheckBox狀態 : 已勾選,morning值為1
                else          {
                    Work_ListView_Search();
                }//CheckBox狀態 : 未勾選,morning值為空
            }
        });
    }

    public void Button_Main_Work_Filter(View View){
        button_work_filter = (Button) findViewById(R.id.button_main_work_filter);
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainWorkActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_main_work_filter, null);
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("篩選薪資");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText text_work_dialog_losest_price = (EditText) dialogView.findViewById(R.id.work_dialog_losest_price);
                EditText text_work_dialog_highest_price = (EditText) dialogView.findViewById(R.id.work_dialog_highest_price);
                if(text_work_dialog_losest_price != null || text_work_dialog_highest_price != null) {
                    button_work_filter.setText(text_work_dialog_losest_price.getText() + "～" + text_work_dialog_highest_price.getText() + "/hr");
                    if (!text_work_dialog_losest_price.getText().toString().equals("")){
                        min_price = "min=" + text_work_dialog_losest_price.getText().toString() + "&";
                    }
                    if (!text_work_dialog_highest_price.getText().toString().equals("")){
                        max_price = "max=" + text_work_dialog_highest_price.getText().toString() + "&";
                    }
                    Work_ListView_Search();
                }
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                button_work_filter.setText("篩選");
                min_price = "";
                max_price = "";
                Work_ListView_Search();
            }
        });
        dialog.show();
    }

    private void Work_ListView_Search(){
        JsonURL_work_search = JsonURL_work + URL_searchtitle_UTF_8 + min_price + max_price;
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_Worklist_Search = new JsonObjectRequest(Request.Method.GET, JsonURL_work_search,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Work");
                            workList_Search.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                workList_Search.add(new Work(array.getJSONObject(i).getString("FNO")
                                        ,array.getJSONObject(i).getString("Title")
                                        ,array.getJSONObject(i).getString("Salary")
                                        ,array.getJSONObject(i).getString("WorkTime")
                                        ,"0"));

                            }
                            main_work_listview.setAdapter(new WorkAdapter(MainWorkActivity.this, workList_Search));
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
        requestQueue.add(obreq_Worklist_Search);
    }

    private void UserInfo_WorkCar(){
        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        myworktime = userinfo.getString("EHour","");
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_UserInfo_WorkCar = new JsonObjectRequest(Request.Method.GET, JsonURL_WorkCar,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Work");
                            user_workList.clear();
                            for (int i = 0 ; i < array.length() ; i++){

                                Worktime_Form = array.getJSONObject(i).getString("WorkTime");
                                WorktimeArray = Worktime_Form.split("-");
                                Worktime_Begin = WorktimeArray[0];
                                Worktime_End = WorktimeArray[1];
                                if(myworktime.indexOf("1") >= 0 && (Integer.valueOf(Worktime_Begin) >= 5 && Integer.valueOf(Worktime_Begin) < 12)) {
                                    user_workList.add(new Work(array.getJSONObject(i).getString("FNO")
                                            , array.getJSONObject(i).getString("Title")
                                            , array.getJSONObject(i).getString("Salary")
                                            , array.getJSONObject(i).getString("WorkTime")
                                            , "0"));
                                }
                                if(myworktime.indexOf("2") >= 0 && (Integer.valueOf(Worktime_Begin) >= 12 && Integer.valueOf(Worktime_Begin) < 18)) {
                                    user_workList.add(new Work(array.getJSONObject(i).getString("FNO")
                                            , array.getJSONObject(i).getString("Title")
                                            , array.getJSONObject(i).getString("Salary")
                                            , array.getJSONObject(i).getString("WorkTime")
                                            , "0"));
                                }
                                if(myworktime.indexOf("3") >= 0 && (Integer.valueOf(Worktime_Begin) >= 18 && Integer.valueOf(Worktime_Begin) < 5)) {
                                    user_workList.add(new Work(array.getJSONObject(i).getString("FNO")
                                            , array.getJSONObject(i).getString("Title")
                                            , array.getJSONObject(i).getString("Salary")
                                            , array.getJSONObject(i).getString("WorkTime")
                                            , "0"));
                                }
                            }
                            main_work_listview.setAdapter(new WorkAdapter(MainWorkActivity.this, user_workList));
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
        requestQueue.add(obreq_UserInfo_WorkCar);
    }
}
