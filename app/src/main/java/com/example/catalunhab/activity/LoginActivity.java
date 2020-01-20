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
package com.example.catalunhab.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.catalunhab.type.User;
import com.example.sdaassign4_2019.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.Objects;

/**
 * DCU - SDA - Assignment 4
 *
 * Firebase authentication with Google
 *
 * @author Bianca Catalunha <bianca.catalunha2@mail.dcu.ie>
 * @since January 2020
 *
 * References:
 * Firebase - https://firebase.google.com/docs/firestore/quickstart
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    public static FirebaseAuth mAuth;
    //todo fix this
    public static GoogleSignInClient mGoogleSignInClient;
    public static User userInfo;

    /**
     * Sets the progress bar on the base activity
     * Sets an onClick listener to the sign in button
     * Configures Google sign in
     * Initializes Firebase Auth
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        setProgressBar(R.id.progressBar);

        findViewById(R.id.signInButton).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signInButton) {
            signIn();
        }
    }

    /**
     * Starts Google sign in intent
     */
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    /**
     * Gets the result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...)
     * If the sign in was successful, authenticate with Firebase
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    /**
     * Shows progress bar once firebase auth with Google starts
     * On successful sign-in, starts main activity intent.
     * On unsuccessful sign-in, displays message to the user.
     * When auth finished, hides the progress bar
     *
     * @param acct GoogleSignInAccount details
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        showProgressBar();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = mAuth.getCurrentUser();
                            userInfo = new User(Objects.requireNonNull(user).getUid(), user.getDisplayName(), user.getEmail());

                            addUserInfoToSharedPreferences();

                            writeToDatabase();

                            Intent MainActivity = new Intent(getApplicationContext(), com.example.catalunhab.MainActivity.class);
                            startActivity(MainActivity);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.login_activity), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }

                        hideProgressBar();
                    }
                });
    }

    /**
     * Adds Google auth user information to SharedPreferences if SharedPreferences
     * info is null
     * If the user cleared all data, it will remain "Not Set"
     */
    private void addUserInfoToSharedPreferences() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        String name = pref.getString("name", null);
        String email = pref.getString("email", null);
        String id = pref.getString("id", null);

        if(name == null) {
            editor.putString("name", userInfo.getName());
            Log.d(TAG, "Name: " + userInfo.getName() + " added to SharedPreferences");
        }

        if(email == null) {
            editor.putString("email", userInfo.getEmail());
            Log.d(TAG, "Email: " + userInfo.getEmail() + " added to SharedPreferences");
        }

        if(id == null) {
            editor.putString("id", userInfo.getId());
            Log.d(TAG, "Id: " + userInfo.getId() + " added to SharedPreferences");
        }

        editor.apply();
    }

    private void writeToDatabase() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userInfo.getId()).setValue(userInfo);
        Log.d(TAG, "Wrote user info to database");
    }
}
