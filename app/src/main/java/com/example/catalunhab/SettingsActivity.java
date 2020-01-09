package com.example.catalunhab;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sdaassign4_2019.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_fragment, new SettingsFragment())
                .commit();
    }

}

