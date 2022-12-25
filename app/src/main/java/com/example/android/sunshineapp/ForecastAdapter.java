package com.example.android.sunshineapp;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.sunshineapp.data.SunshinePreferences;
import com.example.android.sunshineapp.data.WeatherContract;
import com.example.android.sunshineapp.utilities.SunshineDateUtils;
import com.example.android.sunshineapp.utilities.SunshineWeatherUtils;

import org.w3c.dom.Text;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;
    private final ForecastAdapterOnClickHandler mClickHandler;

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayLayout;

    public ForecastAdapter(Context mContext, ForecastAdapterOnClickHandler mClickHandler) {
        this.mContext = mContext;
        this.mClickHandler = mClickHandler;
        mUseTodayLayout = mContext.getResources().getBoolean(R.bool.use_today_layout);
    }

    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView dateTextView;
        final TextView descriptionTextView;
        final TextView highTempTextView;
        final TextView lowTempTextView;
        final ImageView weatherIcon;


        public ForecastAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            highTempTextView = itemView.findViewById(R.id.highTempTextView);
            lowTempTextView = itemView.findViewById(R.id.lowTempTextView);
            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
//          COMPLETED (37) Instead of passing the String for the clicked item, pass the date from the cursor
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }
    public interface ForecastAdapterOnClickHandler {
        void onClick(long date);
    }

    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        int layoutId;
        switch (viewType){
            case VIEW_TYPE_TODAY:
                layoutId = R.layout.forecast_item_today_layout;
                break;
            case VIEW_TYPE_FUTURE_DAY:
                layoutId = R.layout.forecast_item_layout;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId,parent,false);
        view.setFocusable(true);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false);
        holder.dateTextView.setText(dateString);

        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);

        String description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId);
        holder.descriptionTextView.setText(description);

        int imageIcon;
        
        int id = getItemViewType(position);
        switch (id){
            case VIEW_TYPE_TODAY:
                imageIcon = SunshineWeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);
                break;
            case VIEW_TYPE_FUTURE_DAY:
                imageIcon = SunshineWeatherUtils.getSmallArtResourceIdForWeatherCondition(weatherId);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + id);
        }
        holder.weatherIcon.setImageResource(imageIcon);

        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
        String highTemp = SunshineWeatherUtils.formatTemperature(mContext,highInCelsius);
        holder.highTempTextView.setText(highTemp);

        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
        String lowTemp = SunshineWeatherUtils.formatTemperature(mContext,lowInCelsius);
        holder.lowTempTextView.setText(lowTemp);

    }

    @Override
    public int getItemCount() {
        if(mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    public void swapCursor(Cursor cursor){
        mCursor = cursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return VIEW_TYPE_TODAY;
        }else{
            return VIEW_TYPE_FUTURE_DAY;
        }
    }
}
