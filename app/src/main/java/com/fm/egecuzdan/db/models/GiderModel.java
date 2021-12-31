package com.fm.egecuzdan.db.models;

import java.io.Serializable;

public class GiderModel implements Serializable {

    private int id;
    private String açıklama;
    private String miktar;
    private String tarih;
    private boolean düzenliÖdemedir;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAçıklama() {
        return açıklama;
    }

    public void setAçıklama(String açıklama) {
        this.açıklama = açıklama;
    }

    public String getMiktar() {
        return miktar;
    }

    public void setMiktar(String miktar) {
        this.miktar = miktar;
    }

    public String getTarih() {
        return tarih;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public boolean düzenliÖdemedir() {
        return düzenliÖdemedir;
    }

    public void setDüzenliÖdemedir(boolean düzenliÖdemedir) {
        this.düzenliÖdemedir = düzenliÖdemedir;
    }
}