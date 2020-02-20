package com.example.admin.graduationproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class BuyerWorkerActivity extends AppCompatActivity {

    private Button button_buyer_worker_return;

    private ListView buyerworker_listview;

    private String Fno;

    private String JsomURL_BuyerWorker = "http://www.fcupapper.16mb.com/papper/get/single_search/focus.php?ffno=";
    private String JsomURL_BuyerWorker_FFNO = "http://www.fcupapper.16mb.com/papper/get/single_search/focus.php?ffno=";
    private String JsonURL_BuyerWorker_Check = "http://www.fcupapper.16mb.com/papper/post/focus.php";



    RequestQueue requestQueue;

    List<BuyerWorker> BuyerWorkerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buyer_worker);

        buyerworker_listview = (ListView) findViewById(R.id.buyer_worker_listview) ;
        OnItemClickListener_WhoCanBuy();

        Bundle bundle = getIntent().getExtras();
        Fno = bundle.getString("FNO");

        BuyerWorker_ListView();
        OnClickButtonListener_Cancel();
    }

    private void OnItemClickListener_WhoCanBuy(){
        buyerworker_listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(BuyerWorkerActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_buyerworker, null);
                //載入對話框 的資源黨設定
                dialog.setView(dialogView).setTitle("買家/打工者確認");
                dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BuyerWorker_BuyerCheck(id);
                        Toast.makeText(BuyerWorkerActivity.this, "表單確認完成", Toast.LENGTH_LONG).show();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
    }

    private void OnClickButtonListener_Cancel() {
        button_buyer_worker_return = (Button) findViewById(R.id.button_buyer_worker_return);

        button_buyer_worker_return.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }

    private class BuyerWorker_Adapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private List<BuyerWorker> buyerList;

        private BuyerWorker_Adapter(Context context, List<BuyerWorker> buyerList){
            layoutInflater = LayoutInflater.from(context);
            this.buyerList = buyerList;
        }
        public int getCount(){
            return buyerList.size();
        }

        public Object getItem(int position){
            return buyerList.get(position);
        }
        @Override
        public long getItemId(int position) {
            return buyerList.get(position).getId();
        }

        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView==null) convertView = layoutInflater.inflate(R.layout.listview_buyerworker, parent, false);//取得listItem容器 view

            BuyerWorker buyer = buyerList.get(position);

            TextView Text_Name = (TextView) convertView.findViewById(R.id.listview_buyerworker_name);
            Text_Name.setText(buyer.getName());

            TextView Text_School = (TextView) convertView.findViewById(R.id.listview_buyerworker_school);
            Text_School.setText(buyer.getSchool());

            TextView Text_Department = (TextView) convertView.findViewById(R.id.listview_buyerworker_department);
            Text_Department.setText(buyer.getDepartment());

            return convertView;
        }
    }

    private void BuyerWorker_ListView(){
        JsomURL_BuyerWorker_FFNO = JsomURL_BuyerWorker + Fno;
        Log.d("JsomURL_BuyerWorker",JsomURL_BuyerWorker_FFNO);//除錯用
        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest obreq_Itemlist_Search = new JsonObjectRequest(Request.Method.GET, JsomURL_BuyerWorker_FFNO,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray array = response.getJSONArray("Focus");
                            BuyerWorkerList.clear();
                            for (int i = 0 ; i < array.length() ; i++){
                                if(array.getJSONObject(i).getString("FStatus").equals("2")) {
                                    BuyerWorkerList.add(new BuyerWorker(array.getJSONObject(i).getString("FUID")
                                            , array.getJSONObject(i).getString("Name")
                                            , array.getJSONObject(i).getString("School")
                                            , array.getJSONObject(i).getString("Class")));
                                }
                            }
                            buyerworker_listview.setAdapter(new BuyerWorker_Adapter(BuyerWorkerActivity.this,BuyerWorkerList));
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
        requestQueue.add(obreq_Itemlist_Search);
    }

    private void BuyerWorker_BuyerCheck(long id) {
        final String Uid = String.valueOf(id);
        requestQueue = Volley.newRequestQueue(this);
        StringRequest obreq_BuyerWorker_BuyerCheck = new StringRequest(Request.Method.POST,
                JsonURL_BuyerWorker_Check, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // response
                try {
                    Log.d("Tag",response);
                }catch (Exception e){
                    Log.e("jsonException",e.toString());
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

                params.put("fuid", Uid);
                params.put("ffno", Fno);
                params.put("fstatus", "3");

                return params;
            }
        };
        requestQueue.add(obreq_BuyerWorker_BuyerCheck);
    }
}
