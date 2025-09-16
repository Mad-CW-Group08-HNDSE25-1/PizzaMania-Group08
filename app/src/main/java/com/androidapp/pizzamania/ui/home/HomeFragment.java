package com.androidapp.pizzamania.ui.home;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.androidapp.pizzamania.MainActivity;
import com.androidapp.pizzamania.R;
import com.androidapp.pizzamania.databinding.FragmentHomeBinding;
import com.androidapp.pizzamania.ui.menu.MenuFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeFragment extends Fragment {
    private Button btnDetectLocation;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnDetectLocation = binding.btnDetectLocation;

        // CATEGORY CLICKS
        binding.VPimage.setOnClickListener(v -> openMenuFragment());
        binding.VPtxt.setOnClickListener(v -> openMenuFragment());

        binding.NVPimage.setOnClickListener(v -> openMenuFragment());
        binding.NVPtxt.setOnClickListener(v -> openMenuFragment());

        binding.BevImage.setOnClickListener(v -> openMenuFragment());
        binding.BevTxt.setOnClickListener(v -> openMenuFragment());

        binding.DesImage.setOnClickListener(v -> openMenuFragment());
        binding.DesTxt.setOnClickListener(v -> openMenuFragment());
        return root;
    }

    private void openMenuFragment() {
        BottomNavigationView navView = requireActivity().findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_menu);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}