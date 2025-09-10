package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class AddBranchActivity extends AppCompatActivity {

    private Button btnSetLocation;
    private EditText eLatitude;
    private EditText eLongitude;
    private EditText eBranchName;
    private  EditText eBranchAddress;
    private FirebaseDatabase firebaseDatabase;

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
        eBranchName = findViewById(R.id.branchName);
        eBranchAddress = findViewById(R.id.branchAddress);

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

    public void addBranches(View v){

        HashMap<String, Object> branchHashMap = new HashMap<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference branchReference = firebaseDatabase.getReference("branches");
        String key = branchReference.push().getKey();

        String branchName = eBranchName.getText().toString();
        String branchAddress = eBranchAddress.getText().toString();
        double latitude = Double.parseDouble(eLatitude.getText().toString());
        double longitude = Double.parseDouble(eLongitude.getText().toString());

        if (branchName.trim().isEmpty()){
            Toast.makeText(this, "Please Enter the Branch Name", Toast.LENGTH_SHORT).show();
        } else if (branchAddress.trim().isEmpty()) {
            Toast.makeText(this, "Please Enter the Branch Address", Toast.LENGTH_SHORT).show();
        } else if (String.valueOf(latitude).trim().isEmpty()) {
            Toast.makeText(this, "Please Select a Location", Toast.LENGTH_SHORT).show();
        }else if (String.valueOf(longitude).trim().isEmpty()) {
            Toast.makeText(this, "Please Select a Location", Toast.LENGTH_SHORT).show();
        }else {

            branchHashMap.put("key", key);
            branchHashMap.put("branchName", branchName);
            branchHashMap.put("branchAddress", branchAddress);
            branchHashMap.put("latitude", latitude);
            branchHashMap.put("longitude", longitude);

            assert key != null;
            branchReference.child(key).setValue(branchHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    eBranchName.getText().clear();
                    eBranchAddress.getText().clear();
                    eLatitude.getText().clear();
                    eLongitude.getText().clear();

                    Toast.makeText(AddBranchActivity.this, "Branch Added Successfully", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void viewBranchBtnOnclick(View view){
        Intent viewBranchIntent = new Intent(AddBranchActivity.this, ViewBranches.class);
        startActivity(viewBranchIntent);
    }
}