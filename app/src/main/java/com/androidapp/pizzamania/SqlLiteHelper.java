package com.androidapp.pizzamania;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

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
                "status text," +
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

    public void InsertOrder(OrderDTO orderDTO) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.beginTransaction();

        try {

            ContentValues orderValues = new ContentValues();
            orderValues.put("order_id", orderDTO.getOrderID());
            orderValues.put("user_id", orderDTO.getUserID());
            orderValues.put("branch_id", orderDTO.getBranchID());
            orderValues.put("total_price", orderDTO.getTotalAmount());
            orderValues.put("status", orderDTO.getOrderStatus());
            orderValues.put("created_at", orderDTO.getCreatedAt());
            orderValues.put("latitude", orderDTO.getLocation().getLatitude());
            orderValues.put("longitude", orderDTO.getLocation().getLongitude());

            sqLiteDatabase.insert("orders", null, orderValues);

            for (ItemDTO item : orderDTO.getItemList()) {
                ContentValues itemVals = new ContentValues();
                itemVals.put("order_id", item.getOrderID());
                itemVals.put("item_id", item.getItemID());
                itemVals.put("quantity", item.getQty());
                itemVals.put("price", item.getPrice());

                sqLiteDatabase.insert("order_items", null, itemVals);
            }

            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            sqLiteDatabase.endTransaction();
            sqLiteDatabase.close();
        }
    }

    public List<OrderDTO> getOrderByUser(String userId) {

        List<OrderDTO> orderDTOList = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery(
                "Select * from orders where user_id = ?", new String[]{userId}
        );

        try {
            if (cursor.moveToFirst()) {
                do {

                    OrderDTO orderDTO = new OrderDTO();

                    orderDTO.setOrderID(cursor.getString(cursor.getColumnIndexOrThrow("order_id")));
                    orderDTO.setUserID(cursor.getString(cursor.getColumnIndexOrThrow("user_id")));
                    orderDTO.setBranchID(cursor.getString(cursor.getColumnIndexOrThrow("branch_id")));
                    orderDTO.setTotalAmount(cursor.getDouble(cursor.getColumnIndexOrThrow("total_price")));
                    orderDTO.setOrderStatus(cursor.getString(cursor.getColumnIndexOrThrow("status")));
                    orderDTO.setCreatedAt(cursor.getString(cursor.getColumnIndexOrThrow("created_at")));
                    OrderDTO.Location location = new OrderDTO.Location(
                      cursor.getDouble(cursor.getColumnIndexOrThrow("latitude")),
                      cursor.getDouble(cursor.getColumnIndexOrThrow("longitude"))
                    );

                    orderDTO.setLocation(location);

                    List<ItemDTO> itemDTOList = new ArrayList<>();
                    Cursor itemCursor = sqLiteDatabase.rawQuery(
                            "select * from order_items where order_id =  ?", new String[]{String.valueOf(orderDTO.getOrderID())}
                    );

                    try{
                        if (itemCursor.moveToFirst()){
                            do {
                                ItemDTO itemDTO = new ItemDTO();
                                ItemDTO item = new ItemDTO();
                                item.setItemID(itemCursor.getString(itemCursor.getColumnIndexOrThrow("item_id")));
                                item.setQty(itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity")));
                                item.setPrice(itemCursor.getDouble(itemCursor.getColumnIndexOrThrow("price")));
                                itemDTOList.add(item);
                            } while (itemCursor.moveToNext());
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }finally {
                        itemCursor.close();
                    }

                    orderDTO.setItemList(itemDTOList);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            cursor.close();
            sqLiteDatabase.close();
        }

        return orderDTOList;
    }
}
