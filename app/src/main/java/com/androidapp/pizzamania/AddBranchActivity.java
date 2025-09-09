package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddBranchActivity extends AppCompatActivity {

    private Button btnSetLocation;

    private EditText eLatitude;
    private EditText eLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_branch);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSetLocation = findViewById(R.id.btnSetLocation);
        eLatitude = findViewById(R.id.etLatitude);
        eLongitude = findViewById(R.id.etLongitude);

        btnSetLocation.setOnClickListener(v -> {
            Intent intent = new Intent(AddBranchActivity.this, MapsActivity.class);
            startActivityForResult(intent, 200);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == 200) && (resultCode == RESULT_OK) &&( data != null)){
            eLongitude.setText(String.valueOf(data.getDoubleExtra("long", 0)));
            eLatitude.setText(String.valueOf(data.getDoubleExtra("lat", 0)));
        }
    }
}