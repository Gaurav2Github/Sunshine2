package com.gauravtproject.android.sunshine2.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gauravtproject.android.sunshine2.app.data.WeatherContract;

/**
 * Created by Gaurav Tandon on 23/03/2015.
 *
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 *
 */
public class ForecastAdapter extends CursorAdapter {

    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DATE = 1;

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        return (position==0) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DATE;
    }

    @Override
    public int getViewTypeCount() {
        //return super.getViewTypeCount();
        return 2;
    }

    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     * @param flags   Flags used to determine the behavior of the adapter; may
     *                be any combination of {@link #FLAG_AUTO_REQUERY} and
     *                {@link #FLAG_REGISTER_CONTENT_OBSERVER}.
     */
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
        int idx_max_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);

        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     * Views are reused as needed.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // View view = LayoutInflater.from(context).inflate(R.layout.list_item_forecast, parent, false);
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // Determining layout from viewType.
        if(viewType==VIEW_TYPE_TODAY){
            layoutId = R.layout.list_item_forecast_today;
        }else if (viewType==VIEW_TYPE_FUTURE_DATE){
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     * This is where we fill-in the views with the contents of the cursor.
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //TextView textView = (TextView)view;
        //textView.setText(convertCursorRowToUXFormat(cursor));

        ViewHolder viewHolder = (ViewHolder)view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);

        // Use placeholder image for now
//        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
//        iconView.setImageResource(R.drawable.ic_launcher);
        viewHolder.iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        String formattedDateString = Utility.getFriendlyDayString(context, date);
//        TextView dateTextView = (TextView) view.findViewById(R.id.list_item_date_textview);
//        dateTextView.setText(formattedDateString);
        viewHolder.dateView.setText(formattedDateString);

        // Read weather forecast from cursor
        String weatherForecastTextString = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
//        TextView weatherForecastDescTextView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
//        weatherForecastDescTextView.setText(weatherForecastTextString);
        viewHolder.descriptionView.setText(weatherForecastTextString);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
//        TextView highView = (TextView) view.findViewById(R.id.list_item_high_textview);
//        highView.setText(Utility.formatTemperature(high, isMetric));
        viewHolder.highTempTextView.setText(Utility.formatTemperature(high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
//        TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
//        lowView.setText(Utility.formatTemperature(low, isMetric));
        viewHolder.lowTempTextView.setText(Utility.formatTemperature(low, isMetric));


    }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView lowTempTextView;
        public final TextView highTempTextView;

        public ViewHolder(View view){
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            lowTempTextView = (TextView) view.findViewById(R.id.list_item_low_textview);
            highTempTextView = (TextView) view.findViewById(R.id.list_item_high_textview);
        }
    }

}
