package com.androidapp.pizzamania.ui.menu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.pizzamania.adapter.CategoryCardRvAdapter;
import com.androidapp.pizzamania.callBack.OnResultListener;
import com.androidapp.pizzamania.controller.CategoryController;
import com.androidapp.pizzamania.databinding.FragmentMenuBinding;
import com.androidapp.pizzamania.databinding.FragmentMenuBinding;
import com.androidapp.pizzamania.model.Category;

import java.util.ArrayList;
import java.util.List;

public class MenuFragment extends Fragment {
    private Button filterBtn;
    private EditText searchTxt;
    private String search;
    private RecyclerView categoryCardRv;
    private ProgressBar progressBar;
    private ArrayList<Category> categoryArrayList, filteredArrayList;
    private CategoryCardRvAdapter categoryCardRvAdapter;
    private CategoryController categoryController;

    private FragmentMenuBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MenuViewModel menuViewModel =
                new ViewModelProvider(this).get(MenuViewModel.class);

        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        filterBtn= binding.filterBtn;
        searchTxt = binding.searchTxt;
        categoryCardRv = binding.categoryCardRv;
        progressBar = binding.progressBar;;
        categoryController = new CategoryController();

        categoryArrayList = new ArrayList<>();
        filteredArrayList = new ArrayList<>();
        categoryCardRv.setHasFixedSize(true);
        categoryCardRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        categoryCardRvAdapter = new CategoryCardRvAdapter(categoryArrayList, requireContext());
        categoryCardRv.setAdapter(categoryCardRvAdapter);

        categoryController.getAllCategories(new OnResultListener<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                progressBar.setVisibility(View.GONE);
                if (!result.isEmpty()) {
                    categoryArrayList.clear();
                    categoryArrayList.addAll(result);
                    categoryCardRvAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "No data found in Database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Fail to get the data.", Toast.LENGTH_SHORT).show();
                Log.d("Error", "Fail to get the data. "+e);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}