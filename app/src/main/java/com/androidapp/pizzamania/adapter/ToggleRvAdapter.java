package com.androidapp.pizzamania.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToggleRvAdapter<T extends ToggleRvAdapter.ToggleItem>
        extends RecyclerView.Adapter<ToggleRvAdapter.ViewHolder> {

    private List<T> data;
    private Map<String, Boolean> selectionMap = new HashMap<>();

    public ToggleRvAdapter(List<T> data) {
        this.data = data;
    }

    public ToggleRvAdapter(List<T> data, Map<String, Boolean> preSelected) {
        this.data = data;
        if (preSelected != null) {
            this.selectionMap = new HashMap<>(preSelected);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.it, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T item = data.get(position);

        holder.toggleBtn.setText(item.getName());
        holder.toggleBtn.setTextOff(item.getName() + ": Not Selected");
        holder.toggleBtn.setTextOn(item.getName() + ": Selected");

        // Restore selection if exists
        boolean isSelected = selectionMap.getOrDefault(item.getId(), false);
        holder.toggleBtn.setChecked(isSelected);

        holder.toggleBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            selectionMap.put(item.getId(), isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Map<String, Boolean> getSelectionMap() {
        return selectionMap;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ToggleButton toggleBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            toggleBtn = itemView.findViewById(R.id.toggleBtn);
        }
    }

    // Interface to allow generic model
    public interface ToggleItem {
        String getId();
        String getName();
    }
}
