package wildfire.volunteers.smokegator.solo.ui.settings;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import wildfire.volunteers.smokegator.solo.R;

public class SettingsFragment extends PreferenceFragmentCompat {

     @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}