package com.example.admin.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;

public class AccountSettingActivity extends AppCompatActivity {

    private Button button_account_setting_cancel;
    private Button button_account_setting_modify;
    private Button button_account_setting_Signout;

    private CheckBox checkBox_account_setting_morning;  //1
    private CheckBox checkBox_account_setting_afternoon;//2
    private CheckBox checkBox_account_setting_night;    //3

    private String account_setting_uid = "";
    private String account_setting_name = "";
    private String account_setting_nickname = "";
    private String account_setting_school = "";
    private String account_setting_department = "";
    private String account_setting_cellphone = "";
    private String account_setting_password = "";
    private String account_setting_password_check = "";
    private String account_setting_worktime;
    private String morning = "";
    private String afternoon = "";
    private String night = "";

    private String PostURL_Updata = "http://www.fcupapper.16mb.com/papper/post/update/user.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        SetAccountInfo();
        OnCheckedChangeListener_Registered_Checkbox();
        OnKeyListener_Account_EditText();
        OnClickButtonListener_AccountSetting_Modify();
        OnClickButtonListener_AccountSetting_Cancel();
        OnClickButtonListener_AccountSetting_Signout();
    }

    private void OnClickButtonListener_AccountSetting_Cancel() {
        button_account_setting_cancel = (Button) findViewById(R.id.button_account_setting_cancel);

        button_account_setting_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_AccountSetting_Modify() {
        button_account_setting_modify = (Button) findViewById(R.id.button_account_setting_modify);

        button_account_setting_modify.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ResetAccountInfo();
                        if(!account_setting_name.equals("") && !account_setting_nickname.equals("")
                                && !account_setting_school.equals("")  && !account_setting_department.equals("")
                                && !account_setting_cellphone.equals("")  && !account_setting_password.equals("")
                                && !account_setting_password_check.equals("")) {
                            if(account_setting_password.equals(account_setting_password_check)) {
                                button_account_setting_modify.setEnabled(false);
                                AccountSetting_Modify();

                                SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
                                userinfo.edit()
                                        .putString("School",account_setting_school)
                                        .putString("Class",account_setting_department)
                                        .putString("Name",account_setting_name)
                                        .putString("Nick",account_setting_nickname)
                                        .putString("Telephone",account_setting_cellphone)
                                        .putString("Password",account_setting_password)
                                        .putString("EHour",account_setting_worktime)
                                        .commit();
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

    private void OnClickButtonListener_AccountSetting_Signout() {
        button_account_setting_Signout = (Button) findViewById(R.id.button_account_setting_Signout);

        button_account_setting_Signout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent LoginActivity = new Intent(AccountSettingActivity.this, LoginActivity.class);
                        LoginActivity.setFlags(LoginActivity.FLAG_ACTIVITY_CLEAR_TOP);//關掉所有到的界面中間的activity
                        startActivity(LoginActivity);
                        finish();
                    }
                }
        );
    }

    private void OnKeyListener_Account_EditText(){
        EditText Name_Listener = (EditText) findViewById(R.id.account_setting_name);
        EditText Nickname_Listener = (EditText) findViewById(R.id.account_setting_nickname);
        EditText School_Listener = (EditText) findViewById(R.id.account_setting_school);
        EditText Department_Listener = (EditText) findViewById(R.id.account_setting_department);
        EditText Cellphone_Listener = (EditText) findViewById(R.id.account_setting_cellphone);
        EditText Password_Listener = (EditText) findViewById(R.id.account_setting_password);
        EditText Password_Check_Listener = (EditText) findViewById(R.id.account_setting_password_check);

        Name_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_account_setting_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Nickname_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_account_setting_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        School_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_account_setting_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Department_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_account_setting_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Cellphone_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_account_setting_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Password_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_account_setting_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Password_Check_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_account_setting_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
    }

    private void OnCheckedChangeListener_Registered_Checkbox(){
        checkBox_account_setting_morning = (CheckBox) findViewById(R.id.account_setting_morning);
        checkBox_account_setting_afternoon = (CheckBox) findViewById(R.id.account_setting_afternoon);
        checkBox_account_setting_night = (CheckBox) findViewById(R.id.account_setting_night);

        if(account_setting_worktime.indexOf("1") >= 0){
            checkBox_account_setting_morning.setChecked(true);
        }

        if(account_setting_worktime.indexOf("2") >= 0){
            checkBox_account_setting_afternoon.setChecked(true);
        }

        if(account_setting_worktime.indexOf("3") >= 0){
            checkBox_account_setting_night.setChecked(true);
        }

        checkBox_account_setting_morning.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if(isChecked) {
                    morning = "1";
                    account_setting_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 已勾選,morning值為1
                else          {
                    morning = "9";
                    account_setting_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 未勾選,morning值為空
            }
        });

        checkBox_account_setting_afternoon.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {   //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if(isChecked) {
                    afternoon = "2";
                    account_setting_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 已勾選,afternoon值為2
                else          {
                    afternoon = "9";
                    account_setting_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 未勾選,afternoon值為空
            }
        });

        checkBox_account_setting_night.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked)
            {   //判斷CheckBox是否有勾選，同mCheckBox.isChecked()
                if(isChecked) {
                    night = "3";
                    account_setting_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 已勾選,night值為3
                else          {
                    night = "9";
                    account_setting_worktime = morning + afternoon + night;
                }//CheckBox狀態 : 未勾選，night值為空
            }
        });
    }

    private void SetAccountInfo(){
        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        account_setting_uid = userinfo.getString("UID","");
        account_setting_name = userinfo.getString("Name","");
        account_setting_nickname = userinfo.getString("Nick","");
        account_setting_school = userinfo.getString("School","");
        account_setting_department = userinfo.getString("Class","");
        account_setting_cellphone = userinfo.getString("Telephone","");
        account_setting_password = userinfo.getString("Password","");
        account_setting_worktime = userinfo.getString("EHour","");

        if(account_setting_worktime.indexOf("1") >= 0){
            morning = "1";
        }
        else morning = "9";

        if(account_setting_worktime.indexOf("2") >= 0){
            afternoon = "2";
        }
        else afternoon = "9";

        if(account_setting_worktime.indexOf("3") >= 0){
            night = "3";
        }
        else night = "9";

        EditText Name = (EditText) findViewById(R.id.account_setting_name);
        Name.setText(account_setting_name);

        EditText Nickname = (EditText) findViewById(R.id.account_setting_nickname);
        Nickname.setText(account_setting_nickname);

        EditText School = (EditText) findViewById(R.id.account_setting_school);
        School.setText(account_setting_school);

        EditText Department = (EditText) findViewById(R.id.account_setting_department);
        Department.setText(account_setting_department);

        EditText Cellphone = (EditText) findViewById(R.id.account_setting_cellphone);
        Cellphone.setText(account_setting_cellphone);

        EditText Password = (EditText) findViewById(R.id.account_setting_password);
        Password.setText(account_setting_password);
    }

    private void ResetAccountInfo(){
        EditText Name_Reset = (EditText) findViewById(R.id.account_setting_name);
        EditText Nickname_Reset = (EditText) findViewById(R.id.account_setting_nickname);
        EditText School_Reset = (EditText) findViewById(R.id.account_setting_school);
        EditText Department_Reset = (EditText) findViewById(R.id.account_setting_department);
        EditText Cellphone_Reset = (EditText) findViewById(R.id.account_setting_cellphone);
        EditText Password_Reset = (EditText) findViewById(R.id.account_setting_password);
        EditText Password_Check_Reset = (EditText) findViewById(R.id.account_setting_password_check);

        account_setting_name = Name_Reset.getText().toString().trim();
        account_setting_nickname = Nickname_Reset.getText().toString().trim();
        account_setting_school = School_Reset.getText().toString().trim();
        account_setting_department = Department_Reset.getText().toString().trim();
        account_setting_cellphone = Cellphone_Reset.getText().toString().trim();
        account_setting_password = Password_Reset.getText().toString().trim();
        account_setting_password_check = Password_Check_Reset.getText().toString().trim();
    }

    private void AccountSetting_Modify() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_AccountModify = new StringRequest(Request.Method.POST,
                PostURL_Updata, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(AccountSettingActivity.this, "資料修改成功", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(AccountSettingActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("uid",account_setting_uid);
                params.put("school", account_setting_school);
                params.put("class", account_setting_department);
                params.put("name", account_setting_name);
                params.put("nick", account_setting_nickname);
                params.put("password", account_setting_password);
                params.put("tel", account_setting_cellphone);
                params.put("ehour", account_setting_worktime);

                return params;
            }
        };
        requestQueue.add(obreq_AccountModify);
    }
}
