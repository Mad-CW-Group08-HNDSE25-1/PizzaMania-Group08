package com.androidapp.pizzamania;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    // Converts JSON string like: [{"itemId":"id1","quantity":2,"price":100},{"itemId":"id2","quantity":1,"price":50}]
    // into List<OrderItem.Item>
    public static List<OrderItem.Item> parseItemsString(String itemsStr) {
        List<OrderItem.Item> itemList = new ArrayList<>();
        if (itemsStr == null || itemsStr.isEmpty()) return itemList;

        try {
            JSONArray arr = new JSONArray(itemsStr);
            for (int i = 0; i < arr.length(); i++) {
                String itemId = arr.getJSONObject(i).getString("itemId");
                int quantity = arr.getJSONObject(i).getInt("quantity");
                double price = arr.getJSONObject(i).getDouble("price");
                itemList.add(new OrderItem.Item(itemId, quantity, price));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return itemList;
    }
}
