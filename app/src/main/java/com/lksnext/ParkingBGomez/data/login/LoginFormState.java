package com.lksnext.ParkingBGomez.data.login;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Data validation state of the login form.
 */
public class LoginFormState {
    @Nullable
    private final Integer usernameError;
    @Nullable
    private final Integer passwordError;
    private final boolean isDataValid;

    public LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    public LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginFormState that = (LoginFormState) o;
        return isDataValid == that.isDataValid && Objects.equals(usernameError, that.usernameError) && Objects.equals(passwordError, that.passwordError);
    }

    @Override
    public int hashCode() {
        return Objects.hash(usernameError, passwordError, isDataValid);
    }
}