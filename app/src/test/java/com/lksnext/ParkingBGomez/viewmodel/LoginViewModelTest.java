package com.lksnext.ParkingBGomez.viewmodel;

import static org.junit.Assert.assertEquals;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.lksnext.ParkingBGomez.LiveDataTestUtil;
import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.data.signup.SignUpFormState;
import com.lksnext.ParkingBGomez.data.login.LoginFormState;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LoginViewModelTest {

    private LoginViewModel loginViewModel;

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp(){
        loginViewModel = new LoginViewModel();
    }

    @Test
    public void testLoginDataChanged() throws InterruptedException {
        loginViewModel.loginDataChanged("invalid email", "password");
        LoginFormState actualValue1 = LiveDataTestUtil.getOrAwaitValue(loginViewModel.getLoginFormState());
        assertEquals(new LoginFormState(R.string.invalid_username, null), actualValue1);

        loginViewModel.loginDataChanged("test@example.com", "short");
        LoginFormState actualValue2 = LiveDataTestUtil.getOrAwaitValue(loginViewModel.getLoginFormState());
        assertEquals(new LoginFormState(null, R.string.invalid_password), actualValue2);

        loginViewModel.loginDataChanged("test@example.com", "validpassword");
        LoginFormState actualValue3 = LiveDataTestUtil.getOrAwaitValue(loginViewModel.getLoginFormState());
        assertEquals(new LoginFormState(true), actualValue3);
    }

    @Test
    public void testSignUpDataChanged() throws InterruptedException {
        loginViewModel.signUpDataChanged("invalid email", "password");
        SignUpFormState actualValue1 = LiveDataTestUtil.getOrAwaitValue(loginViewModel.getSignUpFormState());
        assertEquals(new SignUpFormState(R.string.invalid_username, null), actualValue1);

        loginViewModel.signUpDataChanged("test@example.com", "short");
        SignUpFormState actualValue2 = LiveDataTestUtil.getOrAwaitValue(loginViewModel.getSignUpFormState());
        assertEquals(new SignUpFormState(null, R.string.invalid_password), actualValue2);

        loginViewModel.signUpDataChanged("test@example.com", "validpassword");
        SignUpFormState actualValue3 = LiveDataTestUtil.getOrAwaitValue(loginViewModel.getSignUpFormState());
        assertEquals(new SignUpFormState(true), actualValue3);
    }
}
