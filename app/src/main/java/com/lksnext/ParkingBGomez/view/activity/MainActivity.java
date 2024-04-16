package com.lksnext.ParkingBGomez.view.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.lksnext.ParkingBGomez.view.fragment.LoginFragment;
import com.lksnext.ParkingBGomez.view.fragment.MainContent;
import com.lksnext.ParkingBGomez.NavigationHost;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationHost {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            // add login fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new LoginFragment())
                    .commit();
        }else{
            // add login fragment
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new MainContent())
                    .commit();
        }

    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    public void navigateTo(@NonNull Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}