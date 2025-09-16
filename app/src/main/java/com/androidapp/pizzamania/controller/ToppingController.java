package com.androidapp.pizzamania.controller;

import androidx.annotation.NonNull;

import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.model.Topping;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ToppingController {
    private final DatabaseReference dr;

    public ToppingController() {
        dr = FirebaseDatabase.getInstance().getReference("toppings");
    }

    public void createTopping(Topping topping, OnResultListener<Topping> listener) {
        String id = dr.push().getKey();
        topping.setId(id);
        dr.child(id).setValue(topping)
                .addOnSuccessListener(aVoid -> listener.onSuccess(topping))
                .addOnFailureListener(listener::onFailure);
    }

    public void updateTopping(Topping topping, OnResultListener<Void> listener) {
        dr.child(topping.getId()).setValue(topping)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllToppings(OnResultListener<List<Topping>> listener) {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Topping> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Topping t = child.getValue(Topping.class);
                    if (t != null) list.add(t);
                }
                listener.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void getToppingById(String id, OnResultListener<Topping> listener) {
        dr.child(id).get()
                .addOnSuccessListener(snap -> listener.onSuccess(snap.getValue(Topping.class)))
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteTopping(String id, OnResultListener<Void> listener) {
        dr.child(id).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }
}
