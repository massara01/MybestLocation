package com.example.mybestlocation;

public class Position {
    int id;
    String longitude , latitude , description ;

    public Position(int id, String longitude, String latitude, String description) {
        this.id = id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
