package com.hhh.mausam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harsha on 11/13/16.
 */
public class ForecastFragment extends Fragment {

    private String[] forecastList;
    private ArrayAdapter<String> forecastAdapter;

    private static String LOG_TAG = ForecastFragment.class.getSimpleName();

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This statement is required in order to indicate that the fragment needs to call its
        // lifecycle methods to handle the menu options.
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        forecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, new ArrayList<String>());
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                displayForecastDetail(forecastAdapter.getItem(position));
            }
        });
        return rootView;
    }

    private void updateWeather() {
        FetchWeatherTask fetchWeatherTask = new FetchWeatherTask();
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String locationPref = sharedPref.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));
            // Data is fetched in Celsius by default.
            // If user prefers to see in Fahrenheit, convert the values here.
            // We do this rather than fetching in Fahrenheit so that the user can
            // change this option without us having to re-fetch the data once
            // we start storing the values in a database.
            String unitType = sharedPref.getString(getString(R.string.pref_units_key),
                    getString(R.string.pref_units_metric));
            forecastList = fetchWeatherTask.execute(locationPref, unitType).get();
            if(forecastList != null) {
                forecastAdapter.clear();
                for(String forecast: forecastList) {
                    forecastAdapter.add(forecast);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            forecastList = null;
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    /**
     * This method is invoked when there is a request to display the forecast detail.
     */
    public void displayForecastDetail(String forecastDetail) {
        Intent detailIntent = new Intent(getActivity(), ForecastDetailActivity.class);
        detailIntent.putExtra(Intent.EXTRA_TEXT, forecastDetail);
        startActivity(detailIntent);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragement, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.action_map:
                openPreferredLocationInMap();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openPreferredLocationInMap() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String location = sharedPrefs.getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));

        //Using the Uri scheme for showing the location by opening a map intent. Same
        //intent can be used to show the contacts, open email or invoking the dialer.
        //https://developer.android.com/reference/android/content/Intent.html#ACTION_VIEW
        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location).build();
        Intent geoLocationIntent = new Intent(Intent.ACTION_VIEW);
        geoLocationIntent.setData(geoLocation);
        if(geoLocationIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(geoLocationIntent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + location + ", no receiving apps installed!");
        }
    }
}
