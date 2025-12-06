package com.aniketjain.weatherapp.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.aniketjain.weatherapp.location.LocationCord;
import com.aniketjain.weatherapp.url.URL;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherRepository {
    private final Context context;
    private final RequestQueue requestQueue;

    public WeatherRepository(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    public void getCityCoordinates(String cityName, final OnCityCoordinatesListener listener) {
        URL.setCity_url(cityName);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL.getCity_url(), null,
                response -> {
                    try {
                        String lat = response.getJSONObject("coord").getString("lat");
                        String lon = response.getJSONObject("coord").getString("lon");
                        listener.onSuccess(lat, lon);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onError(e);
                    }
                }, listener::onError);
        requestQueue.add(jsonObjectRequest);
    }

    public void getWeatherData(String lat, String lon, final OnWeatherDataListener listener) {
        // Construct URL directly or use URL class if modified to accept params
        // For now, assuming URL class is still static-ish or we use a new instance
        // But better to construct it here to be clean
        String link = "https://api.openweathermap.org/data/2.5/onecall?exclude=minutely&lat="
                + lat + "&lon=" + lon + "&appid=" + LocationCord.API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, link, null, response -> {
            try {
                listener.onSuccess(response);
            } catch (Exception e) {
                e.printStackTrace();
                listener.onError(e);
            }
        }, listener::onError);
        requestQueue.add(jsonObjectRequest);
        Log.i("json_req", "Weather Data Request Sent");
    }

    public interface OnCityCoordinatesListener {
        void onSuccess(String lat, String lon);

        void onError(Exception e);
    }

    public interface OnWeatherDataListener {
        void onSuccess(JSONObject response);

        void onError(Exception e);
    }
}
