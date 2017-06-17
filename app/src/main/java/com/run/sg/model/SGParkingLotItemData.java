package com.run.sg.model;

/**
 * Created by yq on 2017/6/11.
 */
public class SGParkingLotItemData {

    private String mName;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private int mVacancy;
    private int mLeft;
    private double mPrice;
    private double mDistance;

    public SGParkingLotItemData(String name, String address, double latitude, double longitude,
                                int vacancy, int left, double price, double distance){
        this.mName = name;
        this.mAddress = address;
        this.mLatitude = latitude;
        this.mLongitude = longitude;
        this.mVacancy = vacancy;
        this.mLeft = left;
        this.mPrice = price;
        this.mDistance = distance;
    }

    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public int getVacancy(){
        return mVacancy;
    }

    public int getLeft(){
        return mLeft;
    }

    public double getPrice(){
        return mPrice;
    }

    public double getDistance(){
        return mDistance;
    }
}
