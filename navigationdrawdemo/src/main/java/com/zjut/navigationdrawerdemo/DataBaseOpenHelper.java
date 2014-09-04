package com.zjut.navigationdrawerdemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lake on 14-9-4.
 */
public class DataBaseOpenHelper extends SQLiteOpenHelper {

    private static DataBaseOpenHelper mInstance = null;
    private static String DATABASE_NAME="db";
    private static int DATABASE_VERSION =1;

    public DataBaseOpenHelper(Context context, String name,
                              SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public DataBaseOpenHelper(Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION );
    }

    //单例模式
    static synchronized DataBaseOpenHelper getInstance(Context context)
    {
        if(mInstance==null)
            mInstance=new DataBaseOpenHelper(context);
        return mInstance;
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }

    public boolean deleteDatabase(Context context)
    {
        return context.deleteDatabase(DATABASE_NAME);
    }
}
