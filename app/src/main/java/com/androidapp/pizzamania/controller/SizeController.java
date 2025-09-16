package com.androidapp.pizzamania.controller;

import androidx.annotation.NonNull;

import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.model.Size;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SizeController {
    private final DatabaseReference dr;

    public SizeController() {
        dr = FirebaseDatabase.getInstance().getReference("sizes");
    }

    public void createSize(Size size, OnResultListener<Size> listener) {
        String id = dr.push().getKey();
        size.setId(id);
        dr.child(id).setValue(size)
                .addOnSuccessListener(aVoid -> listener.onSuccess(size))
                .addOnFailureListener(listener::onFailure);
    }

    public void updateSize(Size size, OnResultListener<Void> listener) {
        dr.child(size.getId()).setValue(size)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllSizes(OnResultListener<List<Size>> listener) {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Size> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Size s = child.getValue(Size.class);
                    if (s != null) list.add(s);
                }
                listener.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void getSizeById(String id, OnResultListener<Size> listener) {
        dr.child(id).get()
                .addOnSuccessListener(snap -> listener.onSuccess(snap.getValue(Size.class)))
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteSize(String id, OnResultListener<Void> listener) {
        dr.child(id).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }
}
