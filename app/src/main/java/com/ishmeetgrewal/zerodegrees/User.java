package com.ishmeetgrewal.zerodegrees;


import java.util.ArrayList;


public class User {

    int id;
    String name;
    int homeID;
    int temp;
    ArrayList<Apparel> clothes;



    public User(String name, int homeID, int temp) {
        this.name = name;
        this.homeID = homeID;
        this.temp = temp;
    }


    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHome() {
        return homeID;
    }

    public void setHome(int home) {
        this.homeID = home;
    }

    public int getTemp() {
        return temp;
    }



}
