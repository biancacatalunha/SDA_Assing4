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
package com.example.catalunhab.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.catalunhab.fragment.BookListFragment;
import com.example.catalunhab.fragment.SettingsFragment;
import com.example.sdaassign4_2019.R;


/**
 * DCU - SDA - Assignment 4
 *
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 *
 * Defined constants for the tabs so the switch is more readable
 *
 * @author Bianca Catalunha <bianca.catalunha2@mail.dcu.ie>
 * @since January 2020
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_2, R.string.tab_text_3};
    private static final String TAG = "SectionsPagerAdapter";

    private static final int BOOKS = 0;
    private static final int SETTINGS = 1;

    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    /**
     * @param position integer to identify each tab, defined in the constants above
     * @return a fragment depending on the number identifier of the tab selected
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case BOOKS:
                Log.d(TAG, "Books tab clicked");
                return new BookListFragment();
            case SETTINGS:
                Log.d(TAG, "Settings tab clicked");
                return new SettingsFragment();
            default:
                Log.d(TAG, "No matching fragment found");
                return new Fragment();
        }
    }

    /**
     * Returns the name of the title of the tab that is stored in the TAB_TILES array
     *
     * @param position integer to identify each tab
     * @return CharSequence
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    /**
     * @return number of tabs
     */
    @Override
    public int getCount() {
        return 2;
    }
}