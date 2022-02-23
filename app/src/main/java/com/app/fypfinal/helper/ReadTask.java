package com.app.fypfinal.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.app.fypfinal.Info.Info;
import com.app.fypfinal.activities.PostmanMaps;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ReadTask extends AsyncTask<String, Void, String> implements Info {

    GoogleMap googleMap;

    public ReadTask(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try {
            HttpConnection http = new HttpConnection();
            data = http.readUrl(url[0]);
            Log.i(TAG, "doInBackground: " + data);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        new ParserTask(googleMap).execute(result);
    }
}

class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> implements Info {

    GoogleMap googleMap;

    public ParserTask(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    protected List<List<HashMap<String, String>>> doInBackground(
            String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            PathJSONParser parser = new PathJSONParser();
            Log.i(TAG, "doInBackground: ");
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
        ArrayList<LatLng> points = null;
        PolylineOptions polyLineOptions = null;
        Log.i(TAG, "onPostExecute: " + routes.size());
        // traversing through routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<>();
            polyLineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = routes.get(i);

            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                LatLng position = new LatLng(lat, lng);
                Log.i(TAG, "onPostExecute: " + lat + lng);
                points.add(position);
            }

            polyLineOptions.addAll(points);
            polyLineOptions.width(10);
            polyLineOptions.color(PostmanMaps.lineColor);
        }
        if (polyLineOptions != null) {
            PostmanMaps.polyline = googleMap.addPolyline(polyLineOptions);
        } else Log.i(TAG, "onPostExecute: null");
    }
}