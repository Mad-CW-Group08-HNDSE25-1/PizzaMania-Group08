package com.androidapp.pizzamania;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewBranches extends AppCompatActivity {
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private BranchAdapter branchAdapter;
    private List<BranchesDTO> branchesDTOList = new ArrayList<>();

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

        recyclerView = findViewById(R.id.recyclerViewBranches);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference("branches");

        branchAdapter = new BranchAdapter(branchesDTOList, new BranchAdapter.OnBranchActionListener() {
            @Override
            public void onViewLocation(BranchesDTO branchesDTO) {
                double lat = branchesDTO.getLatitude();
                double lng = branchesDTO.getLongitude();
                String locLabel = branchesDTO.getBranchName();

                String uriString = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + Uri.encode(locLabel) + ")";
                Uri uri = Uri.parse(uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }

            @Override
            public void onDeleteBranch(String branchKey) {
                databaseReference.child(branchKey).removeValue()
                        .addOnSuccessListener(successVoid -> {
                            Toast.makeText(ViewBranches.this, "BranchDeleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(exception -> {
                            Toast.makeText(ViewBranches.this, "Failed", Toast.LENGTH_SHORT).show();
                        });
            }
        }, this, databaseReference);

        recyclerView.setAdapter(branchAdapter);
        loadBranches();

    }

    private void loadBranches() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                branchesDTOList.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    BranchesDTO branchesDTO = snap.getValue(BranchesDTO.class);
                    if (branchesDTO != null) {
                        branchesDTO.setKey(snap.getKey());
                        branchesDTOList.add(branchesDTO);
                    }
                }

                branchAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ViewBranches.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}