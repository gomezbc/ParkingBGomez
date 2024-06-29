package com.lksnext.ParkingBGomez.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.lksnext.ParkingBGomez.R;
import com.lksnext.ParkingBGomez.utils.ThemeSetup;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_preferences, rootKey);

        Preference notificationPreference = findPreference("notification_settings");
        if (notificationPreference != null) {
            notificationPreference.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
                startActivity(intent);
                return true;
            });
        }

        Preference themePreference = getPreferenceManager().findPreference(getString(R.string.settings_theme_key));
        if (themePreference != null) {
            themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                ThemeSetup.applyTheme((String) newValue, requireContext());
                return true;
            });
        }
    }
}