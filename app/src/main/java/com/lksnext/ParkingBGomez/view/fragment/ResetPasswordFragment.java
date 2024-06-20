package com.lksnext.ParkingBGomez.view.fragment;

import static android.widget.Toast.LENGTH_LONG;

import android.os.Bundle;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentResetPasswordBinding;
import com.lksnext.ParkingBGomez.domain.Callback;

public class ResetPasswordFragment extends Fragment{

    private FragmentResetPasswordBinding binding;
    private LinearProgressIndicator loginProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentResetPasswordBinding.inflate(getLayoutInflater());

        NavHostFragment navHostFragment = (NavHostFragment) getParentFragment();

        if (navHostFragment != null) {
            loginProgressBar = navHostFragment.requireView().getRootView().findViewById(R.id.progress_bar_login);
            if (loginProgressBar != null) {
                loginProgressBar.setProgress(50, true);
            }
        }

        binding.email.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.passwordResetContinueButton.performClick();
                return true;
            }
            return false;
        });

        binding.email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                boolean enableButton = true;
                if(!Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    binding.email.setError("Email no válido");
                    enableButton = false;
                } else {
                    binding.email.setError(null);
                }
                binding.passwordResetContinueButton.setEnabled(enableButton);
            }
        });

        binding.passwordResetContinueButton.setOnClickListener(v -> {
            if (loginProgressBar != null) {
                loginProgressBar.setIndeterminate(true);
            }
            String email = String.valueOf(binding.email.getText());
            binding.passwordResetContinueButton.setEnabled(false);
            DataRepository.getInstance().resetPassword(email, new Callback() {
                @Override
                public void onSuccess() {
                    NavHostFragment.findNavController(ResetPasswordFragment.this)
                            .navigate(R.id.action_resetPasswordFragment_to_resetPasswordFinishFragment);
                }

                @Override
                public void onFailure() {
                    Snackbar.make(binding.getRoot(), "Error al enviar el correo", LENGTH_LONG).show();
                    if (loginProgressBar != null) {
                        loginProgressBar.setIndeterminate(false);
                    }
                }
            });
        });

        return binding.getRoot();
    }

}
