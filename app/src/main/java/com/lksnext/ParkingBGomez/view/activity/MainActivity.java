package com.lksnext.ParkingBGomez.view.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());

        NavHostFragment navHostFragment = binding.navHostFragmentContentMain.getFragment();
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = binding.bottomNavigation;
        bottomNav.setItemIconTintList(null);
        NavigationUI.setupWithNavController(bottomNav, navController);

        List<Integer> bottomNavMenuItem = List.of(R.id.inicioMainFragment, R.id.reservarMainFragment,
                R.id.reservasMainFragment, R.id.cuentaMainFragment);

        navController.addOnDestinationChangedListener((navController1, destination, bundle) -> {
            if (bottomNavMenuItem.contains(destination.getId())) {
                bottomNav.setVisibility(View.VISIBLE);
            } else {
                bottomNav.setVisibility(View.GONE);
            }
        });

        setContentView(binding.getRoot());
    }
}