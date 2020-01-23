/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.catalunhab.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.sdaassign4_2019.R;

import java.util.Objects;

import static com.example.catalunhab.activity.LoginActivity.mAuth;
import static com.example.catalunhab.activity.LoginActivity.mGoogleSignInClient;

/**
 * DCU - SDA - Assignment 4
 *
 * Preference library persists data with SharedPreferences by default
 *
 * @author Bianca Catalunha <bianca.catalunha2@mail.dcu.ie>
 * @since January 2020
 *
 * References:
 * Settings - https://developer.android.com/guide/topics/ui/settings
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference email;
    private EditTextPreference name;
    private Preference reset;
    private Preference signOut;
    private Preference id;
    private Preference admin;
    private String rootKeyGlobal;
    private static final String TAG = "SettingsFragment";

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
        rootKeyGlobal = rootKey;
        initiate();
    }

    /**
     * I've extracted the initialization code because it is also being invoked when the user
     * clears all preferences. It's how I'm reloading the page to display "Not set"
     * in the summary of Email and Name.
     *
     * Reference
     * How to reload a fragment - https://stackoverflow.com/questions/8003098/how-do-you-refresh-preferenceactivity-to-show-changes-in-the-settings
     */
    private void initiate() {
        setPreferencesFromResource(R.xml.preferences, rootKeyGlobal);

        email = findPreference("email");
        name = findPreference("name");
        reset = findPreference("reset");
        id = findPreference("id");
        signOut = findPreference("signOut");
        admin = findPreference("admin");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getContext()));
        String idValue = pref.getString("id", null);
        id.setSummary(idValue);

        onBindListeners(email);
        onPreferenceChangedListeners();
    }

    /**
     * Sets an onPreferenceChangedListener for email.
     * If the email entered by the user in settings is valid, a toast displays a success message
     * and the value is updated in SharedPreferences.
     * If the email is invalid, a toast displays an error message and the value is not updated
     * in SharedPreferences.
     *
     * Sets an onPreferenceChangedListener for name.
     * When the name is changed, a success message is displayed and the value is updated in
     * SharedPreferences.
     *
     * References:
     * Check if an email is valid - https://stackoverflow.com/questions/22348212/android-check-if-an-email-address-is-valid-or-not
     * OnPreferenceChangeListener - https://www.programcreek.com/java-api-examples/?class=android.preference.SwitchPreference&method=setOnPreferenceChangeListener
     */
    private void onPreferenceChangedListeners() {

        email.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    Log.d(TAG, "Email onPreferenceChange got called. New value: " + newValue);

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
                });

        name.setOnPreferenceChangeListener(
                (preference, newValue) -> {
                    Log.d(TAG, "Name onPreferenceChange got called. New value: " + newValue);

                    Toast toast = Toast.makeText(getContext(), R.string.name_changed_successfully, Toast.LENGTH_SHORT);
                    toast.show();

                    Log.d(TAG, "Name is changed successfully");
                    return true;
                });

        reset.setOnPreferenceClickListener(preference -> {
            Log.d(TAG, "Reset clicked");
            alertDialog();
            return false;
        });

        id.setOnPreferenceClickListener(preference -> {
            Log.d(TAG, "Id clicked");
            Toast toast = Toast.makeText(getContext(), R.string.id_cannot_be_changed, Toast.LENGTH_SHORT);
            toast.show();
            return false;
        });

        signOut.setOnPreferenceClickListener(preference -> {
            Log.d(TAG, "SignOut clicked");
            signOut();
            return false;
        });

        admin.setOnPreferenceClickListener(preference -> {
            Log.d(TAG, "Admin clicked");
            adminAlertDialog();
            return false;
        });
    }

    //todo put this method in loginActivity
    private void signOut() {
        Log.d(TAG, "Sign Out called");

        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(Objects.requireNonNull(getActivity()),
                task -> {
                    Toast toast = Toast.makeText(getContext(), R.string.signed_out, Toast.LENGTH_SHORT);
                    toast.show();

                    Intent MainActivity = new Intent(getContext(), com.example.catalunhab.activity.LoginActivity.class);
                    startActivity(MainActivity);

                    Log.d(TAG, "Signed Out");
                });

    }

    /**
     * This dialog is displayed when the user clicks on the clear data switch.
     * It asks if the user is sure, if so, clears all SharedPreferences data and
     * reloads the preferences fragment, in order to display the new value of the
     * preferences on the screen.
     *
     * References:
     * Dialogs - https://developer.android.com/guide/topics/ui/dialogs
     */
    private void alertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder
                .setMessage(R.string.reset_dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
                    pref.edit()
                            .clear()
                            .apply();

                    initiate();
                    Log.d(TAG, "Preferences reset");
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Reference:
     * Input text dialog - https://stackoverflow.com/questions/10903754/input-text-dialog-android
     */
    private void adminAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final String pass = getResources().getString(R.string.admin_password);

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder
                .setView(input)
                .setMessage(R.string.password)
                .setPositiveButton(R.string.login, (dialog, which) -> {
                    if(input.getText().toString().equals(pass)) {
                        Intent intent = new Intent(getContext(), com.example.catalunhab.activity.UploadDataActivity.class);
                        startActivity(intent);

                        Log.d(TAG, "Admin login successful");
                    } else {
                        Toast toast = Toast.makeText(getContext(), R.string.wrong_password, Toast.LENGTH_SHORT);
                        toast.show();
                        Log.d(TAG, "Admin login failed");
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> {});

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * When the user clicks on email, the keyboard's main screen displays "@" and "."
     *
     * @param email stored in SharedPreferences
     */
    private void onBindListeners(EditTextPreference email) {
        if (email != null) {
            email.setOnBindEditTextListener(
                    editText -> {
                        editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                        Log.d(TAG, "onBindListener got called");
                    });
        }
    }
}
