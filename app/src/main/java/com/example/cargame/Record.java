package com.example.cargame;
import java.io.Serializable;

public class Record implements Serializable {
    private int distance;
    private int coins;
    private double latitude;
    private double longitude;


    public Record(int distance, int coins, double latitude, double longitude) {
        this.distance = distance;
        this.coins = coins;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getDistance() {
        return distance;
    }

    public int getCoins() {
        return coins;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
