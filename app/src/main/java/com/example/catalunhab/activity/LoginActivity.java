package com.example.catalunhab.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

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

import java.util.Objects;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    public static FirebaseAuth mAuth;
    public static GoogleSignInClient mGoogleSignInClient;

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
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        showProgressBar();

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");

                            addUserInfoToSharedPreferences();

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
     * info is null or not set
     */
    private void addUserInfoToSharedPreferences() {
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        String name = pref.getString("name", null);
        String email = pref.getString("email", null);

        if(name == null || name.equals("Not set")) {
            editor.putString("name", Objects.requireNonNull(user).getDisplayName());
        }

        if(email == null || email.equals("Not set")) {
            editor.putString("email", Objects.requireNonNull(user).getEmail());
        }

        editor.putString("id", Objects.requireNonNull(user).getUid());

        editor.apply();

        Log.d(TAG, "id is " + pref.getString("id", null));
    }
}
