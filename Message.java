package com.example.admin.graduationproject;

/**
 * Created by admin on 2016/11/12.
 */
public class Message {
    private int id = 0;
    private String name = "";
    private String message = "";
    private String message_time = "";
    private String dno = "";

    public Message(String id, String name, String message, String message_time, String dno){
        this.id = Integer.valueOf(id);
        this.name = name;
        this.message = message;
        this.message_time = message_time;
        this.dno = dno;
    }

    public int getId(){ return id;}

    public String getName() { return name; }

    public String getMessage() { return message; }

    public String getMessage_time() { return message_time; }

    public int getDno(){ return Integer.valueOf(dno);}

    public void setId(String id){ this.id = Integer.valueOf(id);}

    public void setName(String name){ this.name = name;}

    public void setMessage(String message){ this.message = message;}

    public void setMessage_time(String message_time){ this.message_time = message_time;}
}
