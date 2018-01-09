package com.example.pc_22.coupon2;

/**
 * Created by pc-22 on 2018-01-08.
 */

class tolocation {
    String pcname;
    String place;
    double pclat;
    double pclng;

    public tolocation() {
    }

    public tolocation(String pcname, String place, double pclat, double pclng) {
        this.pcname = pcname;
        this.place = place;
        this.pclat = pclat;
        this.pclng = pclng;
    }

    public String getPcname() {
        return pcname;
    }

    public void setPcname(String pcname) {
        this.pcname = pcname;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public double getPclat() {
        return pclat;
    }

    public void setPclat(double pclat) {
        this.pclat = pclat;
    }

    public double getPclng() {
        return pclng;
    }

    public void setPclng(double pclng) {
        this.pclng = pclng;
    }
}
