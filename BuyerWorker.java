package com.example.admin.graduationproject;

/**
 * Created by admin on 2016/11/17.
 */
public class BuyerWorker {

    private int uid = 0;
    private String name = "";
    private String school = "";
    private String department = "";

    public BuyerWorker(String uid, String name, String school, String department){
        this.uid = Integer.valueOf(uid);
        this.name = name;
        this.school = school;
        this.department = department;
    }

    public int getId(){return this.uid;}

    public String getName(){return this.name;}

    public String getSchool(){return this.school;}

    public String getDepartment(){return this.department;}
}
