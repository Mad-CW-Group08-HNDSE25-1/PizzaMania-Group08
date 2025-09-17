package com.androidapp.pizzamania;

import android.app.AlertDialog;
import android.content.Context;
import android.view.*;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.AdminViewHolder> {

    private Context context;
    private List<AdminModel> adminList;
    private String currentUserRole;

    public AdminListAdapter(Context context, List<AdminModel> adminList, String currentUserRole) {
        this.context = context;
        this.adminList = adminList;
        this.currentUserRole = currentUserRole;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        AdminModel admin = adminList.get(position);
        holder.tvAdminName.setText(admin.getName());
        holder.tvAdminEmail.setText(admin.getEmail());
        holder.tvAdminRole.setText("Role: " + admin.getRole());

        if (admin.getProfileUrl() != null && !admin.getProfileUrl().isEmpty()) {
            Glide.with(context).load(admin.getProfileUrl()).into(holder.imgAdmin);
        } else {
            holder.imgAdmin.setImageResource(R.drawable.ic_person);
        }

        // Remove button only for Super Admin (cannot remove other Super Admins)
        if ("super_admin".equals(currentUserRole) && !"super_admin".equals(admin.getRole())) {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(v -> removeAdmin(admin.getId(), position));
        } else {
            holder.btnRemove.setVisibility(View.GONE);
        }
    }

    private void removeAdmin(String userId, int position) {
        new AlertDialog.Builder(context)
                .setTitle("Remove Admin")
                .setMessage("Are you sure you want to remove this admin?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(userId)
                            .removeValue()
                            .addOnSuccessListener(aVoid -> {
                                adminList.remove(position);
                                notifyItemRemoved(position);
                                Toast.makeText(context, "Admin removed", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> Toast.makeText(context, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAdmin;
        TextView tvAdminName, tvAdminEmail, tvAdminRole;
        ImageButton btnRemove;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAdmin = itemView.findViewById(R.id.imgAdmin);
            tvAdminName = itemView.findViewById(R.id.tvAdminName);
            tvAdminEmail = itemView.findViewById(R.id.tvAdminEmail);
            tvAdminRole = itemView.findViewById(R.id.tvAdminRole);
            btnRemove = itemView.findViewById(R.id.btnRemoveAdmin);
        }
    }
}
