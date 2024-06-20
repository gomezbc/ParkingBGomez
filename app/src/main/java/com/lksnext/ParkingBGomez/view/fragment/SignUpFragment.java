package com.lksnext.ParkingBGomez.view.fragment;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentSignUpBinding;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;
import com.lksnext.ParkingBGomez.viewmodel.LoginViewModel;

import java.util.Objects;


public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private LinearProgressIndicator loginProgressBar;
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(getLayoutInflater());
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getParentFragment();

        if (navHostFragment != null) {
            loginProgressBar = navHostFragment.requireView().getRootView().findViewById(R.id.progress_bar_login);
            if (loginProgressBar != null) {
                loginProgressBar.setProgress(50, true);
            }
        }

        binding.signUpButton.setOnClickListener(v -> {
            if (loginProgressBar != null) {
                loginProgressBar.setIndeterminate(true);
            }
            String email = Objects.requireNonNull(binding.email.getText()).toString();
            String password = Objects.requireNonNull(binding.password.getText()).toString();
            loginViewModel.signUpUser(email, password);
        });


        setSignUpResultObserver();
        setAfterTextChangedListener();
        setSignUpFormStateListener();

        return binding.getRoot();
    }

    private void setSignUpResultObserver() {
        loginViewModel.getSignUpResult().observe(getViewLifecycleOwner(), signUpResult -> {
            if (signUpResult == null) {
                return;
            }
            if (signUpResult) {
                if (loginProgressBar != null) {
                    loginProgressBar.setProgress(100, true);
                    loginProgressBar.setIndeterminate(false);
                }
                if (DataRepository.getInstance().getCurrentUser() != null){
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }else {
                    NavHostFragment.findNavController(this)
                            .navigate(R.id.action_signUpFragment_to_loginFragment);
                }
            }else {
                Snackbar.make(binding.getRoot(), "Error al crear la cuenta. Intentalo de nuevo más tarde", LENGTH_LONG).show();
            }
        });
    }

    private void setSignUpFormStateListener() {
        loginViewModel.getSignUpFormState().observe(getViewLifecycleOwner(), signUpFormState -> {
            if (signUpFormState == null) {
                return;
            }
            if (signUpFormState.isDataValid()){
                binding.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                binding.signUpButton.setEnabled(true);
            }
            if (signUpFormState.getEmailError() != null) {
                binding.email.setError(getString(signUpFormState.getEmailError()));
            }
            if (signUpFormState.getPasswordError() != null) {
                binding.password.setError(getString(signUpFormState.getPasswordError()));
                binding.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
            }
        });
    }

    private void setAfterTextChangedListener() {
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                String email = Objects.requireNonNull(binding.email.getText()).toString();
                String password = Objects.requireNonNull(binding.password.getText()).toString();
                loginViewModel.signUpDataChanged(email, password);
            }
        };

        binding.email.addTextChangedListener(afterTextChangedListener);
        binding.password.addTextChangedListener(afterTextChangedListener);
    }
}
