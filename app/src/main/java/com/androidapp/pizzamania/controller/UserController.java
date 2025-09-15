package com.androidapp.pizzamania.controller;

import android.util.Log;
import android.widget.Toast;

import com.androidapp.pizzamania.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserController {
    private User user = new User();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserController() {
    }

    public UserController(FirebaseFirestore db) {
        this.db = db;
    }

    public Task<Void> createUser(String id, User user){
        DocumentReference userDoc = db.collection("users").document(id);
        return userDoc.set(user)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<Void> updateUser(String id, User upUser){
        DocumentReference userDoc = db.collection("users").document(id);

        Map<String, Object> updatedUser = new HashMap<>();
        updatedUser.put("name", upUser.getName());
        updatedUser.put("email", upUser.getEmail());
        updatedUser.put("phone", upUser.getPhone());
        updatedUser.put("branch", upUser.getBranch());
        updatedUser.put("imageURL", upUser.getImageURL());

        return userDoc.update(updatedUser)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<Void> updateUserEmail(String id, String email){
        DocumentReference userDoc = db.collection("users").document(id);
        return userDoc.update("email", email)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<List<User>> readAllUsers(){
        CollectionReference userCollection = db.collection("users");
        return userCollection.get()
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    QuerySnapshot querySnapshot = task.getResult();
                    List<User> users = querySnapshot.toObjects(User.class);
                    return Tasks.forResult(users);
                });
    }

    public Task<User> readUserById(String id){
        DocumentReference userDoc = db.collection("users").document(id);
        return userDoc.get()
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    DocumentSnapshot doc = task.getResult();
                    user = null;
                    if(doc.exists()){
                        user = doc.toObject(User.class);
                        user.setId(doc.getId());
                    }
                    return Tasks.forResult(user);
                });
    }
    public Task<Void> deleteUser(String id){
        DocumentReference userDoc = db.collection("users").document(id);
        return userDoc.delete()
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<String> getUserBranchById(String id) {
        DocumentReference userDoc = db.collection("users").document(id);

        return userDoc.get()
                .continueWith(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        User user = doc.toObject(User.class);
                        if (user != null) {
                            user.setId(doc.getId());
                            String branch = user.getBranch();
                            return branch;
                        }
                    }
                    return null;
                });
    }


}
