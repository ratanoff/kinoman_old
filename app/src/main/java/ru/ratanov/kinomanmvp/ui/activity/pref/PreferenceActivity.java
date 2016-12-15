package ru.ratanov.kinomanmvp.ui.activity.pref;

import android.os.Bundle;

import ru.ratanov.kinomanmvp.R;

public class PreferenceActivity extends android.preference.PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setContentView(R.layout.pref_button_test);

    }

}
