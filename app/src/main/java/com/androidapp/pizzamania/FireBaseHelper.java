package com.androidapp.pizzamania;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FireBaseHelper {

    private final DatabaseReference dbReference;

    public FireBaseHelper(String nodeName){

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference(nodeName);

    }

    public void writeDate(String val){
        dbReference.setValue(val);
    }

    public DatabaseReference getDbReference() {
        return dbReference;
    }
}
