package com.androidapp.pizzamania;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class SqlLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "PizzaMania";
    private static final int DB_VERSION = 1;

    public SqlLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createOrderTable = "Create table orders (" +
                "order_id int primary key autoincrement," +
                "user_id text," +
                "branch_id text," +
                "total_price real," +
                "created_at Text," +
                "latitude real," +
                "longitude real)";

        String createOrderItemTable = "Create table order_items (" +
                "id primary key autoincrement," +
                "order_id int," +
                "item_id Text," +
                "quantity int," +
                "price real," +
                "foreign key(order_id) references orders(order_id))";

        db.execSQL(createOrderTable);
        db.execSQL(createOrderItemTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
