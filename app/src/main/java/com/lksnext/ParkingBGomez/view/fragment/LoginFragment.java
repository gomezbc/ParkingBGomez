package com.lksnext.ParkingBGomez.view.fragment;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetPasswordOption;
import androidx.credentials.GetPublicKeyCredentialOption;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.databinding.FragmentLoginBinding;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.view.activity.MainActivity;
import com.lksnext.ParkingBGomez.viewmodel.LoginViewModel;

import java.util.Objects;

public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private LinearProgressIndicator loginProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        binding = FragmentLoginBinding.inflate(getLayoutInflater());

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        NavHostFragment navHostFragment = (NavHostFragment) getParentFragment();

        if (navHostFragment != null) {
            loginProgressBar = navHostFragment.requireView().getRootView().findViewById(R.id.progress_bar_login);
            loginProgressBar.setProgress(0, true);
        }

        //Acciones a realizar cuando el usuario clica el boton de crear cuenta (se cambia de pantalla)
        binding.registerText.setOnClickListener(v ->
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_loginFragment_to_signUpFragment));

        binding.forgotPassword.setOnClickListener(v ->
                NavHostFragment.findNavController(this)
                        .navigate(R.id.action_loginFragment_to_resetPasswordFragment));

        binding.signInGoogleChip.setOnClickListener(v -> {
                    if (loginProgressBar != null) {
                        loginProgressBar.setIndeterminate(true);
                    }
                    loginViewModel.signInWithGoogle(requireActivity(), new Callback() {
                        @Override
                        public void onSuccess() {
                            if (loginProgressBar != null) {
                                loginProgressBar.setIndeterminate(false);
                                loginProgressBar.setProgress(100, true);
                            }
                            loginViewModel.setLogged(true);
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(requireActivity(), R.string.login_failed_google, Toast.LENGTH_SHORT).show();
                            if (loginProgressBar != null) {
                                loginProgressBar.setIndeterminate(false);
                                loginProgressBar.setProgress(0, true);
                            }
                        }
                    });
                });


        //Observamos la variable logged, la cual nos informara cuando el usuario intente hacer login y se
        //cambia de pantalla en caso de login correcto
        LiveData<Boolean> isLogged = loginViewModel.isLogged();
        isLogged.observe(requireActivity(), logged -> {
            if (logged != null) {
                if (logged) {
                    //Login Correcto
                    // Remove all observers to avoid memory leaks
                    isLogged.removeObservers(requireActivity());
                    loginViewModel.getLoginResult().removeObservers(getViewLifecycleOwner());
                    loginViewModel.getSignUpResult().removeObservers(getViewLifecycleOwner());
                    loginViewModel.getLoginFormState().removeObservers(getViewLifecycleOwner());
                    Intent intent = new Intent(requireActivity(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    binding.email.setError(getString(R.string.login_failed));
                    showLoginFailed(R.string.login_failed);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final TextInputEditText emailEditText = binding.email;
        final TextInputEditText passwordEditText = binding.password;
        final Button loginButton = binding.login;

        setLoginFormStateListener(loginButton, emailEditText, passwordEditText);

        setLoginResultListener();

        setAfterTextChangedListener(emailEditText, passwordEditText);
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String email = Objects.requireNonNull(binding.email.getText()).toString();
                String password = Objects.requireNonNull(binding.password.getText()).toString();
                loginViewModel.loginUser(email, password);
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
                    String email = Objects.requireNonNull(binding.email.getText()).toString();
                    String password = Objects.requireNonNull(binding.password.getText()).toString();
                    binding.login.setEnabled(false);
                    if (loginProgressBar != null) {
                        loginProgressBar.setVisibility(View.VISIBLE);
                        loginProgressBar.setIndeterminate(true);
                    }
                    loginViewModel.loginUser(email, password);
        });
    }

    private void setAfterTextChangedListener(TextInputEditText emailEditText, TextInputEditText passwordEditText) {
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
                loginViewModel.loginDataChanged(email, password);
            }
        };
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
    }

    private void setLoginFormStateListener(Button loginButton, TextInputEditText emailEditText, TextInputEditText passwordEditText) {
        loginViewModel.getLoginFormState().observe(getViewLifecycleOwner(), loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            if (loginFormState.isDataValid()){
                binding.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                loginButton.setEnabled(true);
            }
            if (loginFormState.getUsernameError() != null) {
                emailEditText.setError(getString(loginFormState.getUsernameError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
                binding.passwordLayout.setEndIconMode(TextInputLayout.END_ICON_NONE);
            }
        });
    }

    private void setLoginResultListener() {
        loginViewModel.getLoginResult().observe(getViewLifecycleOwner(), loginResult -> {
            binding.login.setEnabled(true);
            if (loginResult == null) {
                return;
            }
            if (loginResult.getError() != null) {
                if (loginProgressBar != null) {
                    loginProgressBar.setVisibility(View.INVISIBLE);
                    loginProgressBar.setProgress(0);
                    loginProgressBar.setIndeterminate(false);
                }
                showLoginFailed(loginResult.getError());
            }
            if (loginResult.getSuccess() != null && loginProgressBar != null) {
                loginProgressBar.setProgress(100);
                loginProgressBar.setIndeterminate(false);
            }
        });
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        if (getContext() != null && getContext().getApplicationContext() != null) {
            Snackbar.make(binding.getRoot(), errorString, LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
