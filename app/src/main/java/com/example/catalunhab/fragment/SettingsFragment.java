package com.example.catalunhab.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.sdaassign4_2019.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

import static com.example.catalunhab.activity.LoginActivity.mAuth;
import static com.example.catalunhab.activity.LoginActivity.mGoogleSignInClient;

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
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private EditTextPreference email;
    private EditTextPreference name;
    private Preference reset;
    private Preference signOut;
    private Preference id;
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
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
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
                    }
                });

        name.setOnPreferenceChangeListener(
                new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Log.d(TAG, "Name onPreferenceChange got called. New value: " + newValue);

                        Toast toast = Toast.makeText(getContext(), R.string.name_changed_successfully, Toast.LENGTH_SHORT);
                        toast.show();

                        Log.d(TAG, "Name is changed successfully");
                        return true;
                    }
                });

        reset.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "Reset clicked");
                alertDialog();
                return false;
            }
        });

        id.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "Id clicked");
                return false;
            }
        });

        signOut.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Log.d(TAG, "SignOut clicked");
                signOut();
                return false;
            }
        });
    }

    private void signOut() {
        Log.d(TAG, "Sign Out called");

        mAuth.signOut();

        mGoogleSignInClient.signOut().addOnCompleteListener(Objects.requireNonNull(getActivity()),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast toast = Toast.makeText(getContext(), "signed out", Toast.LENGTH_SHORT);
                        toast.show();

                        Intent MainActivity = new Intent(getContext(), com.example.catalunhab.activity.LoginActivity.class);
                        startActivity(MainActivity);

                        Log.d(TAG, "Signed Out");
                    }
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
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Objects.requireNonNull(getActivity()));
                        pref.edit()
                            .clear()
                            .apply();

                        initiate();
                        Log.d(TAG, "Preferences reset");
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

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
