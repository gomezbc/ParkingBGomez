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

        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        assert navHostFragment != null;
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = binding.bottomNavigation;
        setBottomNavListener(bottomNav, navController);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        restoreBottomNavState(bottomNav, Objects.requireNonNull(mainViewModel.getBottomNavState().getValue()));

        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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

            // Store the original icon of the currently selected item
            prevIcon = item.getIcon();

            final int id = item.getItemId();
            if (id == R.id.item_1) {
                item.setIcon(R.drawable.home_fill);
                mainViewModel.setBottomNavState(BottomNavState.HOME);
                navController.navigate(R.id.inicioMainFragment);
            } else if (id == R.id.item_2) {
                item.setIcon(R.drawable.directions_car_fill);
                mainViewModel.setBottomNavState(BottomNavState.RESERVAR);
                navController.navigate(R.id.reservarMainFragment);
            } else if (id == R.id.item_3) {
                item.setIcon(R.drawable.bookmark_fill);
                mainViewModel.setBottomNavState(BottomNavState.RESERVAS);
                navController.navigate(R.id.reservasMainFragment);
            } else if (id == R.id.item_4) {
                item.setIcon(R.drawable.person_fill);
                mainViewModel.setBottomNavState(BottomNavState.CUENTA);
                navController.navigate(R.id.cuentaMainFragment);
            }

            // Update the previously selected item
            prevMenuItem = item;

            return true;
        });
    }

    /**
     * Restores the state of the content.
     *
     * @param bottomNav The bottom navigation bar view
     * @param state    The state to restore
     */
    private void restoreBottomNavState(BottomNavigationView bottomNav, BottomNavState state) {
        switch (state) {
            case HOME:
                bottomNav.setSelectedItemId(R.id.item_1);
                break;
            case RESERVAR:
                bottomNav.setSelectedItemId(R.id.item_2);
                break;
            case RESERVAS:
                bottomNav.setSelectedItemId(R.id.item_3);
                break;
            case CUENTA:
                bottomNav.setSelectedItemId(R.id.item_4);
                break;
        }
    }


}
