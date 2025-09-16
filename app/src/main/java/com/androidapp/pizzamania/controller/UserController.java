package com.androidapp.pizzamania.controller;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.androidapp.pizzamania.callBack.OnResultListener;
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

public class UserController {
    private final DatabaseReference dr;

    public UserController() {
        dr = FirebaseDatabase.getInstance().getReference("Users");
    }

    public void createUser(User user, OnResultListener<User> listener){
        dr.child(user.getId()).setValue(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess(user))
                .addOnFailureListener(listener::onFailure);
    }

    public void updateUser(User user, OnResultListener<Void> listener){
        dr.child(user.getId()).setValue(user)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void getAllUsers(OnResultListener<List<User>> listener){
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> usersList = new ArrayList<>();
                for(DataSnapshot child : snapshot.getChildren()){
                    User user = child.getValue(User.class);
                    if(user != null){
                        usersList.add(user);
                    }
                    listener.onSuccess(usersList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }

    public void getUserById(String id, OnResultListener<User> listener){
        dr.child(id).get()
                .addOnSuccessListener(dataSnapshot -> {
                    User user = dataSnapshot.getValue(User.class);
                    listener.onSuccess(user);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteUser(String id, OnResultListener<Void> listener){
        dr.child(id).removeValue()
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(listener::onFailure);
    }

    public void getUserBranchById(String id, OnResultListener<String> listener) {
        dr.child(id).child("branchId").get()
                .addOnSuccessListener(dataSnapshot -> {
                    String branchId = dataSnapshot.getValue(String.class);
                    listener.onSuccess(branchId);
                })
                .addOnFailureListener(listener::onFailure);
    }
}
