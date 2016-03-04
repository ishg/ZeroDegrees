package com.ishmeetgrewal.zerodegrees;

import java.util.UUID;

public class Apparel {

    UUID id;
    String name;
    int icon;
    int startRange;
    int endRange;
    String type;

    public Apparel(String name, int icon, int startRange, int endRange, String type) {
        this.name = name;
        this.icon = icon;
        this.startRange = startRange;
        this.endRange = endRange;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getStartRange() {
        return startRange;
    }

    public void setStartRange(int startRange) {
        this.startRange = startRange;
    }

    public int getEndRange() {
        return endRange;
    }

    public void setEndRange(int endRange) {
        this.endRange = endRange;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
