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

public class WorkFormActivity extends AppCompatActivity {

    private Button button_work_form_i_want_work;
    private Button button_work_form_cancel;
    private Button button_work_form_boss_information;
    private Button button_work_form_leave_message;
    private Button button_work_form_addtoworkcar;

    private ListView work_form_listview;

    private String JsonURL_work = "http://www.fcupapper.16mb.com/papper/get/single_search/form_work.php?fno=";
    private String JsonURL_work_singlesearch = "http://www.fcupapper.16mb.com/papper/get/single_search/form_work.php?fno=";
    private String JsonURL_WorkFormDo = "http://www.fcupapper.16mb.com/papper/post/focus.php";
    private String JsonURL_leave_message = "http://www.fcupapper.16mb.com/papper/post/discuss.php";
    private String JsonURL_message_board = "http://www.fcupapper.16mb.com/papper/get/single_search/discuss.php?fno=";
    private String JsonURL_message_board_listview = "";
    private String JsonURL_WorkCarCheck = "http://www.fcupapper.16mb.com/papper/get/focus.php";
    private String JsonURL_message_delete = "http://www.fcupapper.16mb.com/papper/post/delete/discuss.php";

    private List<Message> message_board_array = new ArrayList<>();

    RequestQueue requestQueue;

    private String Uid = "";
    private String WNO = "";
    private String FEID = "";
    private String Title = "";
    private String Salary = "";
    private String Worktime = "";
    private String Number = "";
    private String Paytype = "";
    private String Content = "";
    private String Place = "";
    private String WorkForm_createtime = "";
    private String WorkForm_message = "";
    private String Dno_check[] = new String[20];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Bundle bundle = getIntent().getExtras();
        WNO = String.valueOf(bundle.getLong("WNO"));

        work_form_listview = (ListView)findViewById(R.id.work_form_listview);
        SetOnItemLongClickListener_Delete_Message();
        //初始化Button
        InitialButton();
        //WorkCar & I do Check
        WorkFormWorkCarCheck();
        //載入表單資料
        WorkFormInfo();
        //載入留言板資料
        WorkFormMessageBoard();
        //Button Listener
        OnClickButtonListener_Work_Form_IWantWrok();
        OnClickButtonListener_Work_Form_Cancel();
        OnClickButtonListener_BossInfo();
        OnClickButtonListener_Work_Form_Leave_Message();
        OnClickButtonListener_Work_Form_AddToWorkCar();
        //Edittext Listener
        OnKeyListener_WorkForm_EditText();
    }

    private void InitialButton(){
        button_work_form_i_want_work = (Button) findViewById(R.id.button_work_form_i_want_work);
        button_work_form_addtoworkcar = (Button) findViewById(R.id.button_work_form_addtoworkcar);
        button_work_form_cancel = (Button) findViewById(R.id.button_work_form_cancel);
        button_work_form_boss_information = (Button) findViewById(R.id.button_work_form_boss_information);
        button_work_form_leave_message = (Button) findViewById(R.id.button_work_form_leave_message);
    }

    private void OnClickButtonListener_Work_Form_IWantWrok() {
        button_work_form_i_want_work.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog_IWanyBuy_Check();
                    }
                }
        );
    }

    private void Dialog_IWanyBuy_Check(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(WorkFormActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_check, null);
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("送出打工請求確認");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                button_work_form_i_want_work.setEnabled(false);
                button_work_form_addtoworkcar.setEnabled(false);
                WorkFormDo();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void OnClickButtonListener_Work_Form_AddToWorkCar() {

        button_work_form_addtoworkcar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button_work_form_addtoworkcar.setEnabled(false);
                        WorkFormAddShoppingBag();
                    }
                }
        );
    }

    private void OnClickButtonListener_Work_Form_Cancel() {
        button_work_form_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_BossInfo() {
        button_work_form_boss_information.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent SellerInfoActivity = new Intent(WorkFormActivity.this, SellerInfoActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("FEID", FEID);//傳遞ID
                        SellerInfoActivity.putExtras(bundle);

                        startActivity(SellerInfoActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_Work_Form_Leave_Message() {
        button_work_form_leave_message.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText message = (EditText) findViewById(R.id.work_form_leave_message);
                        WorkForm_message = message.getText().toString().trim();
                        button_work_form_leave_message.setEnabled(false);
                        WorkFormLeaveMessge();

                        EditText message_clear = (EditText) findViewById(R.id.work_form_leave_message);
                        message_clear.setText("");
                    }
                }
        );
    }

    private void SetOnItemLongClickListener_Delete_Message(){
        work_form_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                Dialog_Message_Delete(l);
                return false;
            }
        });
    }

    private void Dialog_Message_Delete(final long l){
        AlertDialog.Builder dialog = new AlertDialog.Builder(WorkFormActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_check, null);
        TextView Text = (TextView) dialogView.findViewById(R.id.dialog_form_check_text);
        Text.setText("是否要刪除該留言?");
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("留言刪除確認");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WorkFormDeleteMessage(String.valueOf(l));
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void OnKeyListener_WorkForm_EditText(){
        final EditText message_Listener = (EditText) findViewById(R.id.work_form_leave_message);

        message_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                if(message_Listener.getText().toString().trim().equals("")){
                    button_work_form_leave_message.setEnabled(false);
                }
                else button_work_form_leave_message.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
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

        public View getView(int position,View convertView,ViewGroup parent){
            if(convertView==null) convertView = layoutInflater.inflate(R.layout.listview_message_board, parent, false);//取得listItem容器 view

            Message message = messageList.get(position);

            TextView Text_Name = (TextView) convertView.findViewById(R.id.message_board_name);
            Text_Name.setText(message.getName());

            TextView Text_Message = (TextView) convertView.findViewById(R.id.message_board_message);
            Text_Message.setText(message.getMessage());

            TextView Text_MessageTime = (TextView) convertView.findViewById(R.id.message_board_messagetime);
            Text_MessageTime.setText(message.getMessage_time());

            return convertView;
        }
    }

    private void SetText(){
        TextView Text_Title = (TextView) findViewById(R.id.work_form_title);
        Text_Title.setText(Title);

        TextView Text_Price = (TextView) findViewById(R.id.work_form_salary);
        Text_Price.setText(Salary);

        TextView Text_Worktime = (TextView) findViewById(R.id.work_form_worktime);
        Text_Worktime.setText(Worktime);

        TextView Text_Number = (TextView) findViewById(R.id.work_form_number);
        Text_Number.setText(Paytype);

        TextView Text_Paytype = (TextView) findViewById(R.id.work_form_paytype);
        Text_Paytype.setText(Number);

        TextView Text_Description = (TextView) findViewById(R.id.work_form_description);
        Text_Description.setText(Content);

        TextView Text_Transaction = (TextView) findViewById(R.id.work_form_place);
        Text_Transaction.setText(Place);

        TextView Text_Form_createtime = (TextView) findViewById(R.id.work_form_createtime);
        Text_Form_createtime.setText(WorkForm_createtime);

        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        Uid = userinfo.getString("UID","");
    }

    private void WorkFormInfo(){

        requestQueue = Volley.newRequestQueue(this);
        JsonURL_work_singlesearch = JsonURL_work + WNO;
        JsonObjectRequest obreq_WorkFormInfo = new JsonObjectRequest(Request.Method.GET, JsonURL_work_singlesearch,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Work");

                            FEID = array.getJSONObject(0).getString("FEID");
                            Title = array.getJSONObject(0).getString("Title");
                            Salary = array.getJSONObject(0).getString("Salary");
                            Worktime = array.getJSONObject(0).getString("WorkTime");
                            Paytype = array.getJSONObject(0).getString("PayType");
                            Number = array.getJSONObject(0).getString("Number");
                            Content = array.getJSONObject(0).getString("Content");
                            Place = array.getJSONObject(0).getString("Place");
                            WorkForm_createtime = array.getJSONObject(0).getString("EHour");

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
        requestQueue.add(obreq_WorkFormInfo);
    }

    private void WorkFormMessageBoard(){
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
                            int j = 0;
                            for (int i = 0 ; i < array.length() ; i++){
                                message_board_array.add(new Message(array.getJSONObject(i).getString("DUID")
                                        ,array.getJSONObject(i).getString("Name")
                                        ,array.getJSONObject(i).getString("DText")
                                        ,array.getJSONObject(i).getString("DHour")
                                        ,array.getJSONObject(i).getString("DNO")));
                                if(array.getJSONObject(i).getString("DUID").equals(Uid)){
                                    Dno_check[j] = array.getJSONObject(i).getString("DNO");
                                    j++;
                                }
                            }
                            work_form_listview.setAdapter(new MessageBoard_Adapter(WorkFormActivity.this, message_board_array));
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

    private void WorkFormLeaveMessge() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_WorkFormLeaveMessge = new StringRequest(Request.Method.POST,
                JsonURL_leave_message, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    WorkFormMessageBoard();
                    Toast.makeText(WorkFormActivity.this, "留言成功", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                    WorkFormMessageBoard();
                    Toast.makeText(WorkFormActivity.this, "留言失敗請在試一次", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(WorkFormActivity.this, "與伺服器連線異常請在試一次", Toast.LENGTH_LONG).show();
                WorkFormMessageBoard();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("uid", Uid);
                params.put("fno", WNO);
                params.put("text", WorkForm_message);

                return params;
            }
        };
        requestQueue.add(obreq_WorkFormLeaveMessge);
    }

    private void WorkFormAddShoppingBag() {
        requestQueue = Volley.newRequestQueue(this);
        StringRequest obreq_SellFormAddShoppingBag = new StringRequest(Request.Method.POST,
                JsonURL_WorkFormDo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(WorkFormActivity.this, "已加入您的工作車", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(WorkFormActivity.this, "與伺服器連線異常請在試一次", Toast.LENGTH_LONG).show();
                button_work_form_addtoworkcar.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fuid", Uid);
                params.put("ffno", WNO);
                params.put("fstatus", "1");

                return params;
            }
        };
        requestQueue.add(obreq_SellFormAddShoppingBag);
    }

    private void WorkFormDo() {
        requestQueue = Volley.newRequestQueue(this);
        Log.d("JsonURL_WorkFormDo",JsonURL_WorkFormDo);
        StringRequest obreq_WorkFormDo = new StringRequest(Request.Method.POST,
                JsonURL_WorkFormDo, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(WorkFormActivity.this, "已送出表單等待資方確認", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(WorkFormActivity.this ,"與伺服器連線異常請在試一次", Toast.LENGTH_SHORT).show();
                button_work_form_i_want_work.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fuid", Uid);
                params.put("ffno", WNO);
                params.put("fstatus", "2");

                return params;
            }
        };
        requestQueue.add(obreq_WorkFormDo);
    }

    private void WorkFormWorkCarCheck(){
        requestQueue = Volley.newRequestQueue(this);
        Log.d("JsonURL_message_board",JsonURL_WorkCarCheck);
        JsonObjectRequest obreq_SellFormShoppingbagCheck = new JsonObjectRequest(Request.Method.GET, JsonURL_WorkCarCheck,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Focus");
                            for (int i = 0 ; i < array.length() ; i++){
                                if(array.getJSONObject(i).getString("FFNO").equals(WNO)
                                        && array.getJSONObject(i).getString("FUID").equals(Uid)
                                        && array.getJSONObject(i).getString("FStatus").equals("1"))
                                    button_work_form_addtoworkcar.setEnabled(false);
                                if(array.getJSONObject(i).getString("FFNO").equals(WNO)
                                        && array.getJSONObject(i).getString("FUID").equals(Uid)
                                        && (array.getJSONObject(i).getString("FStatus").equals("2")||array.getJSONObject(i).getString("FStatus").equals("3"))) {
                                    button_work_form_addtoworkcar.setEnabled(false);
                                    button_work_form_i_want_work.setEnabled(false);
                                }
                            }
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
        requestQueue.add(obreq_SellFormShoppingbagCheck);
    }

    private void WorkFormDeleteMessage(final String Dno) {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MySellFormDeleteMessage = new StringRequest(Request.Method.POST,
                JsonURL_message_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(WorkFormActivity.this, "留言刪除成功", Toast.LENGTH_LONG).show();
                    WorkFormMessageBoard();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(WorkFormActivity.this, "留言刪除失敗請重試", Toast.LENGTH_LONG).show();
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
