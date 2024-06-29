package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentSettingsBinding;
import com.lksnext.ParkingBGomez.enums.SettingsEnum;

public class SettingsWrapperFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentSettingsBinding binding = FragmentSettingsBinding.inflate(inflater);

        SettingsEnum settingOption = SettingsWrapperFragmentArgs.fromBundle(getArguments()).getSettingOption();

        Toolbar toolbar = binding.settingsToolbar;
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        if (settingOption == SettingsEnum.ABOUT_ME) {
            toolbar.setTitle(R.string.about_me);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new AboutMeSettingsFragment())
                    .commit();
        } else if (settingOption == SettingsEnum.GENERAL_SETTINGS) {
            toolbar.setTitle(R.string.settings);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }

        binding.settingsToolbar.setNavigationOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            navController.navigateUp();
        });


        return binding.getRoot();
    }

}
