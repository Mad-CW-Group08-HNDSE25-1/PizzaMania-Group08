package com.androidapp.pizzamania.controller;

import com.androidapp.pizzamania.model.MenuItem;
import com.androidapp.pizzamania.model.Order;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class OrderController {
    private Order order = new Order();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderController() {
    }

    public Task<Void> createOrder(String total, String branch){
        order = new Order(total, branch, "paid");
        CollectionReference orderCollection = db.collection("orders");
        return orderCollection.add(order)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });

    }
}
