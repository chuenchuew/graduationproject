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

public class MySellFormActivity extends AppCompatActivity {

    private Button button_my_sell_form_buyer;
    private Button button_my_sell_form_delete;
    private Button button_my_sell_form_modify;
    private Button button_my_sell_form_cancel;
    private Button button_my_sell_form_leave_message;

    private ImageView imageView;

    private ListView my_sell_form_listview;

    private String Uid = "";
    private String Fno = "";
    private String Title = "";
    private String Price = "";
    private String Number = "";
    private String Description = "";
    private String Transaction = "";
    private String Form_createtime = "";
    private String my_sell_form_message = "";
    private String ImageURL = "";

    private String JsonURL_form = "http://www.fcupapper.16mb.com/papper/get/single_search/form_sell.php?";
    private String JsonURL_form_single = "http://www.fcupapper.16mb.com/papper/get/single_search/form_sell.php?";
    private String PostURL_delete = "http://www.fcupapper.16mb.com/papper/post/delete/sell.php";
    private String PostURL_modify = "http://www.fcupapper.16mb.com/papper/post/update/sell.php";
    private String JsonURL_leave_message = "http://www.fcupapper.16mb.com/papper/post/discuss.php";
    private String JsonURL_message_board = "http://www.fcupapper.16mb.com/papper/get/single_search/discuss.php?fno=";
    private String JsonURL_message_board_listview = "";
    private String JsonURL_message_delete = "http://www.fcupapper.16mb.com/papper/post/delete/discuss.php";

    List<Message> my_sell_form_message_board_array = new ArrayList<>();

    RequestQueue requestQueue;
    private ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sell_form);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext()).getImageLoader();
        Bundle bundle = getIntent().getExtras();
        Fno = String.valueOf(bundle.getLong("FNO"));

        my_sell_form_listview = (ListView)findViewById(R.id.my_sell_form_listview);
        SetOnItemLongClickListener_Delete_Message();
        //載入該表單資料
        SetFormInfo();
        //載入留言板
        MySellFormMessageBoard();
        //EditText Listener
        OnKeyListener_MySellForm_EditText();
        //Button
        OnClickButtonListener_Delete();
        OnClickButtonListener_Modify();
        OnClickButtonListener_Cancel();
        OnClickButtonListener_LeaveMessage();
        OnClickButtonListener_Buyer();
    }

    private void OnClickButtonListener_Buyer() {
        button_my_sell_form_buyer = (Button) findViewById(R.id.button_my_sell_form_buyer);

        button_my_sell_form_buyer.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent BuyerWorkerActivity = new Intent(MySellFormActivity.this, BuyerWorkerActivity.class);

                        Bundle bundle = new Bundle();
                        bundle.putString("FNO",Fno);//傳遞ID
                        BuyerWorkerActivity.putExtras(bundle);

                        startActivity(BuyerWorkerActivity);
                    }
                }
        );
    }

    private void OnClickButtonListener_Delete() {
        button_my_sell_form_delete = (Button) findViewById(R.id.button_my_sell_form_delete);

        button_my_sell_form_delete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MySellFormDelete();
                    }
                }
        );
    }

    private void OnClickButtonListener_Modify() {
        button_my_sell_form_modify = (Button) findViewById(R.id.button_my_sell_form_modify);

        button_my_sell_form_modify.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!Title.equals("") && !Price.equals("") && !Number.equals("")  && !Description.equals("") && !Transaction.equals("") ) {
                            button_my_sell_form_modify.setEnabled(false);
                            FormInfoReset();
                            MySellForm_Modify();
                        }
                        else{
                            Toast.makeText(v.getContext(), "請勿空欄請重新輸入", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    private void OnClickButtonListener_Cancel() {
        button_my_sell_form_cancel = (Button) findViewById(R.id.button_my_sell_form_cancel);

        button_my_sell_form_cancel.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private void OnClickButtonListener_LeaveMessage() {
        button_my_sell_form_leave_message = (Button) findViewById(R.id.button_my_sell_form_leave_message);

        button_my_sell_form_leave_message.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Leave_Message();
                        button_my_sell_form_leave_message.setEnabled(false);
                        MySellFormLeaveMessge();

                        EditText message_clear = (EditText) findViewById(R.id.my_sell_form_leave_message);
                        message_clear.setText("");
                    }
                }
        );
    }

    private void SetOnItemLongClickListener_Delete_Message(){
        my_sell_form_listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int index, long l) {
                Dialog_Message_Delete(l);
                return false;
            }
        });
    }

    private void Dialog_Message_Delete(final long l){
        AlertDialog.Builder dialog = new AlertDialog.Builder(MySellFormActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_form_check, null);
        TextView Text = (TextView) dialogView.findViewById(R.id.dialog_form_check_text);
        Text.setText("是否要刪除該留言?");
        //載入對話框 的資源黨設定
        dialog.setView(dialogView).setTitle("留言刪除確認");
        dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MySellFormDeleteMessage(String.valueOf(l));
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog.show();
    }

    private void OnKeyListener_MySellForm_EditText(){
        EditText Title_Listener = (EditText) findViewById(R.id.my_sell_form_item_name);
        EditText Price_Listener = (EditText) findViewById(R.id.my_sell_form_item_price);
        EditText Number_Listener = (EditText) findViewById(R.id.my_sell_form_item_number);
        EditText Description_Listener = (EditText) findViewById(R.id.my_sell_form_item_description);
        EditText Transaction_Listener = (EditText) findViewById(R.id.my_sell_form_item_transaction);
        final EditText message_Listener = (EditText) findViewById(R.id.my_sell_form_leave_message);

        message_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                if(message_Listener.getText().toString().trim().equals("")){
                    button_my_sell_form_leave_message.setEnabled(false);
                }
                else button_my_sell_form_leave_message.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Title_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_sell_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Price_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_sell_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Number_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_sell_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Description_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_sell_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });

        Transaction_Listener.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2)
            {
                button_my_sell_form_modify.setEnabled(true);
                return false; //回傳 false 表示到這邊結束，回傳 true 則會繼續原本 onKey 定義的動作。
            }
        });
    }

    private void SetText(){
        EditText EditText_Title = (EditText) findViewById(R.id.my_sell_form_item_name);
        EditText_Title.setText(Title);

        EditText EditText_Price = (EditText) findViewById(R.id.my_sell_form_item_price);
        EditText_Price.setText(Price);

        EditText EditText_Number = (EditText) findViewById(R.id.my_sell_form_item_number);
        EditText_Number.setText(Number);

        EditText EditText_Description = (EditText) findViewById(R.id.my_sell_form_item_description);
        EditText_Description.setText(Description);

        EditText EditText_Transaction = (EditText) findViewById(R.id.my_sell_form_item_transaction);
        EditText_Transaction.setText(Transaction);

        TextView Text_Form_createtime = (TextView) findViewById(R.id.my_sell_form_createtime);
        Text_Form_createtime.setText(Form_createtime);

        NetworkImageView image = (NetworkImageView) findViewById(R.id.my_sell_form_image);
        image.setImageUrl(ImageURL, imageLoader);
        image.setErrorImageResId(R.mipmap.defaultimage);
    }

    private void FormInfoReset(){
        EditText EditText_Title = (EditText) findViewById(R.id.my_sell_form_item_name);
        EditText EditText_Price = (EditText) findViewById(R.id.my_sell_form_item_price);
        EditText EditText_Number = (EditText) findViewById(R.id.my_sell_form_item_number);
        EditText EditText_Description = (EditText) findViewById(R.id.my_sell_form_item_description);
        EditText EditText_Transaction = (EditText) findViewById(R.id.my_sell_form_item_transaction);

        Title = EditText_Title.getText().toString().trim();
        Price = EditText_Price.getText().toString().trim();
        Number = EditText_Number.getText().toString().trim();
        Description = EditText_Description.getText().toString().trim();
        Transaction = EditText_Transaction.getText().toString().trim();
    }

    private void Leave_Message(){
        EditText message = (EditText) findViewById(R.id.my_sell_form_leave_message);
        my_sell_form_message = message.getText().toString();

        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
        Uid = userinfo.getString("UID","");
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

            return convertView;
        }
    }

    private void SetFormInfo(){
        requestQueue = Volley.newRequestQueue(this);
        JsonURL_form_single = JsonURL_form + "fno=" + Fno;
        Log.d("JsonURL_form_single",JsonURL_form_single);
        JsonObjectRequest obreq_FromInfo = new JsonObjectRequest(Request.Method.GET, JsonURL_form_single,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Sell");

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
        requestQueue.add(obreq_FromInfo);
    }

    private void MySellFormDelete() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MySellFormDelete = new StringRequest(Request.Method.POST,
                PostURL_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(MySellFormActivity.this, "資料刪除成功", Toast.LENGTH_LONG).show();
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
                Toast.makeText(MySellFormActivity.this, "資料刪除失敗請重試", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("fno", Fno);

                return params;
            }
        };
        requestQueue.add(obreq_MySellFormDelete);
    }

    private void MySellForm_Modify() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_SellForm_Modify = new StringRequest(Request.Method.POST,
                PostURL_modify, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(MySellFormActivity.this, "資料修改成功", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(MySellFormActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
                button_my_sell_form_modify.setEnabled(true);
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("fno", Fno);
                params.put("title", Title);
                params.put("sprice", Price);
                params.put("number", Number);
                params.put("content", Description);
                params.put("transaction", Transaction);

                Log.d("Fno",Fno);
                Log.d("title", Title);
                Log.d("price", Price);
                Log.d("number", Number);
                Log.d("content", Description);
                Log.d("transaction", Transaction);

                return params;
            }
        };
        requestQueue.add(obreq_SellForm_Modify);
    }

    //載入留言板資料
    private void MySellFormMessageBoard(){
        requestQueue = Volley.newRequestQueue(this);
        JsonURL_message_board_listview = JsonURL_message_board + Fno;
        Log.d("JsonURL_message_board",JsonURL_message_board_listview);
        JsonObjectRequest obreq_SellFormMessage = new JsonObjectRequest(Request.Method.GET, JsonURL_message_board_listview,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Discuss");
                            my_sell_form_message_board_array.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                my_sell_form_message_board_array.add(new Message(array.getJSONObject(i).getString("DUID")
                                        ,array.getJSONObject(i).getString("Name")
                                        ,array.getJSONObject(i).getString("DText")
                                        ,array.getJSONObject(i).getString("DHour")
                                        ,array.getJSONObject(i).getString("DNO")));
                            }
                            my_sell_form_listview.setAdapter(new MessageBoard_Adapter(MySellFormActivity.this, my_sell_form_message_board_array));

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

    //留言功能
    private void MySellFormLeaveMessge() {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MyWorkFormDelete = new StringRequest(Request.Method.POST,
                JsonURL_leave_message, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    MySellFormMessageBoard();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(MySellFormActivity.this ,"網路不穩請重試", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected HashMap<String, String> getParams() {

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("uid", Uid);
                params.put("fno", Fno);
                params.put("text", my_sell_form_message);

                return params;
            }
        };
        requestQueue.add(obreq_MyWorkFormDelete);
    }

    private void MySellFormDeleteMessage(final String Dno) {
        requestQueue = Volley.newRequestQueue(this);

        StringRequest obreq_MySellFormDeleteMessage = new StringRequest(Request.Method.POST,
                JsonURL_message_delete, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                    Toast.makeText(MySellFormActivity.this, "留言刪除成功", Toast.LENGTH_LONG).show();
                    MySellFormMessageBoard();
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // error
                Log.d("Error.Response", error.getMessage());
                Toast.makeText(MySellFormActivity.this, "留言刪除失敗請重試", Toast.LENGTH_LONG).show();
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
