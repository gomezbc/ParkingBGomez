package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.databinding.FragmentResetPasswordFinishBinding;

public class ResetPasswordFinishFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentResetPasswordFinishBinding binding = FragmentResetPasswordFinishBinding.inflate(getLayoutInflater());

        NavHostFragment navHostFragment = (NavHostFragment) getParentFragment();

        if (navHostFragment != null) {
            LinearProgressIndicator loginProgressBar = navHostFragment.requireView().getRootView().findViewById(R.id.progress_bar_login);
            if (loginProgressBar != null) {
                loginProgressBar.setIndeterminate(false);
                loginProgressBar.setProgress(100, true);
            }
        }

        binding.passwordResetContinueButton.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_resetPasswordFinishFragment_to_loginFragment));

        return binding.getRoot();
    }
}
