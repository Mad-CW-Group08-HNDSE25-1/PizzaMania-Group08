package com.androidapp.pizzamania.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.model.Size;
import com.androidapp.pizzamania.model.Topping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OptionRvAdapter<T> extends RecyclerView.Adapter<OptionRvAdapter<T>.OptionViewHolder> {

    private List<T> options;
    private Set<Integer> selectedPositions = new HashSet<>();
    private OnOptionSelectedListener<T> listener;

    public interface OnOptionSelectedListener<T> {
        void onOptionChanged(Set<T> selectedOptions);
    }

    public OptionRvAdapter(List<T> options, OnOptionSelectedListener<T> listener) {
        this.options = options;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_option, parent, false);
        return new OptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionViewHolder holder, int position) {
        T option = options.get(position);
        String name;
        int price;

        if (option instanceof Size) {
            name = ((Size) option).getName();
            price = Integer.parseInt(((Size) option).getPrice());
        } else if (option instanceof Topping) {
            name = ((Topping) option).getName();
            price = Integer.parseInt(((Topping) option).getPrice());
        } else {
            name = "Unknown";
            price = 0;
        }

        holder.optionTxt.setText(name);
        holder.optionPrice.setText("Rs. " + price);

        holder.optionCheck.setOnCheckedChangeListener(null);
        holder.optionCheck.setChecked(selectedPositions.contains(position));

        holder.optionCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPositions.add(position);
            } else {
                selectedPositions.remove(position);
            }
            if (listener != null) {
                Set<T> selectedOptions = new HashSet<>();
                for (int pos : selectedPositions) {
                    selectedOptions.add(options.get(pos));
                }
                listener.onOptionChanged(selectedOptions);
            }
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    class OptionViewHolder extends RecyclerView.ViewHolder {
        CheckBox optionCheck;
        TextView optionTxt, optionPrice;

        public OptionViewHolder(@NonNull View itemView) {
            super(itemView);
            optionCheck = itemView.findViewById(R.id.optionCheck);
            optionTxt = itemView.findViewById(R.id.optionTxt);
            optionPrice = itemView.findViewById(R.id.optionPrice);
        }
    }
}

