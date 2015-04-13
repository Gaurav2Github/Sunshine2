package com.gauravtproject.android.sunshine2.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gauravtproject.android.sunshine2.app.data.WeatherContract.LocationEntry;
import com.gauravtproject.android.sunshine2.app.data.WeatherContract.WeatherEntry;

/**
 * Created by Gaurav Tandon on 5/03/2015.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "weather.db";

    public WeatherDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY," +
                LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL, " +
                LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL);" ;

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +
        // Why AutoIncrement here, and not above?
        // Unique keys will be auto-generated in either case.  But for weather
        // forecasting, it's reasonable to assume the user will want information
        // for a certain date and all dates *following*, so the forecast data
        // should be sorted accordingly.
        WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

        // the ID of the location entry associated with this weather data
         WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, " +
         WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
         WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
         WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +

         WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
         WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +

         WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, " +
         WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
         WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
         WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, " +

         // Set up the location column as a foreign key to location table.
         " FOREIGN KEY (" + WeatherEntry.COLUMN_LOC_KEY + ") REFERENCES " +
         LocationEntry.TABLE_NAME + " (" + LocationEntry._ID + "), " +

         // To assure the application have just one weather entry per day
         // per location, it's created a UNIQUE constraint with REPLACE strategy
          " UNIQUE (" + WeatherEntry.COLUMN_DATE + ", " +
          WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_WEATHER_TABLE);

    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p/>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.

        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
