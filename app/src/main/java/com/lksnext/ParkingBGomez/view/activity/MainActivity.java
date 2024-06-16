package com.lksnext.ParkingBGomez.view.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.lksnext.ParkingBGomez.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();

        NavHostFragment navHostFragment = binding.navHostFragmentContentMain.getFragment();
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setItemIconTintList(null);
        NavigationUI.setupWithNavController(bottomNav, navController);

        setContentView(binding.getRoot());

    }

    @NonNull
    public FirebaseFirestore getDb() {
        return db;
    }
}