package com.run.sg.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Daijie on 17/6/17.
 */

public class ParkInfoTable {
    public static final String TABLE_NAME = "park_info_table";
    public static final String ID = "id";
    public static final String PARK_NAME = "park_name";
    public static final String PARK_ADDRESS = "park_address";
    public static final String PARK_LONGITUDE = "park_longitude";
    public static final String PARK_LATITUDE = "park_latitude";
    public static final String PARK_TOTAL_PARKING = "park_total_parking";
    public static final String PARK_PRICE = "park_price";

    private static final String SQL_CREATE_TABLE = "create table if not exists "
            + TABLE_NAME + "("
            + ID + " integer primary key autoincrement, "
            + PARK_NAME + " text, "
            + PARK_ADDRESS + " text, "
            + PARK_LATITUDE + " text, "
            + PARK_LONGITUDE + " text, "
            + PARK_TOTAL_PARKING + " integer, "
            + PARK_PRICE + " text)";

    private static final String SQL_DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL(SQL_DROP_TABLE);
    }

    public static String[][] defaultParkInfo = {
            //名字，地址，纬度，经度，数量，价格
            {"大族创新大厦B座停车场", "北环大道9018号大族创新大厦", "22.55375", "113.950646", "100", "3"},
            {"天明科技大厦停车场", "乌石头路8号天明科技大厦", "22.554618", "113.950827", "200", "2"},
            {"源兴科技大厦停车场", "松坪山路1号源兴科技大厦", "22.554186", "113.951824", "300", "5"},
            {"腾讯大厦停车场", "深南大道10000号腾讯大厦", "22.540768", "113.934392", "400", "1"},
            {"海岸城购物中心停车场", "文心三路6号海岸城购物中心", "22.517146", "113.934766", "500", "4"}
    };
}
