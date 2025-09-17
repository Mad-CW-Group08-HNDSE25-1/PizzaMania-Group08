package com.androidapp.pizzamania;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.model.Order;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {

    private RecyclerView ordersRv;
    private List<Order> orderList;
    private OrderMgmtAdapter adapter;
    private String branchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_management);

        ordersRv = findViewById(R.id.ordersRv);
        ordersRv.setLayoutManager(new LinearLayoutManager(this));
        orderList = new ArrayList<>();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getUserBranchById(uid, new OnResultListener<String>() {
            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    branchId = result;
                    loadOrders(branchId);
                } else {
                    Log.e("OrderManagement", "User branch is null");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("OrderManagement", "Failed to get branch: " + e.getMessage());
            }
        });
    }

    private void getUserBranchById(String uid, OnResultListener<String> listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(uid);

        Log.d("OrderManagement", "Fetching branch for UID: " + uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.d("OrderManagement", "User snapshot: " + snapshot.getValue());

                    if (snapshot.hasChild("branch")) {
                        String branch = snapshot.child("branch").getValue(String.class);
                        Log.d("OrderManagement", "Found branch: " + branch);
                        listener.onSuccess(branch);
                    } else {
                        Log.e("OrderManagement", "Branch field missing in user data");
                        listener.onSuccess(null);
                    }
                } else {
                    Log.e("OrderManagement", "User node does not exist for UID: " + uid);
                    listener.onSuccess(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onFailure(error.toException());
            }
        });
    }


    private void loadOrders(String branchId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("orders");

        Query query = ref.orderByChild("branchID").equalTo(branchId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                orderList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Order order = ds.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }
                adapter = new OrderMgmtAdapter(OrderManagementActivity.this, orderList, branchId);
                ordersRv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("OrderManagement", "Firebase error: " + error.getMessage());
            }
        });
    }
}
