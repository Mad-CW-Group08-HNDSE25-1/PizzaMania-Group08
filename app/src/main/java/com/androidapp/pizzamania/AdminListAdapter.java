package com.androidapp.pizzamania;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.AdminViewHolder> {

    private Context context;
    private List<AdminModel> adminList;
    private String currentUserRole = "Admin"; // default

    public AdminListAdapter(Context context, List<AdminModel> adminList) {
        this.context = context;
        this.adminList = adminList;
    }

    public void setCurrentUserRole(String role) {
        this.currentUserRole = role;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_admin, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        AdminModel admin = adminList.get(position);

        holder.tvName.setText(admin.getName());
        holder.tvEmail.setText(admin.getEmail());
        holder.tvRole.setText("Role: " + admin.getRole());

        if (admin.getProfileImageUrl() != null && !admin.getProfileImageUrl().isEmpty()) {
            Picasso.get().load(admin.getProfileImageUrl()).into(holder.imgAdmin);
        } else {
            holder.imgAdmin.setImageResource(R.drawable.ic_person);
        }

        holder.itemView.setOnClickListener(v -> showOptionsDialog(admin));
    }

    private void showOptionsDialog(AdminModel admin) {
        // Restrict actions for non-SuperAdmin
        if (!"SuperAdmin".equals(currentUserRole)) {
            new AlertDialog.Builder(context)
                    .setTitle("Access Denied")
                    .setMessage("Only Super Admin can modify admins.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        String[] options = {"Edit", "Delete"};

        new AlertDialog.Builder(context)
                .setTitle("Select Action for " + admin.getName())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Edit Admin
                        Intent intent = new Intent(context, AddAdminActivity.class);
                        intent.putExtra("adminId", admin.getEmail().replace(".", "_"));
                        intent.putExtra("name", admin.getName());
                        intent.putExtra("email", admin.getEmail());
                        intent.putExtra("role", admin.getRole());
                        context.startActivity(intent);

                    } else if (which == 1) {
                        // Prevent self-delete
                        String currentEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        if (admin.getEmail().equals(currentEmail)) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Not Allowed")
                                    .setMessage("You cannot delete your own Super Admin account.")
                                    .setPositiveButton("OK", null)
                                    .show();
                            return;
                        }

                        // Delete other admins
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Admins");
                        ref.child(admin.getEmail().replace(".", "_")).removeValue();
                    }
                })
                .show();
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAdmin;
        TextView tvName, tvEmail, tvRole;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAdmin = itemView.findViewById(R.id.imgAdmin);
            tvName = itemView.findViewById(R.id.tvAdminName);
            tvEmail = itemView.findViewById(R.id.tvAdminEmail);
            tvRole = itemView.findViewById(R.id.tvAdminRole);
 }
}
}
