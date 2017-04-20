package com.vasyaevstropov.runmanager.Models;

public class Coordinates {

    public static final String TABLE_NAME_SPEEDTABLE = "speedtable";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NUMB_RECORD = "numberrecord";
    public static final String COLUMN_NAME_RECORD = "namerecord";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_SPEED = "speed";
    public static final String COLUMN_TIME = "time";

    public static final String TABLE_NAME_SEGMENT = "segmenttable";
    public static final String COLUMN_DAYOFWEEK = "dayofweek";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DISTANCE = "distance";

    public long id;
    private String numberRecord, nameRecord, longitude, latitude, speed, time;
    private String dayOfWeek, date, distance;

    public Coordinates(String numberRecord, String nameRecord, String longitude, String latitude, String speed, String time){
        this.nameRecord = numberRecord;
        this.nameRecord = nameRecord;
        this.longitude = longitude;
        this.latitude = latitude;
        this.speed = speed;
        this.time = time;
    }
    public Coordinates(String dayOfWeek, String date, String distance){
        this.dayOfWeek = dayOfWeek;
        this.date = date;
        this.distance = distance;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumberRecord() {
        return numberRecord;
    }

    public void setNumberRecord(String numberRecord) {
        this.numberRecord = numberRecord;
    }

    public String getNameRecord() {
        return nameRecord;
    }

    public void setNameRecord(String nameRecord) {
        this.nameRecord = nameRecord;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
