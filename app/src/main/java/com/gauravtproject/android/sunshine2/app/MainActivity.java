package com.gauravtproject.android.sunshine2.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private String mLocation;
    private final String FORECAST_FRAGMENT_TAG ="FFTAG";

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "****onStop() executed..****");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "****onCreate() executed..****");
        mLocation = Utility.getPreferredLocation(this);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(),FORECAST_FRAGMENT_TAG)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "****onPause() executed..****");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "****onDestroy() executed..****");
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "****onResume() executed..****");

        String location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager.
        if(location!=null && !location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment) getSupportFragmentManager().findFragmentByTag(FORECAST_FRAGMENT_TAG);
            if(null!=ff){
                ff.onLocationChanged();
            }
            mLocation=location;
        }

    }

    /**
     * Dispatch onStart() to all fragments.  Ensure any created loaders are
     * now started.
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "****onStart() executed..****");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
        }else if (id == R.id.action_location) {
            String location = null;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            location = prefs.getString(getString(R.string.pref_location_key),getString(R.string.pref_location_default));
            Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                    .appendQueryParameter("q", location)
                    .build();
            Log.i(LOG_TAG , "Location Uri: " + geoLocation);
            showMap(geoLocation);
        }

        return super.onOptionsItemSelected(item);
    }


    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}

