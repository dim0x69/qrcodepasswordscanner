package com.dimi.qrpasswordscanner;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class OldSettingsActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}