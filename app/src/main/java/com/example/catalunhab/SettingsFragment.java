package com.example.catalunhab;

import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
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
 * References:
 * Settings - https://developer.android.com/guide/topics/ui/settings
 * OnPreferenceChangeListener - https://www.programcreek.com/java-api-examples/?class=android.preference.SwitchPreference&method=setOnPreferenceChangeListener
 * Check if an email is valid - https://stackoverflow.com/questions/22348212/android-check-if-an-email-address-is-valid-or-not
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference email;
    private static final String TAG = "SettingsFragment";

    /**
     * Sets an onPreferenceChangedListener for email.
     * If the email entered by the user in settings is valid, a toast display a success message.
     * If the email is invalid, a toast displays an error message.
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        email.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Log.d(TAG, "onPreferenceChange got called " + newValue);

                        if(android.util.Patterns.EMAIL_ADDRESS.matcher(newValue.toString()).matches()) {
                            Toast toast = Toast.makeText(getContext(), R.string.email_changed_successfully, Toast.LENGTH_SHORT);
                            toast.show();

                            Log.d(TAG, "Email is valid");
                            return true;
                        } else {
                            Toast toast = Toast.makeText(getContext(), R.string.email_is_invalid, Toast.LENGTH_SHORT);
                            toast.show();

                            Log.d(TAG, "Email is invalid");
                            return false;
                        }
                    }
                });

    }

    /**
     * Binds the preferences to the fragment
     *
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     * @param rootKey If non-null, this preference fragment should be rooted at the
     * PreferenceScreen with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        email = findPreference("email");

        onBindListeners(email);
    }


    /**
     * When the user clicks on email, the keyboard's main screen displays "@" and "."
     *
     * @param email stored in SharedPreferences
     */
    private void onBindListeners(EditTextPreference email) {
        if (email != null) {
            email.setOnBindEditTextListener(
                    new EditTextPreference.OnBindEditTextListener() {
                        @Override
                        public void onBindEditText(@NonNull EditText editText) {
                            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                            Log.d(TAG, "onBindListener got called");
                        }
                    });
        }
    }
}
