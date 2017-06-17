package com.run.sg.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteFullException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;
import java.util.Map;

/**
 * Created by Daijie on 17/6/17.
 */

public class ParkDBHelper extends SQLiteOpenHelper {
    public static final String TAG = "ParkDBHelper";
    private static ParkDBHelper mInstance;
    private static final String DBNAME = "park_info.db";
    private static final int VERSION = 1;
    private Context mContext;
    private SQLiteDatabase mDB;

    private ParkDBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
        mContext = context;
    }

    public static synchronized ParkDBHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ParkDBHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ParkInfoTable.createTable(db);
        RecentlyGoTable.createTable(db);
        initDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            ParkInfoTable.dropTable(db);
            ParkInfoTable.createTable(db);
            RecentlyGoTable.dropTable(db);
            RecentlyGoTable.createTable(db);
        }
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion > newVersion) {
            ParkInfoTable.dropTable(db);
            ParkInfoTable.createTable(db);
            RecentlyGoTable.dropTable(db);
            RecentlyGoTable.createTable(db);
        }
    }

    public synchronized Cursor query(String tableName, String[] columns,
                                     String selection, String[] selectionArgs,
                                     String groupBy, String having, String orderBy) {
        SQLiteDatabase readableDatabase = getReadableDatabase();
        if (readableDatabase == null) {
            return null;
        }
        Log.d(TAG, "query");
        return readableDatabase.query(tableName, columns, selection,
                selectionArgs, groupBy, having, orderBy);
    }

    public synchronized long insert(String tableName, ContentValues values) {
        SQLiteDatabase writableDatabase = getMyWritableDatabase();
        if (writableDatabase == null) {
            return -1;
        }
        Log.d(TAG, "insert one");
        return writableDatabase.insert(tableName, null, values);
    }

    public synchronized int insert(String tableName, List<ContentValues> values) {
        SQLiteDatabase writableDabase = getMyWritableDatabase();
        if (writableDabase == null) {
            return -1;
        }
        int count = 0;
        writableDabase.beginTransaction();
        try {
            for (ContentValues contentValues :values) {
                long rowId = writableDabase.insert(tableName, null, contentValues);
                if (rowId != -1) {
                    count++;
                }
            }
            writableDabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writableDabase.endTransaction();
        }
        Log.d(TAG, "insert more than one");
        return count;
    }

    public synchronized long delete(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase writableDatabase = getMyWritableDatabase();
        if (writableDatabase == null) {
            return -1;
        }
        Log.d(TAG, "delete one");
        return writableDatabase.delete(tableName, whereClause, whereArgs);
    }

    public synchronized int delete(String tableName, String whereClause, List<String> keyList) {
        SQLiteDatabase writableDatabase = getMyWritableDatabase();
        if (writableDatabase == null) {
            return -1;
        }
        int count = 0;
        writableDatabase.beginTransaction();
        try {
            for (String key : keyList) {
                long rowId = writableDatabase.delete(tableName, whereClause, new String[] { key });
                if (rowId != -1) {
                    count++;
                }
            }
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writableDatabase.endTransaction();
        }
        Log.d(TAG, "delete more than one");
        return count;
    }

    public synchronized long update(String tableName, ContentValues values,
                                     String whereClause, String[] whereArgs) {
        SQLiteDatabase writableDatabse = getMyWritableDatabase();
        if (writableDatabse == null) {
            return -1;
        }
        Log.d(TAG, "update one");
        return writableDatabse.update(tableName, values, whereClause, whereArgs);
    }

    public synchronized int update(String tableName, String whereClause,
                                   Map<String, ContentValues> values) {
        SQLiteDatabase writableDatabase = getMyWritableDatabase();
        if (writableDatabase == null) {
            return -1;
        }
        int count = 0;
        writableDatabase.beginTransaction();
        try {
            for (String key : values.keySet()) {
                long rowId = writableDatabase.update(tableName, values.get(key),
                        whereClause, new String[] { key });
                if (rowId != -1) {
                    count++;
                }
            }
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writableDatabase.endTransaction();
        }
        Log.d(TAG, "update more than one");
        return count;
    }

    private SQLiteDatabase getMyWritableDatabase() {
        if (mDB == null) {
            try {
                mDB = getWritableDatabase();
            } catch (SQLiteFullException e) {
                e.printStackTrace();
            }
        }
        return mDB;
    }

    private void initDefaultData(SQLiteDatabase db) {
        for (String[] parkInfo : ParkInfoTable.defaultParkInfo) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(ParkInfoTable.PARK_NAME, parkInfo[0]);
            contentValues.put(ParkInfoTable.PARK_ADDRESS, parkInfo[1]);
            contentValues.put(ParkInfoTable.PARK_LONGITUDE, parkInfo[2]);
            contentValues.put(ParkInfoTable.PARK_LATITUDE, parkInfo[3]);
            contentValues.put(ParkInfoTable.PARK_TOTAL_PARKING, parkInfo[4]);
            contentValues.put(ParkInfoTable.PARK_PRICE, parkInfo[5]);
            db.insert(ParkInfoTable.TABLE_NAME, null, contentValues);
        }
    }
}
