package com.example.pc_22.coupon2;

/**
 * Created by pc-22 on 2018-01-08.
 */

class clist {
    String name;
    String con;
    String time;
    String place;
    int mytimer;

    public clist() {
    }
    public clist(String name, String con, String time) {
        this.name = name;
        this.con = con;
        this.time = time;
    }

    public clist(String name, String con, String time, String place) {
        this.name = name;
        this.con = con;
        this.time = time;
        this.place = place;
    }

    public clist(String name, String con, String time, String place,int mytimer) {
        this.name = name;
        this.con = con;
        this.time = time;
        this.place = place;
        this.mytimer = mytimer;
    }


    public int getMytimer() {
        return mytimer;
    }

    public void setMytimer(int mytimer) {
        this.mytimer = mytimer;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCon() {
        return con;
    }

    public void setCon(String con) {
        this.con = con;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
