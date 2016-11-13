package com.hhh.mausam;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Harsha on 11/13/16.
 */
public class ForecastFragment extends Fragment {

    String[] forecastList = {"Today - Sunny - 88/63", "Wednesday - Sunny - 76/60",
            "Thursday - Cloudy - 82/65", "Friday - Rainy - 85/66", "Saturday - Foggy - 84/66",
            "Sunday - Rainy - 88/68", "Monday - Sunny - 85/66"};

    public ForecastFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        List<String> forecastItems = new ArrayList<>(Arrays.asList(forecastList));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_forecast,
                R.id.list_item_forecast_textview, forecastItems);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(adapter);
        return rootView;    }
}
