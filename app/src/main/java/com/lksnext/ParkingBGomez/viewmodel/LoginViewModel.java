package com.lksnext.ParkingBGomez.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.DataRepository;
import com.lksnext.ParkingBGomez.data.login.LoginResult;
import com.lksnext.ParkingBGomez.data.signup.SignUpFormState;
import com.lksnext.ParkingBGomez.domain.Callback;
import com.lksnext.ParkingBGomez.data.login.LoginFormState;
import com.lksnext.ParkingBGomez.utils.GoogleSignInHelper;

import java.util.regex.Pattern;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Boolean> logged = new MutableLiveData<>(null);

    private final MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private final MutableLiveData<SignUpFormState> signUpFormState = new MutableLiveData<>();
    private final MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();

    public LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }
    public LiveData<SignUpFormState> getSignUpFormState() {
        return signUpFormState;
    }
    public LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }
    public LiveData<Boolean> getSignUpResult() {
        return signUpResult;
    }

    public LiveData<Boolean> isLogged(){
        return logged;
    }
    public void setLogged(Boolean value){
        logged.setValue(value);
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

    public void signInWithGoogle(Context context, Callback callback) {
        GoogleSignInHelper googleSignInHelper = new GoogleSignInHelper(context);
        googleSignInHelper.signInWithGoogle(callback);
    }

    public void signUpUser(String email, String password) {
        DataRepository.getInstance().signUp(email, password, new Callback() {
            @Override
            public void onSuccess() {
                signUpResult.setValue(Boolean.TRUE);
            }

            @Override
            public void onFailure() {
                signUpResult.setValue(Boolean.FALSE);
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

    public void signUpDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            signUpFormState.setValue(new SignUpFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            signUpFormState.setValue(new SignUpFormState(null, R.string.invalid_password));
        } else {
            signUpFormState.setValue(new SignUpFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        return Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$").matcher(username).matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

}
