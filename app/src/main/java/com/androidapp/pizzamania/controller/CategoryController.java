package com.androidapp.pizzamania.controller;

import android.icu.util.ULocale;

import androidx.annotation.NonNull;

import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.model.Category;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryController {
    private final DatabaseReference dr;

    public CategoryController() {
        dr = FirebaseDatabase.getInstance().getReference("categories");
    }

    public void createCategory(Category category, OnResultListener<Category> listener) {
        String id = dr.push().getKey();
        category.setId(id);
        dr.child(id).setValue(category)
                .addOnSuccessListener(aVoid -> listener.onSuccess(category))
                .addOnFailureListener(listener::onFailure);
    }

    public void updateCategory(Category category, OnResultListener<Void> listener) {
        dr.child(category.getId()).setValue(category)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllCategories(OnResultListener<List<Category>> listener) {
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Category> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Category c = child.getValue(Category.class);
                    if (c != null) list.add(c);
                }
                listener.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void getCategoryById(String id, OnResultListener<Category> listener) {
        dr.child(id).get()
                .addOnSuccessListener(snap -> listener.onSuccess(snap.getValue(Category.class)))
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteCategory(String id, OnResultListener<Void> listener) {
        dr.child(id).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }
}
