package com.example.womensafety.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SafePlace {

    @JsonAlias("display_name") // for reading from Nominatim
    @JsonProperty("pname")     // for sending to frontend
    private String pname;

    @JsonProperty("lat")
    private String latStr;

    @JsonProperty("lon")
    private String lonStr;

    private double plat;
    private double plng;
    private double pdistance;

    public SafePlace(){}

    public SafePlace(String pname, double pdistance) {
        this.pname=pname;
        this.pdistance=pdistance;
    }

    @JsonIgnore
    public void convertLatLon() {
        if (latStr != null && lonStr != null) {
            try {
                this.plat = Double.parseDouble(latStr);
                this.plng = Double.parseDouble(lonStr);
            } catch (NumberFormatException e) {
                this.plat = 0;
                this.plng = 0;
            }
        }
    }

    // Getters & setters
    public String getPname() { return pname; }
    public void setPname(String pname) { this.pname = pname; }

    public double getPlat() { return plat; }
    public void setPlat(double plat) { this.plat = plat; }

    public double getPlng() { return plng; }
    public void setPlng(double plng) { this.plng = plng; }

    public double getPdistance() { return pdistance; }
    public void setPdistance(double pdistance) { this.pdistance = pdistance; }

    public void setLatStr(String latStr) { this.latStr = latStr; }
    public void setLonStr(String lonStr) { this.lonStr = lonStr; }
}



