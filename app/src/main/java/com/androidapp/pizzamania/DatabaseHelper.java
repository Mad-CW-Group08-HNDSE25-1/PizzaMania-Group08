package com.androidapp.pizzamania;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pizza.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USER_SESSION = "UserSession";
    public static final String TABLE_CART = "Cart";           // <- Add this
    public static final String TABLE_OFFLINE_ORDERS = "OfflineOrders"; // <- Add this

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER_SESSION + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "userId TEXT," +
                "name TEXT," +
                "email TEXT," +
                "phone TEXT," +
                "profileImageUrl TEXT," +
                "isLoggedIn INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_CART + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "itemId TEXT," +
                "name TEXT," +
                "price REAL," +
                "quantity INTEGER)");

        db.execSQL("CREATE TABLE " + TABLE_OFFLINE_ORDERS + " (" +
                "localId INTEGER PRIMARY KEY AUTOINCREMENT," +
                "orderId TEXT," +
                "userId TEXT," +
                "itemsJson TEXT," +
                "totalPrice REAL," +
                "status TEXT," +
                "createdAt TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SESSION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_ORDERS);
        onCreate(db);
    }
}
