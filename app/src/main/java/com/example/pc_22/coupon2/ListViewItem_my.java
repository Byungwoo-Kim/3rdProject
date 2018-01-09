package com.example.pc_22.coupon2;

import android.graphics.drawable.Drawable;

public class ListViewItem_my {
    private Drawable iconDrawable ;
    private String titleStr ;
    private String descStr ;
    private String timeStr2 ;
    private String place ;
    private int mytimer;

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

    public void setIcon(Drawable icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setDesc(String desc) {
        descStr = desc ;
    }

    public String getTimeStr2() {
        return timeStr2;
    }

    public void setTimeStr2(String timeStr2) {
        this.timeStr2 = timeStr2;
    }

    public Drawable getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getDesc() {
        return this.descStr ;
    }
}
