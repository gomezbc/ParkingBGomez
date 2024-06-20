package com.lksnext.ParkingBGomez.viewmodel;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.data.login.LoginResult;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.data.login.LoginFormState;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Boolean> logged = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> loginProgress = new MutableLiveData<>(0);

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }
    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public LiveData<Boolean> isLogged(){
        return logged;
    }

    public void loginUser(String email, String password) {
        DataRepository.getInstance().login(email, password, new Callback() {
            @Override
            public void onSuccess() {
                logged.setValue(Boolean.TRUE);
            }

            @Override
            public void onFailure() {
                logged.setValue(Boolean.FALSE);
                loginResult.setValue(new LoginResult(R.string.login_failed));
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    public LiveData<Integer> getLoginProgress(){
        return loginProgress;
    }

    public void setLoginProgress(int progress){
        loginProgress.setValue(progress);
    }

}
