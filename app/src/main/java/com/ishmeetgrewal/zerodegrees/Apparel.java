package com.ishmeetgrewal.zerodegrees;

public class Apparel {

    int icon;
    int startRange;
    int endRange;
    boolean userHas;

    public Apparel(int icon, boolean userHas) {

        this.icon = icon;
        this.userHas = userHas;

    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean getUserHas(){
        return userHas;
    }

    public void setUserHas(boolean value){
        this.userHas = value;
    }

}
