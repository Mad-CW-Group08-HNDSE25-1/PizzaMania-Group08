package com.androidapp.pizzamania;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidapp.pizzamania.adapter.OptionRvAdapter;
import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.MenuItemController;
import com.androidapp.pizzamania.controller.SizeController;
import com.androidapp.pizzamania.controller.ToppingController;
import com.androidapp.pizzamania.model.MenuItem;
import com.androidapp.pizzamania.model.Size;
import com.androidapp.pizzamania.model.Topping;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    private SizeController sizeController = new SizeController();
    private ToppingController toppingController = new ToppingController();
    private List<Size> sizes = new ArrayList<>();
    private List<Topping>  toppings = new ArrayList<>();

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

                menuItemController.getSizesByItemId(itemId, new OnResultListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> result) {
                        sizes.clear();
                        for(String s : result){
                            sizeController.getSizeById(s, new OnResultListener<Size>() {
                                @Override
                                public void onSuccess(Size result) {
                                    sizes.add(result);
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        OptionRvAdapter<Size> sizeAdapter = new OptionRvAdapter<>(sizes, selectedSizes -> {
                            recalcTotal(selectedSizes, null);
                        });
                        sizesRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        sizesRv.setAdapter(sizeAdapter);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

                menuItemController.getToppingsByItemId(itemId, new OnResultListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> result) {
                        toppings.clear();
                        for(String t : result){
                            toppingController.getToppingById(t, new OnResultListener<Topping>() {
                                @Override
                                public void onSuccess(Topping result) {
                                    toppings.add(result);
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        OptionRvAdapter<Topping> toppingAdapter = new OptionRvAdapter<>(toppings, selectedToppings -> {
                            recalcTotal(null, selectedToppings);
                        });
                        sizesRv.setLayoutManager(new LinearLayoutManager(getContext()));
                        sizesRv.setAdapter(toppingAdapter);
                    }

                    @Override
                    public void onFailure(Exception e) {

                    }
                });

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

    private void recalcTotal(Set<Size> selectedSizes, Set<Topping> selectedToppings) {
        int optionTotal = 0;

        if (selectedSizes != null) {
            for (Size size : selectedSizes) {
                optionTotal += Integer.parseInt(size.getPrice());
            }
        }
        if (selectedToppings != null) {
            for (Topping topping : selectedToppings) {
                optionTotal += Integer.parseInt(topping.getPrice());
            }
        }

        total = (price * qty) + optionTotal;
        totalTxt.setText("Total: " + total + ".00");
    }
}