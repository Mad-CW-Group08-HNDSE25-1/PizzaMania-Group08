package com.androidapp.pizzamania;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

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

        holder.tvName.setText(admin.getName());
        holder.tvEmail.setText(admin.getEmail());
        holder.tvPhone.setText(admin.getPhone());
        holder.tvRole.setText(admin.getRole());
        holder.tvBranch.setText(admin.getBranch());

        if (admin.getProfileUrl() != null && !admin.getProfileUrl().isEmpty()) {
            Glide.with(context)
                    .load(admin.getProfileUrl())
                    .placeholder(R.drawable.ic_person) // default placeholder
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_person);
        }
    }

    @Override
    public int getItemCount() {
        return adminList.size();
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfile;
        TextView tvName, tvEmail, tvPhone, tvRole, tvBranch;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvRole = itemView.findViewById(R.id.tvRole);
            tvBranch = itemView.findViewById(R.id.tvBranch);
 }
}
}
