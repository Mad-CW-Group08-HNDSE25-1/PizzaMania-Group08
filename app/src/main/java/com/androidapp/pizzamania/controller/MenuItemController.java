package com.androidapp.pizzamania.controller;

import com.androidapp.pizzamania.model.MenuItem;
import com.androidapp.pizzamania.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
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
    private MenuItem menuItem = new MenuItem();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public MenuItemController() {
    }

    public Task<Void> createMenuItem(String name, String description, String price, String branch){
        //menuItem = new MenuItem(name, description, price, branch);
        CollectionReference menuItemCollection = db.collection("menuItems");
        return menuItemCollection.add(menuItem)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });

    }

    public Task<Void> updateMenuItem(String id, String name, String description, String price, String branch) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("description", description);
        updates.put("price", price);
        updates.put("branch", branch);

        return db.collection("menuItems")
                .document(id)
                .update(updates)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }


    public Task<List<MenuItem>> readAllMenuItems(){
        CollectionReference menuItemCollection = db.collection("menuItems");
        return menuItemCollection.get()
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    QuerySnapshot querySnapshot = task.getResult();
                    List<MenuItem> menuItems = querySnapshot.toObjects(MenuItem.class);
                    return Tasks.forResult(menuItems);
                });
    }

    public Task<List<MenuItem>> readAllMenuItemsByBranch(String branch) {
        CollectionReference menuItemCollection = db.collection("menuItems");

        return menuItemCollection.whereEqualTo("branch", branch).get()
                .continueWith(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }

                    QuerySnapshot querySnapshot = task.getResult();
                    List<MenuItem> menuItems = new ArrayList<>();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        MenuItem item = doc.toObject(MenuItem.class);
                        if (item != null) {
                            item.setId(doc.getId());
                            menuItems.add(item);
                        }
                    }

                    return menuItems;
                });
    }

    public Task<MenuItem> readMenuItemById(String id){
        DocumentReference menuItemDoc = db.collection("menuItems").document(id);
        return menuItemDoc.get()
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    DocumentSnapshot doc = task.getResult();
                    menuItem = null;
                    if(doc.exists()){
                        menuItem = doc.toObject(MenuItem.class);
                        menuItem.setId(doc.getId());
                    }
                    return Tasks.forResult(menuItem);
                });
    }

    public Task<Void> deleteMenuItem(String id){
        DocumentReference menuItemDoc = db.collection("menuItems").document(id);
        return menuItemDoc.delete()
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }
}
