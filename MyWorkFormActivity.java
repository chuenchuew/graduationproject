package com.example.admin.graduationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyWorkFormActivity extends AppCompatActivity {

    private Button button_my_work_form_delete;
    private Button button_my_work_form_modify;
    private Button button_my_work_form_cancel;
    private Button button_my_work_form_leave_message;
    private Button button_my_work_form_worker;

    private RadioGroup Radiogroup_my_work_form_paytype;
    private RadioButton my_work_form_paytype1;
    private RadioButton my_work_form_paytype2;
    private RadioButton my_work_form_paytype3;

    private ListView my_work_form_listview;

    private String Uid = "";
    private String WNO = "";
    private String Title = "";
    private String Salary = "";
    private String Worktime = "";
    private String Worktime_Begin = "";
    private String Worktime_End = "";
    private String[] WorktimeArray;
    private String Number = "";
    private String Paytype = "";
    private String Content = "";
    private String Place = "";
    private String MyWorkForm_createtime = "";
    private String MyWorkForm_message = "";
    private Boolean leave_message_check = null;

    private String JsonURL_work = "http://www.fcupapper.16mb.com/papper/get/single_search/form_work.php?";
    private String JsonURL_work_singlesearch = "http://www.fcupapper.16mb.com/papper/get/single_search/form_work.php?fno=";
    private String JsonURL_work_delete = "http://www.fcupapper.16mb.com/papper/post/delete/work.php";
    private String JsonURL_work_modify = "http://www.fcupapper.16mb.com/papper/post/update/work.php";
    private String JsonURL_leave_message = "http://www.fcupapper.16mb.com/papper/post/discuss.php";
    private String JsonURL_message_board = "http://www.fcupapper.16mb.com/papper/get/single_search/discuss.php?fno=";
    private String JsonURL_message_board_listview = "";
    private String JsonURL_message_delete = "http://www.fcupapper.16mb.com/papper/post/delete/discuss.php";

    private List<Message> message_board_array = new ArrayList<>();

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_work_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Bundle bundle = getIntent().getExtras();
        WNO = String.valueOf(bundle.getLong("WNO"));

        my_work_form_listview = (ListView)findViewById(R.id.my_work_form_listview);
        SetOnItemLongClickListener_Delete_Message();
        //載入表單資料
        MyWorkFormInfo();
        //載入留言板資料
        MyWorkFormMessageBoard();
        //Button Listener
        OnClickButtonListener_Delete();
        OnClickButtonListener_Modify();
        OnClickButtonListener_Cancel();
        OnClickButtonListener_Work_Form_Leave_Message();
        OnClickButtonListener_Worker();
        //RadioGroup Listener
        RadioListener_Create_Work_Form_Paytype();
        //Edittext Listener
        OnKeyListener_MyWorkForm_EditText();
    }

    private void OnClickButtonListener_Worker() {
        button_my_work_form_worker = (Button) findViewById(R.id.button_my_work_form_worker);

        button_my_work_form_worker.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent BuyerWorkerActivity = new Intent(MyWorkFormActivity.this, BuyerWorkerActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("FNO",WNO);//傳遞ID
                        BuyerWorkerActivity.putExtras(bundle);

                        startActivity(BuyerWorkerActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_Delete() {
        button_my_work_form_delete = (Button) findViewById(R.id.button_my_work_form_delete);

        button_my_work_form_delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button_my_work_form_delete.setEnabled(false);
                        MyWorkFormDelete();
                    }
                }
        );
    }

    private void OnClickButtonListener_Modify() {
        button_my_work_form_modify = (Button) findViewById(R.id.button_my_work_form_modify);

        button_my_work_form_modify.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!Title.equals("") && !Salary.equals("") && !Number.equals("")  && !Worktime_Begin.equals("") && !Worktime_End.equals("")
                                && !Paytype.equals("") && !Place.equals("")) {
                            button_my_work_form_modify.setEnabled(false);
                            FormInfoReset();
                            MyWorkForm_Modify();
                        }
                        else{
                            Toast.makeText(v.getContext(), "請勿空欄請重新輸入", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void OnClickButtonListener_Cancel() {
        button_my_work_form_cancel = (Button) findViewById(R.id.button_my_work_form_cancel);

        button_my_work_form_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_Work_Form_Leave_Message() {
        button_my_work_form_leave_message = (Button) findViewById(R.id.button_my_work_form_leave_message);

        button_my_work_form_leave_message.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Leave_Message();
                        button_my_work_form_leave_message.setEnabled(false);
                        MyWorkFormLeaveMessge();

                        EditText message_clear = (EditText) findViewById(R.id.my_work_form_leave_message);
                        message_clear.setText("");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (leave_message_check == null) ;
                                runOnUiThread(new Runnable() {
                                    public void run() { MyWorkFormMessageBoard();
                                        leave_message_check = null; }
                                });
                            }
                        }).start();
                    }
                }
        );
    }

    private void SetOnItemLongClickListener_Delete_Message(){
        my_work_form_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                Dialog_Message_Delete(l);
                return false;
            }
        });
    }

    private void Dialog_Message_Delete(final long l){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MyWorkFormActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_check, null);
        TextView Text = (TextView) dialogView.findViewById(R.id.dialog_form_check_text);
        Text.setText("是否要刪除該留言?");
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("留言刪除確認");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyWorkFormDeleteMessage(String.valueOf(l));
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void Leave_Message(){
        EditText message = (EditText) findViewById(R.id.my_work_form_leave_message);
        MyWorkForm_message = message.getText().toString();

        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        Uid = userinfo.getString("UID","");
    }

    private void RadioListener_Create_Work_Form_Paytype(){
        Radiogroup_my_work_form_paytype = (RadioGroup) findViewById(R.id.my_work_form_radiogroup);

        Radiogroup_my_work_form_paytype.setOnCheckedChangeListener(RadiogroupListener);
    }

    private RadioGroup.OnCheckedChangeListener RadiogroupListener =
            new RadioGroup.OnCheckedChangeListener() {

                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId)
                    {
                        case R.id.my_work_form_paytype1:
                            Paytype = "時薪";
                            break;
                        case R.id.my_work_form_paytype2:
                            Paytype = "日薪";
                            break;
                        case R.id.my_work_form_paytype3:
                            Paytype = "一次付";
                            break;
                    }
                }

            };

    private void OnKeyListener_MyWorkForm_EditText(){
        EditText Title_Listener = (EditText) findViewById(R.id.my_work_form_title);
        EditText Salary_Listener = (EditText) findViewById(R.id.my_work_form_salary);
        EditText Number_Listener = (EditText) findViewById(R.id.my_work_form_number);
        EditText Description_Listener = (EditText) findViewById(R.id.my_work_form_description);
        EditText Place_Listener = (EditText) findViewById(R.id.my_work_form_place);
        final EditText message_Listener = (EditText) findViewById(R.id.my_work_form_leave_message);

        message_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {

                if(message_Listener.getText().toString().trim().equals("")){
                    button_my_work_form_leave_message.setEnabled(false);
                }
                else button_my_work_form_leave_message.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        Title_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_work_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Salary_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_work_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Number_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_work_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Description_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_work_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Place_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_work_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
    }

    private void SetText(){
        EditText EditText_Title = (EditText) findViewById(R.id.my_work_form_title);
        EditText_Title.setText(Title);

        EditText EditText_Salary = (EditText) findViewById(R.id.my_work_form_salary);
        EditText_Salary.setText(Salary);

        EditText EditText_Worktime_Begin = (EditText) findViewById(R.id.my_work_form_worktime_begin);
        EditText_Worktime_Begin.setText(Worktime_Begin);

        EditText EditText_Worktime_End = (EditText) findViewById(R.id.my_work_form_worktime_end);
        EditText_Worktime_End.setText(Worktime_End);

        EditText EditText_Number = (EditText) findViewById(R.id.my_work_form_number);
        EditText_Number.setText(Number);

        EditText EditText_Description = (EditText) findViewById(R.id.my_work_form_description);
        EditText_Description.setText(Content);

        EditText EditText_Place = (EditText) findViewById(R.id.my_work_form_place);
        EditText_Place.setText(Place);

        TextView Text_Form_createtime = (TextView) findViewById(R.id.my_work_form_createtime);
        Text_Form_createtime.setText(MyWorkForm_createtime);

        my_work_form_paytype1 = (RadioButton) findViewById(R.id.my_work_form_paytype1);
        my_work_form_paytype2 = (RadioButton) findViewById(R.id.my_work_form_paytype2);
        my_work_form_paytype3 = (RadioButton) findViewById(R.id.my_work_form_paytype3);

        if(Paytype.equals("時薪")){
            my_work_form_paytype1.setChecked(true);
        }
        if(Paytype.equals("日薪")){
            my_work_form_paytype2.setChecked(true);
        }
        if(Paytype.equals("一次付")){
            my_work_form_paytype3.setChecked(true);
        }
    }

    private void FormInfoReset(){
        EditText EditText_Title = (EditText) findViewById(R.id.my_work_form_title);
        EditText EditText_Salary = (EditText) findViewById(R.id.my_work_form_salary);
        EditText EditText_Worktime_Begin = (EditText) findViewById(R.id.my_work_form_worktime_begin);
        EditText EditText_Worktime_End = (EditText) findViewById(R.id.my_work_form_worktime_end);
        EditText EditText_Number = (EditText) findViewById(R.id.my_work_form_number);
        EditText EditText_Description = (EditText) findViewById(R.id.my_work_form_description);
        EditText EditText_Place = (EditText) findViewById(R.id.my_work_form_place);

        Title = EditText_Title.getText().toString().trim();
        Salary = EditText_Salary.getText().toString().trim();
        Worktime_Begin = EditText_Worktime_Begin.getText().toString().trim();
        Worktime_End = EditText_Worktime_End.getText().toString().trim();
        Worktime = Worktime_Begin + "-" + Worktime_End;
        Number = EditText_Number.getText().toString().trim();
        Content = EditText_Description.getText().toString().trim();
        Place = EditText_Place.getText().toString().trim();
    }

    private class MessageBoard_Adapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<Message> messageList;

        private MessageBoard_Adapter(Context context, List<Message> messageList){
            layoutInflater = LayoutInflater.from(context);
            this.messageList = messageList;
        }
        public int getCount(){
            return messageList.size();
        }

        public Object getItem(int position){
            return messageList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return messageList.get(position).getDno();
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView==null) convertView = layoutInflater.inflate(R.layout.listview_message_board, parent, false);//取得listItem容器 view

            Message message = messageList.get(position);

            TextView Text_Name = (TextView) convertView.findViewById(R.id.message_board_name);
            Text_Name.setText(message.getName());

            TextView Text_Message = (TextView) convertView.findViewById(R.id.message_board_message);
            Text_Message.setText(message.getMessage());

            return convertView;
        }
    }

    private void MyWorkFormInfo(){

        requestQueue = Volley.newRequestQueue(this);
        JsonURL_work_singlesearch = JsonURL_work + "fno=" + WNO;
        Log.d("work_singlesearch",JsonURL_work_singlesearch);
        JsonObjectRequest obreq_MyWorkFormInfo = new JsonObjectRequest(Request.Method.GET, JsonURL_work_singlesearch,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Work");

                            Title = array.getJSONObject(0).getString("Title");
                            Salary = array.getJSONObject(0).getString("Salary");
                            Worktime = array.getJSONObject(0).getString("WorkTime");
                            Paytype = array.getJSONObject(0).getString("PayType");
                            Number = array.getJSONObject(0).getString("Number");
                            Content = array.getJSONObject(0).getString("Content");
                            Place = array.getJSONObject(0).getString("Place");
                            MyWorkForm_createtime = array.getJSONObject(0).getString("EHour");

                            WorktimeArray = Worktime.split("-");
                            Worktime_Begin = WorktimeArray[0];
                            Worktime_End = WorktimeArray[1];
                            SetText();
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
        requestQueue.add(obreq_MyWorkFormInfo);
    }

    private void MyWorkFormDelete() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MyWorkFormDelete = new StringRequest(Request.Method.POST,
                JsonURL_work_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(MyWorkFormActivity.this, "表單刪除成功", Toast.LENGTH_LONG).show();
                    finish();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(MyWorkFormActivity.this, "表單刪除失敗請重試", Toast.LENGTH_LONG).show();
                button_my_work_form_delete.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fno", WNO);

                return params;
            }
        };
        requestQueue.add(obreq_MyWorkFormDelete);
    }

    private void MyWorkForm_Modify() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_SellForm_Modify = new StringRequest(Request.Method.POST,
                JsonURL_work_modify, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(MyWorkFormActivity.this, "資料修改成功", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(MyWorkFormActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
                button_my_work_form_modify.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("fno", WNO);
                params.put("title", Title);
                params.put("salary", Salary);
                params.put("worktime", Worktime);
                params.put("number", Number);
                params.put("paytype", Paytype);
                params.put("content", Content);
                params.put("place", Place);

                Log.d("title", Title);
                Log.d("salary", Salary);
                Log.d("number", Worktime);
                Log.d("description", Number);
                Log.d("paytype", Paytype);
                Log.d("place", Content);
                Log.d("worktime", Place);

                return params;
            }
        };
        requestQueue.add(obreq_SellForm_Modify);
    }

    private void MyWorkFormMessageBoard(){
        requestQueue = Volley.newRequestQueue(this);
        JsonURL_message_board_listview = JsonURL_message_board + WNO;
        Log.d("JsonURL_message_board",JsonURL_message_board_listview);
        JsonObjectRequest obreq_SellFormMessage = new JsonObjectRequest(Request.Method.GET, JsonURL_message_board_listview,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Discuss");
                            message_board_array.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                message_board_array.add(new Message(array.getJSONObject(i).getString("DUID")
                                        ,array.getJSONObject(i).getString("Name")
                                        ,array.getJSONObject(i).getString("DText")
                                        ,array.getJSONObject(i).getString("DHour")
                                        ,array.getJSONObject(i).getString("DNO")));
                            }
                            my_work_form_listview.setAdapter(new MessageBoard_Adapter(MyWorkFormActivity.this, message_board_array));
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
        requestQueue.add(obreq_SellFormMessage);
    }

    private void MyWorkFormLeaveMessge() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_WorkFormLeaveMessge = new StringRequest(Request.Method.POST,
                JsonURL_leave_message, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    leave_message_check = true;
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                    leave_message_check = false;
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
                params.put("uid", Uid);
                params.put("fno", WNO);
                params.put("text", MyWorkForm_message);

                return params;
            }
        };
        requestQueue.add(obreq_WorkFormLeaveMessge);
    }

    private void MyWorkFormDeleteMessage(final String Dno) {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MySellFormDeleteMessage = new StringRequest(Request.Method.POST,
                JsonURL_message_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(MyWorkFormActivity.this, "留言刪除成功", Toast.LENGTH_LONG).show();
                    MyWorkFormMessageBoard();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(MyWorkFormActivity.this, "留言刪除失敗請重試", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("dno", Dno);

                return params;
            }
        };
        requestQueue.add(obreq_MySellFormDeleteMessage);
    }
}
