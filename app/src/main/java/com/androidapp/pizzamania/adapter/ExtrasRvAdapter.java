package com.androidapp.pizzamania.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.R;

import java.util.List;

public class ExtrasRvAdapter<T> extends RecyclerView.Adapter<ExtrasRvAdapter.ViewHolder> {
    private List<T> data;
    private OnDeleteClickListener<T> deleteListener;
    private NameExtractor<T> nameExtractor;

    public interface OnDeleteClickListener<T> {
        void onDelete(T item, int position);
    }

    public interface NameExtractor<T> {
        String getName(T item);
    }

    public ExtrasRvAdapter(List<T> data, NameExtractor<T> nameExtractor, OnDeleteClickListener<T> deleteListener) {
        this.data = data;
        this.deleteListener = deleteListener;
        this.nameExtractor = nameExtractor;
    }

    @NonNull
    @Override
    public ExtrasRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item_extras, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtrasRvAdapter.ViewHolder holder, int position) {
        T item = data.get(position);
        holder.nameTxt.setText(nameExtractor.getName(item));

        holder.deleteBtn.setOnClickListener(view -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTxt;
        Button deleteBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTxt);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }

    }
}
