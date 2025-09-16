package com.androidapp.pizzamania.controller;

import com.androidapp.pizzamania.model.User;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AuthController {
    private final FirebaseAuth auth;

    public AuthController() {
        auth = FirebaseAuth.getInstance();
    }

    public Task<Void> createAuth(String email, String pass){
        return auth.createUserWithEmailAndPassword(email, pass)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<String> signup(String name, String phone, String email, String pass){
        return auth.createUserWithEmailAndPassword(email, pass)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
                    return Tasks.forResult(uid);
                });
    }

    public FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

    public String getCurrentUserId(){
        assert auth.getCurrentUser() != null;
        return auth.getCurrentUser().getUid();
    }

    public Task<Void> login(String email, String pass){
        return auth.signInWithEmailAndPassword(email, pass)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<Void> logout(){
        auth.signOut();
        return Tasks.forResult(null);
    }

    public Task<Void> resetPass(String email){
        return auth.sendPasswordResetEmail(email)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<Void> deleteAuth(){
        FirebaseUser user = auth.getCurrentUser();
        return user.delete()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }
}
