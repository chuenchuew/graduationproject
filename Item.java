package com.example.admin.graduationproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/9/16.
 */
public class Item {

    private int id = 0;
    private String price = "";
    private String name = "";
    private String number;
    private String type = "";
    private Bitmap image ;
    private String imageURL = "";
    private String status = "";

    public Item(String id,String name,String number,String price,String imageURL,String status){
        this.id = Integer.valueOf(id);
        this.name = name;
        this.number = number;
        this.price = price;
        this.imageURL = imageURL;
        if(status.equals("1")){
            this.status = "未購買此商品";
        }
        else if(status.equals("2")){
            this.status = "等待賣家確認";
        }
        else if(status.equals("3")){
            this.status = "賣家已確認";
        }
    }

    public int getId(){ return id; }

    public String getName(){
        return name;
    }

    public String getPrice(){
        return price;
    }

    public String getNumber(){
        return number;
    }

    public String getType(){ return type; }

    public Bitmap getImage(){ return image;}

    public String getImageURL(){return imageURL;}

    public String getStatus(){return status;}

    public void setId(String id){
        this.id = Integer.valueOf(id);
    }

    public void setName(String name){
        this.name = name;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public void setType(String type){
        this.type = type;
    }

    public void setImage(Bitmap image) {this.image = image;}
}
