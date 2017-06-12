package com.run.sg.model;

/**
 * Created by yq on 2017/6/11.
 */
public class SGParkingLotItemData {

    private String mName;
    private String mAddress;
    private int mPrice;
    private double mDistance;
    private int mVacancy;

    public SGParkingLotItemData(String name,String address,int price,double distance,int vacancy){
        this.mName = name;
        this.mAddress = address;
        this.mPrice = price;
        this.mDistance = distance;
        this.mVacancy = vacancy;
    }

    public String getName(){
        return mName;
    }

    public String getAddress(){
        return mAddress;
    }

    public int getPrice(){
        return mPrice;
    }

    public double getDistance(){
        return mDistance;
    }

    public int getVacancy(){
        return mVacancy;
    }

}
