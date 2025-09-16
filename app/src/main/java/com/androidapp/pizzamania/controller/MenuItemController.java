package com.androidapp.pizzamania.controller;

import androidx.annotation.NonNull;

import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.model.MenuItem;
import com.androidapp.pizzamania.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MenuItemController {
    private final DatabaseReference dr;

    public MenuItemController() {
        dr = FirebaseDatabase.getInstance().getReference("menuItems");
    }

    public void createItem(MenuItem menuItem, OnResultListener<MenuItem> listener){
        String iid = dr.push().getKey();
        menuItem.setId(iid);
        dr.child(iid).setValue(menuItem)
                .addOnSuccessListener(aVoid -> listener.onSuccess(menuItem))
                .addOnFailureListener(listener::onFailure);
    }

    public void updateItem(MenuItem menuItem, OnResultListener<Void> listener){
        dr.child(menuItem.getId()).setValue(menuItem)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllItems(OnResultListener<List<MenuItem>> listener){
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MenuItem> itemList = new ArrayList<>();
                for(DataSnapshot child : snapshot.getChildren()){
                    MenuItem menuItem = child.getValue(MenuItem.class);
                    if(menuItem != null){
                        itemList.add(menuItem);
                    }
                }
                listener.onSuccess(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void getItemById(String id, OnResultListener<MenuItem> listener){
        dr.child(id).get()
                .addOnSuccessListener(dataSnapshot -> {
                    MenuItem menuItem = dataSnapshot.getValue(MenuItem.class);
                    listener.onSuccess(menuItem);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteItem(String id, OnResultListener<Void> listener){
        dr.child(id).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void addSizeToItem(String itemId, String sizeId, OnResultListener<Void> listener) {
        dr.child(itemId).child("sizes").child(sizeId).setValue(true)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void removeSizeFromItem(String itemId, String sizeId, OnResultListener<Void> listener) {
        dr.child(itemId).child("sizes").child(sizeId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void addToppingToItem(String itemId, String toppingId, OnResultListener<Void> listener) {
        dr.child(itemId).child("toppings").child(toppingId).setValue(true)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void removeToppingFromItem(String itemId, String toppingId, OnResultListener<Void> listener) {
        dr.child(itemId).child("toppings").child(toppingId).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllItemsByCategoryId(String categoryId, OnResultListener<List<MenuItem>> listener) {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<MenuItem> itemList = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    MenuItem menuItem = child.getValue(MenuItem.class);
                    if (menuItem != null && categoryId.equals(menuItem.getCategoryId())) {
                        itemList.add(menuItem);
                    }
                }
                listener.onSuccess(itemList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void getSizesByItemId(String itemId, OnResultListener<List<String>> listener) {
        dr.child(itemId).child("sizes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> sizeIds = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    sizeIds.add(child.getKey()); // Each sizeId stored as key
                }
                listener.onSuccess(sizeIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void getToppingsByItemId(String itemId, OnResultListener<List<String>> listener) {
        dr.child(itemId).child("toppings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> toppingIds = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    toppingIds.add(child.getKey()); // Each toppingId stored as key
                }
                listener.onSuccess(toppingIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

}
