package com.androidapp.pizzamania;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewBranches extends AppCompatActivity {

    private TextView txtBranchName;
    private TextView txtBranchAddress;
    private DatabaseReference databaseReference;
    private final List<BranchesDTO> branchesDTOList = new ArrayList<BranchesDTO>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_branches);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();

        txtBranchAddress = findViewById(R.id.txtBranchAddress);
        txtBranchName = findViewById(R.id.txtBranchName);

        databaseReference.child("branches").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branchesDTOList.clear();
                Iterable<DataSnapshot> children = snapshot.getChildren();
                for (DataSnapshot child : children) {
                    BranchesDTO branches = child.getValue(BranchesDTO.class);
                    if (branches != null){
                        branchesDTOList.add(branches);
                    }
                }

                if (!branchesDTOList.isEmpty()){
                    BranchesDTO firstBranch = branchesDTOList.get(0);
                    txtBranchName.setText(firstBranch.getBranchName());
                    txtBranchAddress.setText(firstBranch.getBranchAddress());

                    String branchKey = snapshot.getChildren().iterator().next().getKey();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



}