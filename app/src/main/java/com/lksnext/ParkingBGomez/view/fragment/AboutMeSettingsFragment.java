package com.lksnext.ParkingBGomez.view.fragment;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.lksnext.ParkingBGomez.R;

public class AboutMeSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.about_me_preferences, rootKey);
    }
}