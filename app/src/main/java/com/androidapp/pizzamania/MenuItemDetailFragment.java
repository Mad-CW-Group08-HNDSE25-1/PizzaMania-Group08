package com.androidapp.pizzamania;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.model.MenuItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuItemDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuItemDetailFragment extends Fragment {

    // UI Components
    private ImageView itemImage;
    private TextView nameTxt, priceTxt, descriptionTxt, totalTxt, qtyTxt;
    private String priceStr, totalStr, itemId;
    private Button minusBtn, plusBtn, addToCartBtn;
    private RecyclerView sizesRv, toppingsRv;
    private MenuItemController menuItemController = new MenuItemController();

    private int qty = 1, price, total;

    public MenuItemDetailFragment() {
        // Required empty public constructor
    }

    public static MenuItemDetailFragment newInstance(String itemId, int qty) {
        MenuItemDetailFragment fragment = new MenuItemDetailFragment();
        Bundle args = new Bundle();
        args.putString("itemId", itemId);
        args.putInt("itemQty", qty);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_item_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind views
        itemImage = view.findViewById(R.id.itemImage);
        nameTxt = view.findViewById(R.id.nameTxt);
        priceTxt = view.findViewById(R.id.priceTxt);
        descriptionTxt = view.findViewById(R.id.descriptionTxt);
        totalTxt = view.findViewById(R.id.tvTotal); // fixed: should match XML id
        qtyTxt = view.findViewById(R.id.qtyTxt);

        minusBtn = view.findViewById(R.id.minusBtn);
        plusBtn = view.findViewById(R.id.plusBtn);
        addToCartBtn = view.findViewById(R.id.addToCartBtn);

        sizesRv = view.findViewById(R.id.sizesRv);
        toppingsRv = view.findViewById(R.id.toppingsRv);

        // Get arguments safely
        if (getArguments() != null) {
            itemId = getArguments().getString("itemId");
            qty = getArguments().getInt("itemQty", 1);
        }

        qtyTxt.setText(String.valueOf(qty));

        menuItemController.getItemById(itemId, new OnResultListener<MenuItem>() {
            @Override
            public void onSuccess(MenuItem result) {
                nameTxt.setText(result.getName());
                priceTxt.setText(result.getPrice());
                descriptionTxt.setText(result.getDescription());

                price = Integer.parseInt((String) priceTxt.getText());
                total = price * qty;
                totalTxt.setText("Total: "+String.valueOf(total)+".00");

            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        // Listeners
        plusBtn.setOnClickListener(v -> {
            qty++;
            qtyTxt.setText(String.valueOf(qty));
            total = price * qty;
            totalTxt.setText("Total: "+String.valueOf(total)+".00");
        });

        minusBtn.setOnClickListener(v -> {
            if (qty > 1) {
                qty--;
                qtyTxt.setText(String.valueOf(qty));
                total = price * qty;
                totalTxt.setText("Total: "+String.valueOf(total)+".00");
            }
        });

        addToCartBtn.setOnClickListener(v -> {
            // TODO: send this item to cart (via ViewModel, shared prefs, or callback)
        });
    }
}