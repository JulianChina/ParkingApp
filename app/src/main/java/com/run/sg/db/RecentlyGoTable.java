package com.run.sg.db;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Daijie on 17/6/17.
 */

public class RecentlyGoTable {
    public static final String TABLE_NAME = "recently_go_table";
    public static final String ID = "id";
    public static final String PARK_NAME = "park_name";
    public static final String PARK_ADDRESS = "park_address";
    public static final String PARK_PRICE = "park_price";

    private static final String SQL_CREATE_TABLE = "create table if not exists "
            + TABLE_NAME + "("
            + ID + " integer primary key autoincrement, "
            + PARK_NAME + " text, "
            + PARK_ADDRESS + " text, "
            + PARK_PRICE + " integer)";

    private static final String SQL_DROP_TABLE = "drop table if exists " + TABLE_NAME;

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    public static void dropTable(SQLiteDatabase db) {
        db.execSQL(SQL_DROP_TABLE);
    }
}
