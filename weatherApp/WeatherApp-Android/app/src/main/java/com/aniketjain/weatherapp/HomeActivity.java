package com.aniketjain.weatherapp;

import static com.aniketjain.weatherapp.network.InternetConnectivity.isInternetConnected;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aniketjain.weatherapp.adapter.DaysAdapter;
import com.aniketjain.weatherapp.databinding.ActivityHomeBinding;
import com.aniketjain.weatherapp.location.LocationCord;
import com.aniketjain.weatherapp.location.LocationManager;
import com.aniketjain.weatherapp.network.WeatherRepository;
import com.aniketjain.weatherapp.toast.Toaster;
import com.aniketjain.weatherapp.update.UpdateUI;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    private final int WEATHER_FORECAST_APP_UPDATE_REQ_CODE = 101; // for app update
    private static final int PERMISSION_CODE = 1; // for user location permission
    private static final int REQUEST_CODE_EXTRA_INPUT = 101;

    private ActivityHomeBinding binding;
    private WeatherRepository weatherRepository;
    private LocationManager locationManager;

    private String name, updated_at, description, temperature, min_temperature, max_temperature, pressure, wind_speed,
            humidity;
    private int condition;
    private long update_time, sunset, sunrise;
    private String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initHelpers();
        setupUI();
        checkUpdate();

        // getting data using internet connection
        getDataUsingNetwork();
    }

    private void initHelpers() {
        weatherRepository = new WeatherRepository(this);
        locationManager = new LocationManager(this);
    }

    private void setupUI() {
        setNavigationBarColor();
        setRefreshLayoutColor();
        setupListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_EXTRA_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> arrayList = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (arrayList != null && !arrayList.isEmpty()) {
                    binding.layout.cityEt.setText(arrayList.get(0).toUpperCase());
                    searchCity(binding.layout.cityEt.getText().toString());
                }
            }
        }
    }

    private void setNavigationBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.navBarColor));
        }
    }

    private void setUpDaysRecyclerView() {
        DaysAdapter daysAdapter = new DaysAdapter(this);
        binding.dayRv.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.dayRv.setAdapter(daysAdapter);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupListeners() {
        binding.layout.mainLayout.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(view);
            return false;
        });
        binding.layout.searchBarIv.setOnClickListener(view -> searchCity(binding.layout.cityEt.getText().toString()));
        binding.layout.searchBarIv.setOnTouchListener((view, motionEvent) -> {
            hideKeyboard(view);
            return false;
        });
        binding.layout.cityEt.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_GO) {
                searchCity(binding.layout.cityEt.getText().toString());
                hideKeyboard(textView);
                return true;
            }
            return false;
        });
        binding.layout.cityEt.setOnFocusChangeListener((view, b) -> {
            if (!b) {
                hideKeyboard(view);
            }
        });
        binding.mainRefreshLayout.setOnRefreshListener(() -> {
            checkConnection();
            Log.i("refresh", "Refresh Done.");
            binding.mainRefreshLayout.setRefreshing(false); // for the next time
        });
        // Mic Search
        binding.layout.micSearchId.setOnClickListener(view -> {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, REQUEST_CODE_EXTRA_INPUT);
            try {
                startActivityForResult(intent, REQUEST_CODE_EXTRA_INPUT);
            } catch (Exception e) {
                Log.d("Error Voice", "Mic Error:  " + e);
            }
        });
    }

    private void setRefreshLayoutColor() {
        binding.mainRefreshLayout.setProgressBackgroundColorSchemeColor(
                getResources().getColor(R.color.textColor));
        binding.mainRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.navBarColor));
    }

    private void searchCity(String cityName) {
        if (cityName == null || cityName.isEmpty()) {
            Toaster.errorToast(this, "Please enter the city name");
        } else {
            setLatitudeLongitudeUsingCity(cityName);
        }
    }

    private void getDataUsingNetwork() {
        locationManager.getLastLocation(this, new LocationManager.OnLocationListener() {
            @Override
            public void onLocationFound(Location location) {
                LocationCord.lat = String.valueOf(location.getLatitude());
                LocationCord.lon = String.valueOf(location.getLongitude());
                city = locationManager.getCityName(location);
                getTodayWeatherInfo(city);
            }

            @Override
            public void onLocationError(Exception e) {
                Log.e("Location", "Error getting location", e);
            }

            @Override
            public void onPermissionNeeded() {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },
                        PERMISSION_CODE);
            }
        });
    }

    private void setLatitudeLongitudeUsingCity(String cityName) {
        weatherRepository.getCityCoordinates(cityName, new WeatherRepository.OnCityCoordinatesListener() {
            @Override
            public void onSuccess(String lat, String lon) {
                LocationCord.lat = lat;
                LocationCord.lon = lon;
                getTodayWeatherInfo(cityName);
                binding.layout.cityEt.setText("");
            }

            @Override
            public void onError(Exception e) {
                Toaster.errorToast(HomeActivity.this, "Please enter the correct city name");
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void getTodayWeatherInfo(String name) {
        weatherRepository.getWeatherData(LocationCord.lat, LocationCord.lon,
                new WeatherRepository.OnWeatherDataListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            HomeActivity.this.name = name;
                            update_time = response.getJSONObject("current").getLong("dt");
                            updated_at = new SimpleDateFormat("EEEE hh:mm a", Locale.ENGLISH)
                                    .format(new Date(update_time * 1000));

                            condition = response.getJSONArray("daily").getJSONObject(0).getJSONArray("weather")
                                    .getJSONObject(0).getInt("id");
                            sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
                            sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
                            description = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0)
                                    .getString("main");

                            temperature = String
                                    .valueOf(Math.round(response.getJSONObject("current").getDouble("temp") - 273.15));
                            min_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0)
                                    .getJSONObject("temp").getDouble("min") - 273.15);
                            max_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0)
                                    .getJSONObject("temp").getDouble("max") - 273.15);
                            pressure = response.getJSONArray("daily").getJSONObject(0).getString("pressure");
                            wind_speed = response.getJSONArray("daily").getJSONObject(0).getString("wind_speed");
                            humidity = response.getJSONArray("daily").getJSONObject(0).getString("humidity");

                            updateUI();
                            hideProgressBar();
                            setUpDaysRecyclerView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toaster.errorToast(HomeActivity.this, "Error fetching weather data");
                    }
                });
        Log.i("json_req", "Day 0");
    }

    @SuppressLint("SetTextI18n")
    private void updateUI() {
        binding.layout.nameTv.setText(name);
        updated_at = translate(updated_at);
        binding.layout.updatedAtTv.setText(updated_at);
        binding.layout.conditionIv.setImageResource(
                getResources().getIdentifier(
                        UpdateUI.getIconID(condition, update_time, sunrise, sunset),
                        "drawable",
                        getPackageName()));
        binding.layout.conditionDescTv.setText(description);
        binding.layout.tempTv.setText(temperature + "°C");
        binding.layout.minTempTv.setText(min_temperature + "°C");
        binding.layout.maxTempTv.setText(max_temperature + "°C");
        binding.layout.pressureTv.setText(pressure + " mb");
        binding.layout.windTv.setText(wind_speed + " km/h");
        binding.layout.humidityTv.setText(humidity + "%");
    }

    private String translate(String dayToTranslate) {
        String[] dayToTranslateSplit = dayToTranslate.split(" ");
        dayToTranslateSplit[0] = UpdateUI.TranslateDay(dayToTranslateSplit[0].trim(), getApplicationContext());
        return dayToTranslateSplit[0].concat(" " + dayToTranslateSplit[1]);
    }

    private void hideProgressBar() {
        binding.progress.setVisibility(View.GONE);
        binding.layout.mainLayout.setVisibility(View.VISIBLE);
    }

    private void hideMainLayout() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.layout.mainLayout.setVisibility(View.GONE);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void checkConnection() {
        if (!isInternetConnected(this)) {
            hideMainLayout();
            Toaster.errorToast(this, "Please check your internet connection");
        } else {
            hideProgressBar();
            getDataUsingNetwork();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toaster.successToast(this, "Permission Granted");
                getDataUsingNetwork();
            } else {
                Toaster.errorToast(this, "Permission Denied");
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    private void checkUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(HomeActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, HomeActivity.this,
                            WEATHER_FORECAST_APP_UPDATE_REQ_CODE);
                } catch (IntentSender.SendIntentException exception) {
                    Toaster.errorToast(this, "Update Failed");
                }
            }
        });
    }

}