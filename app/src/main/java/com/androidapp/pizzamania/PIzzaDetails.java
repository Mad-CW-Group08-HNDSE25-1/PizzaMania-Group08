package com.androidapp.pizzamania;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PizzaDetails extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        String pizzaName = getIntent().getStringExtra("pizzaName");
        tv.setText("Details for " + pizzaName + " (Mock Screen)");
        tv.setTextSize(24f);
        setContentView(tv);
    }
}
