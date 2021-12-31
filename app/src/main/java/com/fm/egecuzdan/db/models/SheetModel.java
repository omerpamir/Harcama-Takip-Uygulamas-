package com.fm.egecuzdan.db.models;

public class SheetModel {

    private int id;
    private String ay;
    private String yıl;
    private double gelir;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAy() {
        return ay;
    }

    public void setAy(String ay) {
        this.ay = ay;
    }

    public String getYıl() {
        return yıl;
    }

    public void setYıl(String yıl) {
        this.yıl = yıl;
    }

    public double getGelir() {
        return gelir;
    }

    public void setGelir(double gelir) {
        this.gelir = gelir;
    }
}