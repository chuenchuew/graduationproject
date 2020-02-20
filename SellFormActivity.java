package com.example.admin.graduationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class SellFormActivity extends AppCompatActivity {

    private Button button_sell_form_i_want_buy;
    private Button button_sell_form_cancel;
    private Button button_sell_form_seller_information;
    private Button button_sell_form_add_to_shoppingbag;
    private Button button_sell_form_leave_message;

    private ListView SellForm_messageboard;

    private String JsonURL_form = "http://www.fcupapper.16mb.com/papper/get/single_search/form_sell.php?fno=";
    private String JsonURL_form_single = "";
    private String JsonURL_AddShoppingBag = "http://www.fcupapper.16mb.com/papper/post/focus.php";
    private String JsonURL_leave_message = "http://www.fcupapper.16mb.com/papper/post/discuss.php";
    private String JsonURL_message_board = "http://www.fcupapper.16mb.com/papper/get/single_search/discuss.php?fno=";
    private String JsonURL_message_board_listview = "";
    private String JsonURL_shoppingbag_check = "http://www.fcupapper.16mb.com/papper/get/focus.php";
    private String JsonURL_message_delete = "http://www.fcupapper.16mb.com/papper/post/delete/discuss.php";

    RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private String Uid = "";
    private String Fno = "";
    private String FEID = "";
    private String Title = "";
    private String Price = "";
    private String Number = "";
    private String Description = "";
    private String Transaction = "";
    private String Form_createtime = "";
    private String SellForm_message = "";
    private Boolean leave_message_check = null;
    private String ImageURL;
    private String Dno_check[] = new String[20];

    List<Message> message_board_array = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        Bundle bundle = getIntent().getExtras();
        Fno = String.valueOf(bundle.getLong("FNO"));
        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();
        SellForm_messageboard = (ListView) findViewById(R.id.sell_form_listview);
        SetOnItemLongClickListener_Delete_Message();
        InitialButton();
        //載入表單資料與UID
        SellFormInfo();
        //載入留言板
        SellFormMessageBoard();
        //shoppingbag & buy checking
        SellFormShoppingbagCheck();
        //設定listview
        //Button Listener
        OnClickButtonListener_Sell_Form_I_Want_Buy();
        OnClickButtonListener_Sell_Form_Cancel();
        OnClickButtonListener_SellerInfo();
        OnClickButtonListener_AddToShoppingbag();
        OnClickButtonListener_Sell_Form_Leave_Message();
        //Edittext Listener
        OnKeyListener_MySellForm_EditText();
    }

    private void InitialButton(){
        button_sell_form_i_want_buy = (Button) findViewById(R.id.button_sell_form_i_want_buy);
        button_sell_form_add_to_shoppingbag = (Button) findViewById(R.id.button_sell_form_add_to_shoppingbag);
        button_sell_form_cancel = (Button) findViewById(R.id.button_sell_form_cancel);
        button_sell_form_seller_information = (Button) findViewById(R.id.button_sell_form_seller_information);
        button_sell_form_leave_message = (Button) findViewById(R.id.button_sell_form_leave_message);
    }

    private void OnClickButtonListener_Sell_Form_I_Want_Buy() {
        button_sell_form_i_want_buy.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog_IWanyBuy_Check();
                    }
                }
        );
    }

    private void Dialog_IWanyBuy_Check(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(SellFormActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_check, null);
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("購買商品確認");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                button_sell_form_i_want_buy.setEnabled(false);
                button_sell_form_add_to_shoppingbag.setEnabled(false);
                SellFormBuy();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void OnClickButtonListener_Sell_Form_Cancel() {
        button_sell_form_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_SellerInfo() {
        button_sell_form_seller_information.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent SellerInfoActivity = new Intent(SellFormActivity.this, SellerInfoActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("FEID", FEID);//傳遞ID
                        SellerInfoActivity.putExtras(bundle);

                        startActivity(SellerInfoActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_Sell_Form_Leave_Message() {
        button_sell_form_leave_message.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button_sell_form_leave_message.setEnabled(false);
                        EditText message = (EditText) findViewById(R.id.sell_form_leave_message);
                        SellForm_message = message.getText().toString();
                        SellFormLeaveMessge();
                        message.setText("");

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (leave_message_check == null) ;
                                runOnUiThread(new Runnable() {
                                    public void run() { SellFormMessageBoard();
                                        leave_message_check = null; }
                                });
                            }
                        }).start();
                    }
                }
        );
    }

    private void OnClickButtonListener_AddToShoppingbag() {
        button_sell_form_add_to_shoppingbag.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        button_sell_form_add_to_shoppingbag.setEnabled(false);
                        SellFormAddShoppingBag();
                    }
                }
        );
    }

    private void SetOnItemLongClickListener_Delete_Message(){
        SellForm_messageboard.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                Dialog_Message_Delete(String.valueOf(l));
                return false;
            }
        });
    }

    private void Dialog_Message_Delete(final String l){
        AlertDialog.Builder dialog = new AlertDialog.Builder(SellFormActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_check, null);
        TextView Text = (TextView) dialogView.findViewById(R.id.dialog_form_check_text);
        Text.setText("是否要刪除該留言?");
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("留言刪除確認");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SellFormDeleteMessage(l);
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void SetText(){
        TextView Text_Title = (TextView) findViewById(R.id.sell_form_item_name);
        Text_Title.setText(Title);

        TextView Text_Price = (TextView) findViewById(R.id.sell_form_item_price);
        Text_Price.setText(Price);

        TextView Text_Number = (TextView) findViewById(R.id.sell_form_item_number);
        Text_Number.setText(Number);

        TextView Text_Description = (TextView) findViewById(R.id.sell_form_item_description);
        Text_Description.setText(Description);

        TextView Text_Transaction = (TextView) findViewById(R.id.sell_form_item_transaction);
        Text_Transaction.setText(Transaction);

        TextView Text_Form_createtime = (TextView) findViewById(R.id.sell_form_createtime);
        Text_Form_createtime.setText(Form_createtime);

        NetworkImageView image = (NetworkImageView) findViewById(R.id.sell_form_image);
        image.setImageUrl(ImageURL, imageLoader);
        image.setErrorImageResId(R.mipmap.defaultimage);

        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        Uid = userinfo.getString("UID","");
    }

    private void OnKeyListener_MySellForm_EditText(){
        final EditText message_Listener = (EditText) findViewById(R.id.sell_form_leave_message);

        message_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                if(message_Listener.getText().toString().trim().equals("")) {
                    button_sell_form_leave_message.setEnabled(false);
                }
                else button_sell_form_leave_message.setEnabled(true);
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

    private void SellFormInfo(){
        requestQueue = Volley.newRequestQueue(this);
        JsonURL_form_single = JsonURL_form + Fno;
        Log.d("JsonURL_form_single",JsonURL_form_single);
        JsonObjectRequest obreq_SellFormInfo = new JsonObjectRequest(Request.Method.GET, JsonURL_form_single,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Sell");

                            FEID = array.getJSONObject(0).getString("FEID");
                            Title = array.getJSONObject(0).getString("Title");
                            Price = array.getJSONObject(0).getString("SPrice");
                            Number = array.getJSONObject(0).getString("Number");
                            Description = array.getJSONObject(0).getString("Content");
                            Transaction = array.getJSONObject(0).getString("Transaction");
                            Form_createtime = array.getJSONObject(0).getString("EHour");
                            ImageURL = array.getJSONObject(0).getString("SURL");

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
        requestQueue.add(obreq_SellFormInfo);
    }

    private void SellFormMessageBoard(){
        requestQueue = Volley.newRequestQueue(this);
        JsonURL_message_board_listview = JsonURL_message_board + Fno;
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

                            SellForm_messageboard.setAdapter(new MessageBoard_Adapter(SellFormActivity.this, message_board_array));
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

    private void SellFormLeaveMessge() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_SellFormLeaveMessge = new StringRequest(Request.Method.POST,
                JsonURL_leave_message, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    leave_message_check = true;
                    SellFormMessageBoard();
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
                Toast.makeText(SellFormActivity.this, "網路不穩留言失敗請在試一次", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("uid", Uid);
                params.put("fno", Fno);
                params.put("text", SellForm_message);

                return params;
            }
        };
        requestQueue.add(obreq_SellFormLeaveMessge);
    }

    private void SellFormAddShoppingBag() {
        requestQueue = Volley.newRequestQueue(this);
        StringRequest obreq_SellFormAddShoppingBag = new StringRequest(Request.Method.POST,
                JsonURL_AddShoppingBag, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(SellFormActivity.this, "已加入您的購物車", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(SellFormActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
                button_sell_form_add_to_shoppingbag.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fuid", Uid);
                params.put("ffno", Fno);
                params.put("fstatus", "1");

                return params;
            }
        };
        requestQueue.add(obreq_SellFormAddShoppingBag);
    }

    private void SellFormBuy() {
        requestQueue = Volley.newRequestQueue(this);
        StringRequest obreq_SellFormBuy = new StringRequest(Request.Method.POST,
                JsonURL_AddShoppingBag, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(SellFormActivity.this, "已送出訂單請等待賣家確認", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(SellFormActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
                button_sell_form_i_want_buy.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fuid", Uid);
                params.put("ffno", Fno);
                params.put("fstatus", "2");

                return params;
            }
        };
        requestQueue.add(obreq_SellFormBuy);
    }


    private void SellFormShoppingbagCheck(){
        requestQueue = Volley.newRequestQueue(this);
        Log.d("JsonURL_message_board",JsonURL_shoppingbag_check);
        JsonObjectRequest obreq_SellFormShoppingbagCheck = new JsonObjectRequest(Request.Method.GET, JsonURL_shoppingbag_check,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Focus");
                            for (int i = 0 ; i < array.length() ; i++){
                                if(array.getJSONObject(i).getString("FFNO").equals(Fno)
                                        && array.getJSONObject(i).getString("FUID").equals(Uid)
                                        && array.getJSONObject(i).getString("FStatus").equals("1"))
                                    button_sell_form_add_to_shoppingbag.setEnabled(false);
                                if(array.getJSONObject(i).getString("FFNO").equals(Fno)
                                        && array.getJSONObject(i).getString("FUID").equals(Uid)
                                        && (array.getJSONObject(i).getString("FStatus").equals("2") || array.getJSONObject(i).getString("FStatus").equals("3"))) {
                                    button_sell_form_add_to_shoppingbag.setEnabled(false);
                                    button_sell_form_i_want_buy.setEnabled(false);
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

    private void SellFormDeleteMessage(final String Dno) {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MySellFormDeleteMessage = new StringRequest(Request.Method.POST,
                JsonURL_message_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(SellFormActivity.this, "留言刪除成功", Toast.LENGTH_LONG).show();
                    SellFormMessageBoard();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(SellFormActivity.this, "留言刪除失敗請重試", Toast.LENGTH_LONG).show();
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
