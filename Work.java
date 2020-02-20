package com.example.admin.graduationproject;

/**
 * Created by admin on 2016/10/9.
 */
public class Work {
    private int id = 0;
    private String name = "";
    private String hours = "";
    private String salary = "";
    private String number = "";
    private String pay = "";
    private String place = "";
    private String status = "";

    public Work(String id,String Title,String Salary,String Hours,String status){
        this.id = Integer.valueOf(id);
        this.name = Title;
        this.salary = Salary;
        this.hours = Hours;
        if(status.equals("1")){
            this.status = "未應徵此工作";
        }
        else if(status.equals("2")){
            this.status = "等待資方確認";
        }
        else if(status.equals("3")){
            this.status = "資方已確認";
        }
    }

    public int getId(){ return id; }

    public String getName(){
        return name;
    }

    public String getHours(){
        return hours;
    }

    public String getNumber(){
        return number;
    }

    public String getSalary() { return salary; }

    public String getPay() { return pay; }

    public String getPlace() { return place; }

    public String getStatus(){ return status; }

    public void setId(String id){
        this.id = Integer.valueOf(id);
    }

    public void setName(String name){
        this.name = name;
    }

    public void setHours(String hours){
        this.hours = hours;
    }

    public void setNumber(String number){
        this.number = number;
    }

    public void setSalary(String salary) { this.salary = salary; }

    public void setPay(String pay) { this.pay = pay; }

    public void setPlace(String place) { this.salary = place; }
}
