package com.example.admin.graduationproject;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

public class CreateWorkFormActivity extends AppCompatActivity {

    private Button button_create_work_form_cancel;
    private Button button_create_work_form_confirm;

    private RadioGroup Radiogroup_create_work_form_paytype;

    private String create_work_form_feid = "";
    private String create_work_form_work_name = "";
    private String create_work_form_salary = "";
    private String create_work_form_number = "";
    private String create_work_form_description = "";
    private String create_work_form_paytype = "";
    private String create_work_form_place = "";
    private String create_work_form_worktime = "";

    private String PostURL_work = "http://www.fcupapper.16mb.com/papper/post/work.php";
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_work_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        OnClickButtonListener_Create_Work_Form_Confirm();
        OnClickButtonListener_Create_Work_Form_Cancel();
        RadioListener_Create_Work_Form_Paytype();
    }

    private void RadioListener_Create_Work_Form_Paytype(){
        Radiogroup_create_work_form_paytype = (RadioGroup) findViewById(R.id.create_work_form_radiogroup);

        Radiogroup_create_work_form_paytype.setOnCheckedChangeListener(RadiogroupListener);
    }

    private RadioGroup.OnCheckedChangeListener RadiogroupListener =
            new RadioGroup.OnCheckedChangeListener() {

                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId)
                    {
                        case R.id.create_work_form_paytype1:
                            create_work_form_paytype = "時薪";
                            break;
                        case R.id.create_work_form_paytype2:
                            create_work_form_paytype = "日薪";
                            break;
                        case R.id.create_work_form_paytype3:
                            create_work_form_paytype = "一次付";
                            break;
                    }
                }

            };

    private void OnClickButtonListener_Create_Work_Form_Confirm() {
        button_create_work_form_confirm = (Button) findViewById(R.id.button_create_work_form_confirm);

        button_create_work_form_confirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetEditText();
                        if(!create_work_form_work_name.equals("") && !create_work_form_salary.equals("") && !create_work_form_number.equals("")) {
                            button_create_work_form_confirm.setEnabled(false);
                            CreateWorkForm();
                        }
                        else {
                            Toast.makeText(v.getContext(), "標題/數量/酬勞不可為空欄", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void OnClickButtonListener_Create_Work_Form_Cancel() {
        button_create_work_form_cancel = (Button) findViewById(R.id.button_create_work_form_cancel);

        button_create_work_form_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void GetEditText(){
        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        create_work_form_feid = userinfo.getString("UID","");

        EditText name = (EditText) findViewById(R.id.create_work_form_work_name);
        create_work_form_work_name = name.getText().toString().trim();

        EditText salary = (EditText) findViewById(R.id.create_work_form_salary);
        create_work_form_salary = salary.getText().toString().trim();

        EditText number = (EditText) findViewById(R.id.create_work_form_number);
        create_work_form_number = number.getText().toString().trim();

        EditText description = (EditText) findViewById(R.id.create_work_form_description);
        create_work_form_description = description.getText().toString().trim();

        EditText place = (EditText) findViewById(R.id.create_work_form_place);
        create_work_form_place = place.getText().toString().trim();

        EditText worktime_begin = (EditText) findViewById(R.id.create_work_form_work_time_begin);
        EditText worktime_end = (EditText) findViewById(R.id.create_work_form_work_time_end);
        create_work_form_worktime = worktime_begin.getText().toString().trim() + "-" + worktime_end.getText().toString().trim();
    }

    private void CreateWorkForm(){
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq = new StringRequest(Request.Method.POST,
                PostURL_work, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                Log.d("Tag",response.toString());
                Toast.makeText(CreateWorkFormActivity.this, "表單建立成功", Toast.LENGTH_LONG).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(CreateWorkFormActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
                button_create_work_form_confirm.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("feid", create_work_form_feid);
                params.put("title", create_work_form_work_name);
                params.put("salary", create_work_form_salary);
                params.put("number", create_work_form_number);
                params.put("content", create_work_form_description);
                params.put("paytype", create_work_form_paytype);
                params.put("place", create_work_form_place);
                params.put("worktime", create_work_form_worktime);

                return params;
            }
        };
        requestQueue.add(obreq);
    }
}