package com.dimi.qrpasswordscanner;

import android.preference.PreferenceActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;


public class SettingsActivity extends PreferenceActivity {

	/** Called when the activity is first created. */
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content,new SettingsFragment()).commit();
	}
}
