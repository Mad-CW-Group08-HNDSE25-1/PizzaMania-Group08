package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class SplashScreen extends AppCompatActivity {

    private FireBaseHelper fireBaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fireBaseHelper = new FireBaseHelper("message");
        fireBaseHelper.writeDate("Hello");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                   startActivity(new Intent(SplashScreen.this, OrderHistoryActivity.class));
                   finish();
            }
        }, 3000);

    }
}
