/**
 * MIT License
 *
 * Copyright (c) 2016 Hishmad Abubakar Al-Amudi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.stockita.stockitapointofsales.utilities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stockita.stockitapointofsales.R;
import com.stockita.stockitapointofsales.activities.MainActivity;


/**
 * Settings as shared preferences
 */
public class SettingsActivity extends AppCompatActivity {

    private static final String TAG_LOG = SettingsActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Set the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Show the back button on the top left
        if (toolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    finish();
                }
            });
        }

        
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pref_discount_frame, new PrefDiscountFragment())
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pref_service_frame, new PrefServiceFragment())
                    .commit();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.pref_tax_frame, new PrefTaxFragment())
                    .commit();
        }

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getBaseContext(), MainActivity.class));
        finish();

    }

    /**
     * Discount Preference Fragment
     */
    public static class PrefDiscountFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences mSharedPrefereces;

        public PrefDiscountFragment() {}


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            // Get the general preference layout
            addPreferencesFromResource(R.xml.pref_general_discount);

            // Initialize the SharedPreferences
            mSharedPrefereces = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());

            // Assign the listener
            onSharedPreferenceChanged(mSharedPrefereces, "pref_discount");

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("pref_discount")) {

                Preference preference = findPreference(key);
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(sharedPreferences.getString(key, String.valueOf(editTextPreference.getSummary())));

            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }



    /**
     * Service charges Preference Fragment
     */
    public static class PrefServiceFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences mSharedPreferences;

        public PrefServiceFragment() {}


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            // Get the general preference layout
            addPreferencesFromResource(R.xml.pref_general_service);

            // Initialize the SharedPreferences
            mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());

            // Assign the listener
            onSharedPreferenceChanged(mSharedPreferences, "pref_service");

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("pref_service")) {

                Preference preference = findPreference(key);
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(sharedPreferences.getString(key, String.valueOf(editTextPreference.getSummary())));

            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }


    /**
     * Tax Preference Fragment
     */
    public static class PrefTaxFragment extends PreferenceFragmentCompat
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SharedPreferences mSharedPreferences;

        public PrefTaxFragment() {}


        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            // Get the general preference layout
            addPreferencesFromResource(R.xml.pref_general_tax);

            // Initialize the SharedPreferences
            mSharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());

            // Assign the listener
            onSharedPreferenceChanged(mSharedPreferences, "pref_tax");

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals("pref_tax")) {

                Preference preference = findPreference(key);
                EditTextPreference editTextPreference = (EditTextPreference) preference;
                editTextPreference.setSummary(sharedPreferences.getString(key, String.valueOf(editTextPreference.getSummary())));

            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
