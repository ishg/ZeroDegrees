package com.ishmeetgrewal.zerodegrees;

public class Place {

    private static final String LOG = "PlaceModel";

    double lat;
    double lon;
    String name;
    int temp;
    String windSpeed;
    String visibility;
    String precipitation;

    public Place(String name, double lat, double lon){
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getName(){
        return name;
    }

    public double getLat(){
        return lat;
    }

    public double getLon(){
        return lon;
    }

    public int getTemp(){
        return temp;
    }
    public void setTemp(int temp){
        this.temp = temp;
    }

    public String getVisibility(){
        return visibility;
    }
    public void setVisibility(String visi){
        this.visibility = visi;
    }

    public String getPrecipitation(){
        return precipitation;
    }
    public void setPrecipitation(String preci){
        this.precipitation = preci;
    }

    public String getWindSpeed(){
        return windSpeed;
    }
    public void setWindSpeed(String wind) {
        this.windSpeed = wind;
    }






}
