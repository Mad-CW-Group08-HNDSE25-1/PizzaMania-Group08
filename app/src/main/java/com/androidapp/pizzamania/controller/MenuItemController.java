package com.androidapp.pizzamania.controller;

import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import com.androidapp.pizzamania.model.MenuItem;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class MenuItemController {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private MenuItem menuItem = new MenuItem();

//    public void createItem(MenuItem menuItem){
//        db.collection("menu_item").add(menuItem)
//                .addOnSuccessListener(doc -> {
//                    Toast.makeText(this, "Success", LENGTH_SHORT)
//                })
//                .addOnFailureListener(e -> {});
//
//
//    }

    public void getAllCategories() {
        List<String> catergories = new ArrayList<>();
        db.collection("menu_item")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String category = doc.getString("category");
                        catergories.add(category);

                    }
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error getting users", e));
    }

}
