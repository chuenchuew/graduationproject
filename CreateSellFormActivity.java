package com.example.admin.graduationproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class CreateSellFormActivity extends AppCompatActivity {

    private Button button_create_sell_form_cancel;
    private Button button_create_sell_form_confirm;
    private Button button_create_sell_form_select_photo;

    private Spinner spinner_create_sell_form_classification;
    private ArrayAdapter<String> classificationList;
    private String[] classification = {"居家生活", "3C產品", "運動休閒", "學生用品", "服飾鞋子配件","食物","雜物"};

    private ImageView imageView;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private int options = 100;
    private String image_URL = "no-image";
    private String URL_UpLoadImage = "http://www.fcupapper.16mb.com/papper/post/simple_upload.php";

    private String create_sell_form_fno = "";
    private String create_sell_form_feid = "";
    private String create_sell_form_name = "";
    private String create_sell_form_price = "";
    private String create_sell_form_number = "";
    private String create_sell_form_description = "";
    private String create_sell_form_transaction = "";
    private String create_sell_form_type = "";

    private String PostURL_sell = "http://www.fcupapper.16mb.com/papper/post/sell.php";
    RequestQueue requestQueue;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_sell_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        imageView = (ImageView)findViewById(R.id.create_sell_form_image);

        SetOnItemSelectedListener_spinner_create_sell_form_classification();

        OnClickButtonListener_Create_Sell_Form_Confirm();
        OnClickButtonListener_Create_Sell_Form_Cancel();
        OnClickButtonListener_Create_Sell_Form_SelectPhoto();
    }

    private void OnClickButtonListener_Create_Sell_Form_Confirm() {
        button_create_sell_form_confirm = (Button) findViewById(R.id.button_create_sell_form_confirm);

        button_create_sell_form_confirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GetEditText();
                        if(!create_sell_form_name.equals("") && !create_sell_form_price.equals("") && !create_sell_form_number.equals("")) {
                            button_create_sell_form_confirm.setEnabled(false);
                            CreateSellForm();
                        }
                        else {
                            Toast.makeText(v.getContext(), "標題/數量/價格不可為空欄", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void OnClickButtonListener_Create_Sell_Form_Cancel() {
        button_create_sell_form_cancel = (Button) findViewById(R.id.button_create_sell_form_cancel);

        button_create_sell_form_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void SetOnItemSelectedListener_spinner_create_sell_form_classification(){
        spinner_create_sell_form_classification = (Spinner)findViewById(R.id.spinner_create_sell_form_classification);
        classificationList = new ArrayAdapter<String>(CreateSellFormActivity.this, android.R.layout.simple_spinner_item, classification);
        spinner_create_sell_form_classification.setAdapter(classificationList);
        spinner_create_sell_form_classification.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView adapterView, View view, int position, long id){
                create_sell_form_type = adapterView.getSelectedItem().toString();
            }
            public void onNothingSelected(AdapterView arg0) {
            }
        });
    }

    private void GetEditText(){
        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        create_sell_form_feid = userinfo.getString("UID","");

        EditText name = (EditText) findViewById(R.id.create_sell_form_name);
        create_sell_form_name = name.getText().toString().trim();

        EditText price = (EditText) findViewById(R.id.create_sell_form_price);
        create_sell_form_price = price.getText().toString().trim();

        EditText number = (EditText) findViewById(R.id.create_sell_form_number);
        create_sell_form_number = number.getText().toString().trim();

        EditText description = (EditText) findViewById(R.id.create_sell_form_description);
        create_sell_form_description = description.getText().toString().trim();

        EditText transaction = (EditText) findViewById(R.id.create_sell_form_transaction);
        create_sell_form_transaction = transaction.getText().toString().trim();
    }

    private void CreateSellForm(){
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq = new StringRequest(Request.Method.POST,
                PostURL_sell, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    JSONObject JsonObject = new JSONObject(response);
                    create_sell_form_fno = JsonObject.getString("FNO");
                    Log.d("Tag", response);
                    Log.d("create_sell_form_fno", create_sell_form_fno);
                    if(!image_URL.equals("no-image")) {
                        uploadImage();
                    }
                    else if(JsonObject.getString("status").equals("success")) {
                        Toast.makeText(CreateSellFormActivity.this, "表單建立成功", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else Toast.makeText(CreateSellFormActivity.this, "表單建立失敗請重試", Toast.LENGTH_LONG).show();
                }catch (Exception e) {
                    Log.e("jsonException", e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(CreateSellFormActivity.this, "表單建立失敗請重試", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("feid", create_sell_form_feid);
                params.put("title", create_sell_form_name);
                params.put("price", create_sell_form_price);
                params.put("number", create_sell_form_number);
                params.put("content", create_sell_form_description);
                params.put("transaction", create_sell_form_transaction);
                params.put("type", create_sell_form_type);
                params.put("url", image_URL);

                Log.d("feid", create_sell_form_feid);
                Log.d("title", create_sell_form_name);
                Log.d("type", create_sell_form_type);
                Log.d("price", create_sell_form_price);
                Log.d("number", create_sell_form_number);
                Log.d("content", create_sell_form_description);
                Log.d("transction", create_sell_form_transaction);
                Log.d("url", image_URL);

                return params;
            }
        };
        requestQueue.add(obreq);
    }

    private void OnClickButtonListener_Create_Sell_Form_SelectPhoto() {
        button_create_sell_form_select_photo = (Button) findViewById(R.id.button_create_sell_form_select_photo);

        button_create_sell_form_select_photo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFileChooser();
                    }
                }
        );
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                imageView.setImageBitmap(bitmap);
                image_URL = "image";
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 100) {//循環判斷如果壓縮後圖片是否大於100kb,大於繼續壓縮
            baos.reset();//重置baos即讓下一次的寫入覆蓋之前的內容
            options -= 10;//圖片質量每次減少10
            if(options<0)options=0;//如果圖片質量小於10，則將圖片的質量壓縮到最小值
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);//將壓縮後的圖片保存到baos中
            if(options==0)break;//如果圖片的質量已降到最低則，不再進行壓縮
        }
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        requestQueue = Volley.newRequestQueue(this);
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"上傳中...","請稍後...",false,false);
        StringRequest uploadImageRequest = new StringRequest(Request.Method.POST, URL_UpLoadImage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Log.d("Tag",s);
                        Toast.makeText(CreateSellFormActivity.this ,"表單建立成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        //Showing toast
                        Log.d("Tag",volleyError.getMessage().toString());
                        Toast.makeText(CreateSellFormActivity.this ,"圖片上傳失敗請重試", Toast.LENGTH_SHORT).show();
                        button_create_sell_form_confirm.setEnabled(true);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);
                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put("image", image);
                params.put("fno", create_sell_form_fno);
                //returning parameters
                return params;
            }
        };
        //Adding request to the queue
        requestQueue.add(uploadImageRequest);
    }
}