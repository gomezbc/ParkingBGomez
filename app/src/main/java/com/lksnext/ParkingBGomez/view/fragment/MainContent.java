package com.lksnext.ParkingBGomez.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentMainContentBinding;
import com.lksnext.ParkingBGomez.enums.BottomNavState;
import com.lksnext.ParkingBGomez.viewmodel.MainViewModel;

import java.util.Objects;

public class MainContent extends Fragment {

    private FragmentMainContentBinding binding;
    private MainViewModel mainViewModel;
    private MenuItem prevMenuItem;
    private Drawable prevIcon;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMainContentBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavHostFragment navHostFragment = binding.navHostFragmentContentMain.getFragment();
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = binding.bottomNavigation;
        NavigationUI.setupWithNavController(bottomNav, navController);
        //setBottomNavListener(bottomNav, navController); //With this navigation listener i don't when user goes back it doesn't work properly
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setBottomNavListener(BottomNavigationView bottomNav, NavController navController) {
        bottomNav.setOnItemSelectedListener(item -> {
            // If there was a previously selected item, restore its icon
            if (prevMenuItem != null) {
                prevMenuItem.setIcon(prevIcon);
            }

            prevIcon = item.getIcon();

            final int id = item.getItemId();
            BottomNavState state = null;

            if (id == R.id.inicioMainFragment) {
                state = BottomNavState.HOME;
                item.setIcon(R.drawable.home_fill);
            } else if (id == R.id.reservarMainFragment) {
                state = BottomNavState.RESERVAR;
                item.setIcon(R.drawable.directions_car_fill);
            } else if (id == R.id.reservasMainFragment) {
                state = BottomNavState.RESERVAS;
                item.setIcon(R.drawable.bookmark_fill);
            } else if (id == R.id.cuentaMainFragment) {
                state = BottomNavState.CUENTA;
                item.setIcon(R.drawable.person_fill);
            }

            // Update the state and navigate to the selected fragment
            if (state != null){
                mainViewModel.setBottomNavState(state);
            }

            // Check if the current destination is the same as the destination you're navigating to
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == id) {
                return true;
            }

            navController.popBackStack(id, true);
            navController.navigate(id);

            // Update the previously selected item
            prevMenuItem = item;

            return true;
        });
    }
}
