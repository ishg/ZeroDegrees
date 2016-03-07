package com.ishmeetgrewal.zerodegrees;

import java.util.UUID;

public class User {

    int id;
    String name;
    int homeID;
    int temp;
    Apparel[] clothes;
    Place[] locations;

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

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public Apparel[] getClothes() {
        return clothes;
    }

    public Place[] getLocations() {
        return locations;
    }

}
