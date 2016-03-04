package com.ishmeetgrewal.zerodegrees;

public class User {

    String name;
    String home;
    int temp;
    Apparel[] clothes;
    Location[] locations;

    public User(String name, String home, int temp) {
        this.name = name;
        this.home = home;
        this.temp = temp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
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

    public Location[] getLocations() {
        return locations;
    }

}
