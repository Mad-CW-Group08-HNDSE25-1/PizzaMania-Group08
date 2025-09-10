package com.androidapp.pizzamania;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.BranchViewHolder> {
    public interface OnBranchActionListener {
        void onViewLocation(BranchesDTO branch);
        void onDeleteBranch(String branchKey);
    }

    private final List<BranchesDTO> branchesList;
    private  Context context;
    private  DatabaseReference databaseReference;

    public BranchAdapter(List<BranchesDTO> branchesList, OnBranchActionListener listener, Context context, DatabaseReference databaseReference) {
        this.branchesList = branchesList;
        this.context = context;
        this.databaseReference = databaseReference;
    }


    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_branch, parent, false);
        return new BranchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchAdapter.BranchViewHolder holder, int position) {

        BranchesDTO branchesDTO = branchesList.get(position);
        holder.txtBranchName.setText(branchesDTO.getBranchName());
        holder.txtBranchAddress.setText(branchesDTO.getBranchAddress());

        holder.btnViewLocation.setOnClickListener(v -> {
            double lat = branchesDTO.getLatitude();
            double lng = branchesDTO.getLongitude();

            String locLabel = branchesDTO.getBranchName();

            String uriString = "geo:" + lat + "," + lng + "?q=" + lat + "," + lng + "(" + Uri.encode(locLabel) + ")";
            Uri uri = Uri.parse(uriString);

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
            mapIntent.setPackage("com.google.android.apps.maps");

            try {
                context.startActivity(mapIntent);
            } catch (Exception e) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            String key = branchesDTO.getKey();
            if ((key != null) && (!key.isEmpty())) {

                new AlertDialog.Builder(context).setTitle("Delete Branch").setMessage("Do you want to Delete this Branch")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            databaseReference.child(key).removeValue();
                            Toast.makeText(context, "Branch Deleted", Toast.LENGTH_SHORT).show();
                        }).setNegativeButton("No", (dialog, which) -> {
                            Toast.makeText(context, "canceled", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }).show();

            }
        });

        holder.updateBtn.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddBranchActivity.class);
            intent.putExtra("branchKey", branchesDTO.getKey());
            intent.putExtra("branchName", branchesDTO.getBranchName());
            intent.putExtra("branchAddress", branchesDTO.getBranchAddress());
            intent.putExtra("lat", branchesDTO.getLatitude());
            intent.putExtra("lng", branchesDTO.getLongitude());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return branchesList != null ? branchesList.size() :  0;
    }

    static class BranchViewHolder extends RecyclerView.ViewHolder { ;
        TextView txtBranchName;
        TextView txtBranchAddress;
        Button btnViewLocation;

        Button updateBtn;
        Button btnDelete;

        BranchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtBranchName = itemView.findViewById(R.id.txtBranchName);
            txtBranchAddress = itemView.findViewById(R.id.txtBranchAddress);
            btnViewLocation = itemView.findViewById(R.id.btnViewLocation);
            updateBtn = itemView.findViewById(R.id.btnUpdateBranch);
            btnDelete = itemView.findViewById(R.id.btnDeleteBranch);
        }
    }
}
