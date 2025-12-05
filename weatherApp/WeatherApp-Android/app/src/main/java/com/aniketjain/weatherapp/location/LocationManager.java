package com.aniketjain.weatherapp.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.List;
import java.util.Locale;

public class LocationManager {
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;

    public LocationManager(Context context) {
        this.context = context;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void getLastLocation(Activity activity, final OnLocationListener listener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            listener.onPermissionNeeded();
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
            if (location != null) {
                listener.onLocationFound(location);
            } else {
                listener.onLocationError(new Exception("Location is null"));
            }
        });
    }

    public String getCityName(Location location) {
        String city = "";
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                city = addresses.get(0).getLocality();
                Log.d("city", city);
            }
        } catch (Exception e) {
            Log.d("city", "Error to find the city: " + e.getMessage());
        }
        return city;
    }

    public interface OnLocationListener {
        void onLocationFound(Location location);
        void onLocationError(Exception e);
        void onPermissionNeeded();
    }
}
