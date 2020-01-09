package com.example.catalunhab;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sdaassign4_2019.R;

/**
 * DCU - SDA - Assignment 4
 *
 * @author Bianca Catalunha <bianca.catalunha2@mail.dcu.ie>
 * @since January 2020
 *
 * Preference library persists data with SharedPreferences by default
 *
 * Reference: https://developer.android.com/guide/topics/ui/settings
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference email;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        email = findPreference("email");

        onBindListeners(email);
    }


    private void onBindListeners(EditTextPreference email) {
        if (email != null) {
            email.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        }
                    });
        }
    }
}
