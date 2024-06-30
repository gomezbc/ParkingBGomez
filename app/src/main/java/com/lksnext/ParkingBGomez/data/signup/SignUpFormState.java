package com.lksnext.ParkingBGomez.data.signup;

import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * Data validation state of the login form.
 */
public class SignUpFormState {
    @Nullable
    private final Integer emailError;
    @Nullable
    private final Integer passwordError;
    private final boolean isDataValid;

    public SignUpFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.emailError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    public SignUpFormState(boolean isDataValid) {
        this.emailError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
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
        SignUpFormState that = (SignUpFormState) o;
        return isDataValid == that.isDataValid && Objects.equals(emailError, that.emailError) && Objects.equals(passwordError, that.passwordError);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailError, passwordError, isDataValid);
    }
}