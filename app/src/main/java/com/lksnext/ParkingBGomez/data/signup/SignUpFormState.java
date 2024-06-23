package com.lksnext.ParkingBGomez.data.signup;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
public class SignUpFormState {
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

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
}