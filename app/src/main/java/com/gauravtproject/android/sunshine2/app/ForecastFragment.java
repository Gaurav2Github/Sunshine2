package com.gauravtproject.android.sunshine2.app;

/**
 * Created by Gaurav Tandon on 21/02/2015.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gauravtproject.android.sunshine2.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = ForecastFragment.class.getSimpleName();
    //private ArrayAdapter<String> mForecastAdapter;
    private ForecastAdapter mForecastAdapter;
    private final static int FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.LocationEntry.COLUMN_COORD_LAT,
            WeatherContract.LocationEntry.COLUMN_COORD_LONG
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_LOCATION_SETTING = 5;
    static final int COL_WEATHER_CONDITION_ID = 6;
    static final int COL_COORD_LAT = 7;
    static final int COL_COORD_LONG = 8;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if ( id == R.id.action_refresh){
            updateWeather();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the Fragment is visible to the user.  This is generally
     * tied to {@link com.gauravtproject.android.sunshine2.app.ForecastFragment#onStart() Activity.onStart} of the containing
     * Activity's lifecycle.
     */
//    @Override
//    public void onStart() {
//        super.onStart();
//        updateWeather();
//    }



    private void updateWeather() {
//        //FetchWeatherTask weatherTask = new FetchWeatherTask();
//        FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity(),mForecastAdapter);
          FetchWeatherTask weatherTask = new FetchWeatherTask(getActivity());
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        //getActivity().getSharedPreferences(getString(R.xml.prerf_general), Context.MODE_PRIVATE);
//        String location = prefs.getString(getString(R.string.pref_location_key), getString(R.string.pref_location_default));
//        Log.i(LOG_TAG,"Location value : " + location );
//        String units = prefs.getString(getString(R.string.pref_units_key), getString(R.string.pref_units_default));
//        Log.i(LOG_TAG,"Units value : " + units );
//        //AsyncTask<String, Void, String[]> strList = weatherTask.execute("94043");
//        AsyncTask<String, Void, String[]> strList = weatherTask.execute(location,units);
        String location = Utility.getPreferredLocation(getActivity());
        weatherTask.execute(location);
//        Log.i(LOG_TAG, "JSON formatted value: " + strList.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // The CursorAdapter will take data from our cursor and populate the ListView
        mForecastAdapter = new ForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adpater to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                //Cursor adapter returns a cursor at the correct position for getItem(), or null

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null){
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                    startActivity(intent);
                }
            }
        });




//        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
////        ArrayList<String> weatherStringList = new ArrayList<String>();
////        weatherStringList.add("Today - Sunny - 88 / 63");
////        weatherStringList.add("Tomorrow - Fogy - 70 / 46");
////        weatherStringList.add("Weds - Cloudy - 72 / 63");
////        weatherStringList.add("Thurs - Rainy - 64 / 51");
////        weatherStringList.add("Fri - Fogy - 70 / 46");
////        weatherStringList.add("Sat - Sunny - 76 / 68");
//        mForecastAdapter = new ArrayAdapter<String>(
//                getActivity(), // current context (This activity)
//                R.layout.list_item_forecast, // The name of layout id.
//                R.id.list_item_forecast_textview, // The id of the text view to populate
//                new ArrayList<String>());
//
//        // Get a reference of ListView , and attach this adapter to it
//        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
//
//        listView.setAdapter(mForecastAdapter);
//
//        //Setting listView on item click listener.
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                int length = Toast.LENGTH_SHORT;
//                String forecast = mForecastAdapter.getItem(position);
////                Context context = getActivity().getBaseContext();
////                Toast toast = Toast.makeText(context, value, length);
////                toast.show();
//                Intent intent = new Intent(getActivity(),DetailActivity.class);
//                intent.putExtra(Intent.EXTRA_TEXT, forecast);
//                intent.setType(HTTP.PLAIN_TEXT_TYPE);
//                getActivity().startActivity(intent);
//
//            }
//        });




        return rootView;
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order by: Ascending by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(),
                weatherLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(android.os.Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.
     * <p/>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p/>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link android.database.Cursor}
     * and you place it in a {@link android.widget.CursorAdapter}, use
     * the {@link android.widget.CursorAdapter#CursorAdapter(android.content.Context,
     * android.database.Cursor, int)} constructor <em>without</em> passing
     * in either {@link android.widget.CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link android.widget.CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link android.database.Cursor} from a {@link android.content.CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link android.widget.CursorAdapter}, you should use the
     * {@link android.widget.CursorAdapter#swapCursor(android.database.Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mForecastAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mForecastAdapter.swapCursor(null);
    }

    /**
     *
     */
    public void onLocationChanged(){
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);

    }

//    private class FetchWeatherTask extends AsyncTask<String, Void, String[]> {
//
//        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();
//
//        /**
//         * <p>Runs on the UI thread after {@link #doInBackground}. The
//         * specified result is the value returned by {@link #doInBackground}.</p>
//         * <p/>
//         * <p>This method won't be invoked if the task was cancelled.</p>
//         *
//         * @param strings The result of the operation computed by {@link #doInBackground}.
//         * @see #onPreExecute
//         * @see #doInBackground
//         * @see #onCancelled(Object)
//         */
//        @Override
//        protected void onPostExecute(String[] strings) {
//            if(strings!=null){
//                mForecastAdapter.clear();
//                for(String string :strings){
//                    mForecastAdapter.add(string);
//                }
//            }
//           // mForecastAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast,R.id.list_item_forecast_textview,strings);
//
//            super.onPostExecute(strings);
//        }
//
//        /**
//         * Override this method to perform a computation on a background thread. The
//         * specified parameters are the parameters passed to {@link #execute}
//         * by the caller of this task.
//         * <p/>
//         * This method can call {@link #publishProgress} to publish updates
//         * on the UI thread.
//         *
//         * @param params The parameters of the task.
//         * @return A result, defined by the subclass of this task.
//         * @see #onPreExecute()
//         * @see #onPostExecute
//         * @see #publishProgress
//         */
//        @Override
//        protected String[] doInBackground(String... params) {
//
//            //Managing API Request/Response.
//
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            String forecastJsonStr = null;
//
//            try{
//                String location = null;
//                String units = null;
//                if( params.length >= 1 && params[0]!=null ){
//                    location = params[0];
//                }
//                if( params.length >= 2 && params[1] != null ){
//                    units = params[1];
//                }
//                // Construct the URL for  the OpenWeatherMap query,
//                // so they can be closed in the final block.
//                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
//                Uri.Builder builder = new Uri.Builder();
//                builder.scheme("http");
//                builder.authority("api.openweathermap.org");
//                builder.path("/data/2.5/forecast/daily");
//                //builder.appendPath("http://api.openweathermap.org/data/2.5/forecast/daily");
//                builder.appendQueryParameter("q", location);
//                builder.appendQueryParameter("mode", "json");
//                builder.appendQueryParameter("units", units);
//                builder.appendQueryParameter("cnt", "7");
//                builder.build();
//                URL url = new URL(builder.build().toString());
//                Log.i("Url: ", url.getPath().toString());
//                //Create the request to OpenWeatherMap, and open the connection.
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                //Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if(inputStream == null){
//                    //Nothing to be done;
//                    return null;
//                }
//
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//                String line;
//
//                while((line = reader.readLine()) != null){
//                    buffer.append(line + "\n");
//                }
//                if(buffer.length() == 0){
//                    //do nothing.
//                    return  null;
//                }
//                forecastJsonStr = buffer.toString();
//
//
//
//            }catch (IOException ioe){
//                Log.e(LOG_TAG, "Error", ioe);
//                //if the code didn't successfully  get the weather data , there is no point
//                // attempting to parse it.
//                return null;
//            }
//            catch(Exception e){
//                Log.e(LOG_TAG, "Error", e);
//                //if the code didn't successfully  get the weather data , there is no point
//                // attempting to parse it.
//                return null;
//            }
//            finally {
//                if(urlConnection != null){
//                    urlConnection.disconnect();
//                }
//                if(reader!=null){
//                    try{
//                        reader.close();
//                    }catch (IOException ioe)
//                    {
//                        Log.e(LOG_TAG, "Error closing stream", ioe);
//                    }
//                }
//            }
//            if(forecastJsonStr!=null)
//                Log.i("JSON LOG", forecastJsonStr);
//
//            try{
//                return getWeatherDataFromJson(forecastJsonStr, 7);
//            }catch (Exception e){
//                e.printStackTrace();
//                Log.e(LOG_TAG, e.getMessage());
//            }
//
//            return null;
//
//
//            //return null;
//        }
//
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
//        * so for convenience we're breaking it out into its own method now.
//        */
//        private String getReadableDateString(long time){
//            // Because the API returns a unix timestamp (measured in seconds),
//            // it must be converted to milliseconds in order to be converted to valid date.
//            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
//            return shortenedDateFormat.format(time);
//        }
//
//        /**
//         * Prepare the weather high/lows for presentation.
//         */
//        private String formatHighLows(double high, double low) {
//            // For presentation, assume the user doesn't care about tenths of a degree.
//            long roundedHigh = Math.round(high);
//            long roundedLow = Math.round(low);
//
//            String highLowStr = roundedHigh + "/" + roundedLow;
//            return highLowStr;
//        }
//
//        /**
//         * Take the String representing the complete forecast in JSON Format and
//         * pull out the data we need to construct the Strings needed for the wireframes.
//         *
//         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
//         * into an Object hierarchy for us.
//         */
//        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
//                throws JSONException {
//
//            // These are the names of the JSON objects that need to be extracted.
//            final String OWM_LIST = "list";
//            final String OWM_WEATHER = "weather";
//            final String OWM_TEMPERATURE = "temp";
//            final String OWM_MAX = "max";
//            final String OWM_MIN = "min";
//            final String OWM_DESCRIPTION = "main";
//
//            JSONObject forecastJson = new JSONObject(forecastJsonStr);
//            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);
//
//            // OWM returns daily forecasts based upon the local time of the city that is being
//            // asked for, which means that we need to know the GMT offset to translate this data
//            // properly.
//
//            // Since this data is also sent in-order and the first day is always the
//            // current day, we're going to take advantage of that to get a nice
//            // normalized UTC date for all of our weather.
//
//            Time dayTime = new Time();
//            dayTime.setToNow();
//
//            // we start at the day returned by local time. Otherwise this is a mess.
//            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);
//
//            // now we work exclusively in UTC
//            dayTime = new Time();
//
//            String[] resultStrs = new String[numDays];
//            for(int i = 0; i < weatherArray.length(); i++) {
//                // For now, using the format "Day, description, hi/low"
//                String day;
//                String description;
//                String highAndLow;
//
//                // Get the JSON object representing the day
//                JSONObject dayForecast = weatherArray.getJSONObject(i);
//
//                // The date/time is returned as a long.  We need to convert that
//                // into something human-readable, since most people won't read "1400356800" as
//                // "this saturday".
//                long dateTime;
//                // Cheating to convert this to UTC time, which is what we want anyhow
//                dateTime = dayTime.setJulianDay(julianStartDay+i);
//                day = getReadableDateString(dateTime);
//
//                // description is in a child array called "weather", which is 1 element long.
//                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
//                description = weatherObject.getString(OWM_DESCRIPTION);
//
//                // Temperatures are in a child object called "temp".  Try not to name variables
//                // "temp" when working with temperature.  It confuses everybody.
//                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
//                double high = temperatureObject.getDouble(OWM_MAX);
//                double low = temperatureObject.getDouble(OWM_MIN);
//
//                highAndLow = formatHighLows(high, low);
//                resultStrs[i] = day + " - " + description + " - " + highAndLow;
//            }
//
//            for (String s : resultStrs) {
//                Log.v(LOG_TAG, "Forecast entry: " + s);
//            }
//            return resultStrs;
//
//        }
//
//    }




}



