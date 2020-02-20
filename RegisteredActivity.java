package com.example.admin.graduationproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisteredActivity extends AppCompatActivity {

    private Button button_registered_cancel;
    private Button button_registered_registered;
    private Button button_registered_get_verification;

    private CheckBox checkBox_registered_morning;  //1
    private CheckBox checkBox_registered_afternoon;//2
    private CheckBox checkBox_registered_night;    //3

    private String registered_name = "";
    private String registered_nickname = "";
    private String registered_id = "";
    private String registered_school = "";
    private String registered_department = "";
    private String registered_cellphone = "";
    private String registered_password = "";
    private String registered_password_check = "";
    private String registered_worktime = "999";
    private String registered_verification = "";
    private String morning = "9";
    private String afternoon = "9";
    private String night = "9";
    private Boolean check = false;
    private int random;

    private String PostURL_user = "http://www.fcupapper.16mb.com/papper/post/user.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        OnClickButtonListener_Registered_Registered();
        OnClickButtonListener_Registered_Cancel();
        OnCheckedChangeListener_Registered_Checkbox();
        OnKeyListener_Registered_EditText_Id();
        OnClickButtonListener_Registered_GetVerification();
    }

    private void OnKeyListener_Registered_EditText_Id(){
        EditText id_listener = (EditText) findViewById(R.id.registered_id);
        EditText name_listener = (EditText) findViewById(R.id.registered_name);
        EditText nickname_listener = (EditText) findViewById(R.id.registered_nickname);
        EditText school_listener = (EditText) findViewById(R.id.registered_school);
        EditText department_listener = (EditText) findViewById(R.id.registered_department);
        EditText cellphone_listener = (EditText) findViewById(R.id.registered_cellphone);
        EditText password_listener = (EditText) findViewById(R.id.registered_password);
        EditText verification_listener = (EditText) findViewById(R.id.registered_verification);

        id_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        name_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        nickname_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        school_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        department_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        cellphone_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        password_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
        verification_listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_registered_registered.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
    }

    private void OnClickButtonListener_Registered_GetVerification(){
        button_registered_get_verification = (Button) findViewById(R.id.button_registered_get_verification);

        button_registered_get_verification.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Getverification();
                    }
                }
        );
    }

    private void OnClickButtonListener_Registered_Registered() {
        button_registered_registered = (Button) findViewById(R.id.button_registered_registered);

        button_registered_registered.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetEditText();
                        if(!registered_name.equals("") && !registered_nickname.equals("")
                                && !registered_id.equals("")  && !registered_school.equals("")
                                && !registered_department.equals("")  && !registered_cellphone.equals("")
                                && !registered_password.equals("")) {
                            if(registered_password.equals(registered_password_check)) {
                                if(registered_verification.equals(String.valueOf(random))) {
                                    button_registered_registered.setEnabled(false);
                                    RegisterAccount();
                                }
                                else {
                                    Toast.makeText(v.getContext(), "安全驗證碼錯誤請重新輸入", Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Toast.makeText(v.getContext(), "二次密碼不同請重新輸入", Toast.LENGTH_LONG).show();
                            }
                        }
                        else{
                            Toast.makeText(v.getContext(), "請勿空欄請重新輸入", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void OnClickButtonListener_Registered_Cancel() {
        button_registered_cancel = (Button) findViewById(R.id.button_registered_cancel);

        button_registered_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnCheckedChangeListener_Registered_Checkbox(){
        checkBox_registered_morning = (CheckBox) findViewById(R.id.registered_worktime_morning);
        checkBox_registered_afternoon = (CheckBox) findViewById(R.id.registered_worktime_afternoon);
        checkBox_registered_night = (CheckBox) findViewById(R.id.registered_worktime_night);

        checkBox_registered_morning.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {
                //判斷CheckBox是否有勾選，同mCheckBox.isChecked()

                if(isChecked) {
                    morning = "1";
                    registered_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 已勾選,morning值為1
                else          {
                    morning = "9";
                    registered_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 未勾選,morning值為空
            }
        });

        checkBox_registered_afternoon.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {   //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if(isChecked) {
                    afternoon = "2";
                    registered_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 已勾選,afternoon值為2
                else          {
                    afternoon = "9";
                    registered_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 未勾選,afternoon值為空
            }
        });

        checkBox_registered_night.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {   //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if(isChecked) {
                    night = "3";
                    registered_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 已勾選,night值為3
                else          {
                    night = "9";
                    registered_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 未勾選，night值為空
            }
        });
    }

    private void Getverification(){
        final int notifyID = 1; // 通知的識別號碼
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務

        Random ran = new Random();
        random = ran.nextInt(10000);
        while (random < 1000) random = ran.nextInt(10000);

        final Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_menu_send)
                .setContentTitle("安全驗證碼")
                .setContentText(String.valueOf(random))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .build(); // 建立通知
        notificationManager.notify(notifyID, notification); // 發送通知
    }

    private void GetEditText(){
        EditText name = (EditText) findViewById(R.id.registered_name);
        EditText nickname = (EditText) findViewById(R.id.registered_nickname);
        EditText id = (EditText) findViewById(R.id.registered_id);
        EditText school = (EditText) findViewById(R.id.registered_school);
        EditText department = (EditText) findViewById(R.id.registered_department);
        EditText cellphone = (EditText) findViewById(R.id.registered_cellphone);
        EditText password = (EditText) findViewById(R.id.registered_password);
        EditText password_check = (EditText) findViewById(R.id.registered_password_check);
        EditText verification = (EditText) findViewById(R.id.registered_verification);

        registered_name = name.getText().toString().trim();
        registered_nickname = nickname.getText().toString().trim();
        registered_id = id.getText().toString().trim();
        registered_school = school.getText().toString().trim();
        registered_department = department.getText().toString().trim();
        registered_cellphone = cellphone.getText().toString().trim();
        registered_password = password.getText().toString().trim();
        registered_password_check = password_check.getText().toString().trim();
        registered_verification = verification.getText().toString().trim();
    }

    private void RegisterAccount() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_RegisterAccount = new StringRequest(Request.Method.POST,
                PostURL_user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    JSONObject JsonObject = new JSONObject(response);

                    Log.d("Tag",JsonObject.getString("status"));
                    if(JsonObject.getString("status").equals("success")) {
                        Toast.makeText(RegisteredActivity.this, "註冊成功", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else {
                        Toast.makeText(RegisteredActivity.this, "已有此帳號請重新輸入", Toast.LENGTH_LONG).show();
                    }
                    Log.d("check2", check.toString());
                }catch (Exception e){
                        Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(RegisteredActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
                button_registered_registered.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("school",  registered_school);
                params.put("class",   registered_department);
                params.put("name",    registered_name);
                params.put("nick",    registered_nickname);
                params.put("account", registered_id);
                params.put("password",registered_password);
                params.put("tel",     registered_cellphone);
                params.put("ehour",   registered_worktime);

                Log.d("school",   registered_school);
                Log.d("class",    registered_department);
                Log.d("name",     registered_name);
                Log.d("nick",     registered_nickname);
                Log.d("account",  registered_id);
                Log.d("password", registered_password);
                Log.d("tel",      registered_cellphone);
                Log.d("worktime", registered_worktime);
                return params;
            }
        };
        requestQueue.add(obreq_RegisterAccount);
    }
}
