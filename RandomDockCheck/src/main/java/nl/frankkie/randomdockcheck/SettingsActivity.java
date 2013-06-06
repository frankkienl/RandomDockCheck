package nl.frankkie.randomdockcheck;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

/**
 * Created by Gebruiker on 6-6-13.
 */
public class SettingsActivity extends PreferenceActivity {


    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        addPreferencesFromResource(R.xml.settings);
        try {
            // just in case, in een try-catch
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Apply Settings
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}