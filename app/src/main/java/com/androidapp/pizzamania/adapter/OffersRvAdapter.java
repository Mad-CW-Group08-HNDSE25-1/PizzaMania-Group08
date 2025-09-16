package com.androidapp.pizzamania.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.model.MenuItem;

import java.util.List;

public class OffersRvAdapter extends RecyclerView.Adapter<OffersRvAdapter.OfferViewHolder> {

    private Context context;
    private List<MenuItem> offerList;

    public OffersRvAdapter(Context context, List<MenuItem> offerList) {
        this.context = context;
        this.offerList = offerList;
    }

    @NonNull
    @Override
    public OfferViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_offer, parent, false);
        return new OfferViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OfferViewHolder holder, int position) {
        MenuItem offer = offerList.get(position);
        holder.offerTitle.setText(offer.getName());
        holder.offerPrice.setText(offer.getPrice());
        holder.offerDescription.setText(offer.getDescription());
    }

    @Override
    public int getItemCount() {
        return offerList.size();
    }

    public static class OfferViewHolder extends RecyclerView.ViewHolder {
        ImageView offerImage;
        TextView offerTitle, offerDescription, offerPrice;

        public OfferViewHolder(@NonNull View itemView) {
            super(itemView);
            offerImage = itemView.findViewById(R.id.offerImage);
            offerTitle = itemView.findViewById(R.id.offerTitle);
            offerTitle = itemView.findViewById(R.id.offerPrice);
            offerDescription = itemView.findViewById(R.id.offerDescription);
        }
    }
}
