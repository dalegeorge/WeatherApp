package com.groupk.weatherapp.ui.home;

import android.icu.util.Calendar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.groupk.weatherapp.R;
import com.groupk.weatherapp.util.APIKey;
import com.groupk.weatherapp.util.SharedPrefs;
import com.kwabenaberko.openweathermaplib.constants.Units;
import com.kwabenaberko.openweathermaplib.implementation.OpenWeatherMapHelper;
import com.kwabenaberko.openweathermaplib.implementation.callbacks.ThreeHourForecastCallback;
import com.kwabenaberko.openweathermaplib.models.threehourforecast.ThreeHourForecast;

// Created by Yajat
public class PredictionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prediction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // TextView for current city.
        TextView city = view.findViewById(R.id.city_name);

        // The TextViews for predictions.
        TextView day1 = view.findViewById(R.id.weather_day1);
        TextView day2 = view.findViewById(R.id.weather_day2);
        TextView day3 = view.findViewById(R.id.weather_day3);
        TextView day4 = view.findViewById(R.id.weather_day4);
        TextView day5 = view.findViewById(R.id.weather_day5);

        // Load from shared prefs in starting to avoid 2-3 seconds delay in fetching live data.
        city.setText(SharedPrefs.getPrefs(getContext()).getString("city", "Kamloops, CA"));
        day1.setText(SharedPrefs.getPrefs(getContext()).getString("prediction0", "Mon  " + "-15\u00B0C"));
        day2.setText(SharedPrefs.getPrefs(getContext()).getString("prediction1", "Tue  " + "-5\u00B0C"));
        day3.setText(SharedPrefs.getPrefs(getContext()).getString("prediction2", "Wed  " + "-4\u00B0C"));
        day4.setText(SharedPrefs.getPrefs(getContext()).getString("prediction3", "Thur  " + "-2\u00B0C"));
        day5.setText(SharedPrefs.getPrefs(getContext()).getString("prediction4", "Fri  " + "-8\u00B0C"));

        OpenWeatherMapHelper helper = new OpenWeatherMapHelper(APIKey.getKEY());
        helper.setUnits(Units.METRIC);
        helper.getThreeHourForecastByCityName("Kamloops", new ThreeHourForecastCallback() {
            @Override
            public void onSuccess(ThreeHourForecast threeHourForecast) {
                String cityText = threeHourForecast.getCity().getName() + ", " + threeHourForecast.getCity().getCountry();
                city.setText(cityText);

                // Store in shared prefs for cache.
                SharedPrefs.getPrefs(getContext()).edit().putString("city", cityText).apply();

                TextView[] days = {day1, day2, day3, day4, day5};
                for (int i = 0; i < threeHourForecast.getCnt(); i += 8) {
                    // The text for the current day prediction.
                    String text = getDay(i / 8) + threeHourForecast.getList().get(i).getMain().getTempMax() + " \u00B0C";

                    // Store in shared prefs for cache.
                    SharedPrefs.getPrefs(getContext()).edit().putString("prediction" + i / 8, text).apply();

                    // Set text for prediction.
                    days[i / 8].setText(text);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                // Error toast.
                Toast.makeText(getContext(), getResources().getString(R.string.weather_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get day from index.
    private String getDay(int index) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) + index + 1;
        if (day > 7)
            day -= 7;

        switch (day) {
            case 2:
                return "Mon  ";
            case 3:
                return "Tue  ";
            case 4:
                return "Wed  ";
            case 5:
                return "Thur  ";
            case 6:
                return "Fri  ";
            case 7:
                return "Sat  ";
            default:
                return "Sun  ";
        }
    }
}