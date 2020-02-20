package com.example.admin.graduationproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.concurrent.RunnableFuture;

public class LoginActivity extends AppCompatActivity {

    private Button button_login_login;
    private Button button_login_registered;

    private String UID;
    private String School;
    private String Class;
    private String Name;
    private String Nick;
    private String Telephone;
    private String EHour;
    private String Password;

    private Boolean check = null;

    private String JsonURL_member_check_base = "http://fcupapper.16mb.com/papper/get/single_search/login.php?account=";
    private String JsonURL_member_check = "";


    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        OnClickButtonListener_Login();
        OnClickButtonListener_Registered();

    }

    private void OnClickButtonListener_Login() {
        button_login_login = (Button) findViewById(R.id.login_login);

        button_login_login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MemberCheck();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (check == null) ;

                                runOnUiThread(new Runnable() {
                                    public void run()
                                    {
                                        if(check) {
                                            Intent MainActivity = new Intent(LoginActivity.this, MainActivity.class);
                                            Toast.makeText(LoginActivity.this ,"帳號/密碼正確登入中", Toast.LENGTH_SHORT).show();
                                            startActivity(MainActivity);
                                            finish();
                                        }
                                        else {
                                            Toast.makeText(LoginActivity.this ,"帳號/密碼錯誤請重試", Toast.LENGTH_SHORT).show();
                                            check = null;
                                        }
                                    }
                                });
                            }
                        }).start();

                    }
                }
        );
    }

    private void OnClickButtonListener_Registered() {
        button_login_registered = (Button) findViewById(R.id.login_registered);

        button_login_registered.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent RegisteredActivity = new Intent(LoginActivity.this, RegisteredActivity.class);
                        startActivity(RegisteredActivity);
                    }
                }
        );
    }

    private void MemberCheck(){

        EditText editText_account = (EditText) findViewById(R.id.login_account);
        EditText editText_password = (EditText) findViewById(R.id.login_password);

        if(editText_account.getText().toString().equals("") || editText_password.getText().toString().equals("")){
            Toast.makeText(LoginActivity.this ,"請輸入帳號密碼", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonURL_member_check = JsonURL_member_check_base + editText_account.getText() + "&password=" + editText_password.getText();
        Log.d("JsonURL_member_check",JsonURL_member_check);

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, JsonURL_member_check,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Integer object = response.getInt("success");

                            if(object == 1){
                                check = true;
                                JSONArray array = response.getJSONArray("memberinfo");

                                UID = array.getJSONObject(0).getString("UID");
                                School = array.getJSONObject(0).getString("School");
                                Class = array.getJSONObject(0).getString("Class");
                                Name = array.getJSONObject(0).getString("Name");
                                Nick = array.getJSONObject(0).getString("Nick");
                                Telephone = array.getJSONObject(0).getString("Tel");
                                Password = array.getJSONObject(0).getString("Password");
                                EHour = array.getJSONObject(0).getString("EHour");

                                SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
                                userinfo.edit()
                                        .putString("UID",UID)
                                        .putString("School",School)
                                        .putString("Class",Class)
                                        .putString("Name",Name)
                                        .putString("Nick",Nick)
                                        .putString("Telephone",Telephone)
                                        .putString("Password",Password)
                                        .putString("EHour",EHour)
                                        .commit();
                            }
                            else check = false;
                            Log.d("check",check.toString());
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
                        Toast.makeText(LoginActivity.this ,"與伺服器連線異常請稍後在試", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        requestQueue.add(obreq);
    }
}
