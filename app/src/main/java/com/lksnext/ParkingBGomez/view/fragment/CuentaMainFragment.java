package com.lksnext.ParkingBGomez.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseUser;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentCuentaMainBinding;
import com.lksnext.ParkingBGomez.enums.SettingsEnum;
import com.lksnext.ParkingBGomez.view.activity.LoginActivity;

public class CuentaMainFragment extends Fragment{

    private FragmentCuentaMainBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCuentaMainBinding.inflate(inflater, container, false);

        FirebaseUser user = DataRepository.getInstance().getCurrentUser();
        var displayName = user.getDisplayName();
        if (displayName != null) {
            String username = user.getDisplayName().isEmpty() ? user.getEmail() : user.getDisplayName();
            binding.username.setText(username);
        }

        Uri userPhotoURI = user.getPhotoUrl();
        if (userPhotoURI != null) {
            binding.avatar.setImageURI(userPhotoURI);
        }else {
            binding.avatar.setImageResource(R.drawable.person_fill);
        }

        binding.btnLogout.setOnClickListener(v -> {
            DataRepository.getInstance().logout();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        binding.editarPerfil.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            CuentaMainFragmentDirections.ActionCuentaMainFragmentToSettingsWrapperFragment action =
                    CuentaMainFragmentDirections.actionCuentaMainFragmentToSettingsWrapperFragment(SettingsEnum.ABOUT_ME);
            navController.navigate(action);
        });

        binding.settings.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);
            CuentaMainFragmentDirections.ActionCuentaMainFragmentToSettingsWrapperFragment action =
                    CuentaMainFragmentDirections.actionCuentaMainFragmentToSettingsWrapperFragment(SettingsEnum.GENERAL_SETTINGS);
            navController.navigate(action);
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
