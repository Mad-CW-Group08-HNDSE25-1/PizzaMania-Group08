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
    private final UserController userController = new UserController();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    public AuthController() {
    }

    public AuthController(FirebaseAuth auth) {
        this.auth = auth;
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

    public Task<Void> createAuth(String email, String pass){
        return auth.createUserWithEmailAndPassword(email, pass)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null); // success
                });
    }
    public Task<Void> registerCustomer(String name, String phone, String email, String pass){
        return auth.createUserWithEmailAndPassword(email, pass)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
                    User user = new User(name, email, phone, "customer");

                    return userController.createUser(uid, user);
                });
    }

    public Task<Void> registerUser(String name, String phone, String role, String email, String pass){
        return auth.createUserWithEmailAndPassword(email, pass)
                .continueWithTask(task -> {
                    if(!task.isSuccessful()){
                        throw Objects.requireNonNull(task.getException());
                    }
                    String uid = Objects.requireNonNull(task.getResult().getUser()).getUid();
                    User user = new User(name, email, phone, role);

                    return userController.createUser(uid, user);
                });
    }

    public Task<Void> updateAuthEmail(String email){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return Tasks.forException(new Exception("No user is logged in"));
        }
        user.updateEmail(email);
        return Tasks.forResult(null);
    }

    public Task<Void> updateAuthPass(String pass){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return Tasks.forException(new Exception("No user is logged in"));
        }
        user.updatePassword(pass);
        return Tasks.forResult(null);
    }

    public Task<Void> resetPassword(String email) {
        return auth.sendPasswordResetEmail(email)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null);
                });
    }

    public Task<Void> checkCredentials(String email, String pass){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return Tasks.forException(new Exception("No user is logged in"));
        }
        AuthCredential authCredential = EmailAuthProvider.getCredential(email, pass);
        return user.reauthenticate(authCredential)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forResult(null); // success
                });
    }

    public Task<Void> deleteAuth(){
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            return Tasks.forException(new Exception("No user is logged in"));
        }
        String uid = user.getUid();
        return user.delete()
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw Objects.requireNonNull(task.getException());
                    }
                    return Tasks.forException(null);
                });
    }

    public String getAuthId(){
        return auth.getUid().toString();
    }

}
