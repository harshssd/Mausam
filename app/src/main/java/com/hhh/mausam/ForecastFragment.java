package com.hhh.mausam;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Harsha on 11/13/16.
 */
public class ForecastFragment extends Fragment {

    String[] forecastList;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This statement is required in order to indicate that the fragment needs to call its
        // lifecycle methods to handle the menu options.
        setHasOptionsMenu(true);
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
        } catch (InterruptedException | ExecutionException e) {
            forecastList = null;
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        final List<String> forecastItems = new ArrayList<>(Arrays.asList(forecastList));
        final ArrayAdapter<String> forecastAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, forecastItems);
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
            case R.id.action_refresh:
                Toast.makeText(getContext(), "Click on Refresh.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
